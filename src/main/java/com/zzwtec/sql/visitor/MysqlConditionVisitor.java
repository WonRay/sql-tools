package com.zzwtec.sql.visitor;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.zzwtec.sql.info.BracketsInfo;
import com.zzwtec.sql.info.OperatorInfo;
import com.zzwtec.sql.info.WhereInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
/**
 * 用于过滤条sql语句中的条件
 * @author 毛文超
 * */
public class MysqlConditionVisitor extends MySqlOutputVisitor {
    //括号栈
    private LinkedBlockingDeque<BracketsInfo> bracketsInfos = new LinkedBlockingDeque<>(10);
    private LinkedBlockingDeque<WhereInfo> whereInfos = new LinkedBlockingDeque<>(5);
 //   private LinkedBlockingDeque<OperatorInfo> operatorInfos = new LinkedBlockingDeque<>(10);
    private Map<String,OperatorInfo> operatorInfoMpa = new HashMap<>();
    private List<String> condition;//条件字段集合

    public MysqlConditionVisitor(List<String> condition,Appendable appendable) {
        super(appendable);
        this.condition = condition;
    }

    @Override
    public boolean visit(SQLSelectQueryBlock select) {
        if (select instanceof MySqlSelectQueryBlock) {
            return visit((MySqlSelectQueryBlock) select);
        }

        return super.visit(select);

    }

    @Override
    public boolean visit(MySqlSelectQueryBlock x) {
        final boolean bracket = x.isBracket();
        if (bracket) {
            print('(');
        }

        if ((!isParameterized()) && isPrettyFormat() && x.hasBeforeComment()) {
            printlnComments(x.getBeforeCommentsDirect());
        }

        print0(ucase ? "SELECT " : "select ");

        for (int i = 0, size = x.getHintsSize(); i < size; ++i) {
            SQLCommentHint hint = x.getHints().get(i);
            hint.accept(this);
            print(' ');
        }

        final int distionOption = x.getDistionOption();
        if (SQLSetQuantifier.ALL == distionOption) {
            print0(ucase ? "ALL " : "all ");
        } else if (SQLSetQuantifier.DISTINCT == distionOption) {
            print0(ucase ? "DISTINCT " : "distinct ");
        } else if (SQLSetQuantifier.DISTINCTROW == distionOption) {
            print0(ucase ? "DISTINCTROW " : "distinctrow ");
        }

        if (x.isHignPriority()) {
            print0(ucase ? "HIGH_PRIORITY " : "high_priority ");
        }

        if (x.isStraightJoin()) {
            print0(ucase ? "STRAIGHT_JOIN " : "straight_join ");
        }

        if (x.isSmallResult()) {
            print0(ucase ? "SQL_SMALL_RESULT " : "sql_small_result ");
        }

        if (x.isBigResult()) {
            print0(ucase ? "SQL_BIG_RESULT " : "sql_big_result ");
        }

        if (x.isBufferResult()) {
            print0(ucase ? "SQL_BUFFER_RESULT " : "sql_buffer_result ");
        }

        if (x.getCache() != null) {
            if (x.getCache()) {
                print0(ucase ? "SQL_CACHE " : "sql_cache ");
            } else {
                print0(ucase ? "SQL_NO_CACHE " : "sql_no_cache ");
            }
        }

        if (x.isCalcFoundRows()) {
            print0(ucase ? "SQL_CALC_FOUND_ROWS " : "sql_calc_found_rows ");
        }

        printSelectList(x.getSelectList());

        SQLExprTableSource into = x.getInto();
        if (into != null) {
            println();
            print0(ucase ? "INTO " : "into ");
            printTableSource(into);
        }

        SQLTableSource from = x.getFrom();
        if (from != null) {
            println();
            print0(ucase ? "FROM " : "from ");
            printTableSource(from);
        }

        //重点 ,解析where条件
        SQLExpr where = x.getWhere();
        if (where != null) {
            println();
            WhereInfo whereInfo = new WhereInfo();//记录下where 关键字的信息
            whereInfo.setWhereStartIndex(getAppendLength() - 1);//记录where关键字的开始索引
            print0(ucase ? "WHERE " : "where ");
            whereInfo.setWhereEndIndex(getAppendLength() - 1);//记录where关键字的结束索引
            whereInfos.push(whereInfo);//把where关键字信息压入栈
            printExpr(where);//解析where条件表达式
        }



        printHierarchical(x);

        SQLSelectGroupByClause groupBy = x.getGroupBy();
        if (groupBy != null) {
            println();
            visit(groupBy);
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            println();
            orderBy.accept(this);
        }

        SQLLimit limit = x.getLimit();
        if (limit != null) {
            println();
            limit.accept(this);
        }

        SQLName procedureName = x.getProcedureName();
        if (procedureName != null) {
            print0(ucase ? " PROCEDURE " : " procedure ");
            procedureName.accept(this);
            if (!x.getProcedureArgumentList().isEmpty()) {
                print('(');
                printAndAccept(x.getProcedureArgumentList(), ", ");
                print(')');
            }
        }

        if (x.isForUpdate()) {
            println();
            print0(ucase ? "FOR UPDATE" : "for update");
            if (x.isNoWait()) {
                print0(ucase ? " NOWAIT" : " nowait");
            } else if (x.getWaitTime() != null) {
                print0(ucase ? " WAIT " : " wait ");
                x.getWaitTime().accept(this);
            }
        }

        if (x.isLockInShareMode()) {
            println();
            print0(ucase ? "LOCK IN SHARE MODE" : "lock in share mode");
        }

        if (bracket) {
            print(')');
        }

        if(!whereInfos.isEmpty()){
            WhereInfo whereInfo = whereInfos.pop();
            if(whereInfo != null){
                boolean effectiveCondition = whereInfo.isEffectiveCondition();
                if(!effectiveCondition){//如果有效条件为0的话,删除where关键字
                    int whereStartIndex = whereInfo.getWhereStartIndex();
                    int whereEndIndex = whereInfo.getWhereEndIndex();
                    deleteStr(whereStartIndex,whereEndIndex + 1);
                }
            }
        }

        return false;
    }

    //条件解析

    public boolean visit(SQLBinaryOpExpr x){
        SQLBinaryOperator operator = x.getOperator();
        if (this.parameterized
                && operator == SQLBinaryOperator.BooleanOr) {
            x = SQLBinaryOpExpr.merge(this, x);

            operator = x.getOperator();
        }
        if( !(operator == SQLBinaryOperator.BooleanAnd || operator == SQLBinaryOperator.BooleanOr)){//这里一般都是单独的表达式,需要进行单独的处理
            if(x.getParent() instanceof SQLJoinTableSource){
                print0(" "+SQLUtils.toSQLString(x) + "");
            }

            return false;
        }

        boolean bracket = x.isBracket();//判断表达式是否有括号

        if(bracket){//如果有括号添加括号,并且记录括号的信息
            print0("( ");
            BracketsInfo bracketsInfo = new BracketsInfo();//括号信息对象
            bracketsInfo.setLeftBracketsIndex(getAppendLength() - 1);//记录左括号开始位置索引
            bracketsInfos.push(bracketsInfo);//压入堆栈
        }



        SQLExpr left = x.getLeft();
        SQLExpr right = x.getRight();

        //简单的解释一下,如果当前左边的表达式中的连接符跟当前的表达式的连接符一样的话,就把左边的表达式中的右边的表达式放入在group list中去,持续循环下去,直到左边的表达式中的连接符
        //与他的父表达式的连接符不一致位置,这样做的目的是为了找出与父表达式的相同连接符的表达式,也相当于是个拆分吧.达到从左到右的一个解析的顺序的目的
        /*for (;;) {
            if (left instanceof SQLBinaryOpExpr && ((SQLBinaryOpExpr) left).getOperator() == operator) {
                SQLBinaryOpExpr binaryLeft = (SQLBinaryOpExpr) left;
                groupList.add(binaryLeft.getRight());
                left = binaryLeft.getLeft();
            } else {
                groupList.add(left);
                break;
            }
        }*/

        OperatorInfo operatorInfoLeft = new OperatorInfo();//创建连接符信息
        operatorInfoMpa.put(left.getParent().toString(),operatorInfoLeft);
        visitBinaryExpr(left,false);//向左转
        if(operatorInfoLeft.isLeftCondition()){//如果左边的表达式成立才会添加连接符,但是不保证后面不删除,因为要考虑到右边的表达式
            operatorInfoLeft.setOperatorStartIndex(getAppendLength() - 1);
            printOperator(operator);//表达式连接符
            operatorInfoLeft.setOperatorEndIndex(getAppendLength() - 1);
        }

        visitBinaryExpr(right,true);//向右转

        if(!bracketsInfos.isEmpty()){
            BracketsInfo bracketsInfo = bracketsInfos.pop();
            if(bracketsInfo != null){
                boolean effectiveCondition = bracketsInfo.isEffectiveCondition();
                if(effectiveCondition){
                    print0(" ) ");
                }else {
                    int leftBracketsIndex = bracketsInfo.getLeftBracketsIndex();
                    deleteStr(leftBracketsIndex,leftBracketsIndex+1);
                }
            }
        }

        if(!operatorInfoMpa.isEmpty()){
            OperatorInfo operatorInfo = operatorInfoMpa.get(right.getParent().toString());
            if(operatorInfo != null && (operatorInfo.getOperatorStartIndex() != 0 || operatorInfo.getOperatorEndIndex() != 0 )){
                boolean ok = operatorInfo.isOk();
                if(!ok){//如果两边的表达式不成立的话,那么就要删除中间所添加的连接符
                    int operatorStartIndex = operatorInfo.getOperatorStartIndex();
                    int operatorEndIndex = operatorInfo.getOperatorEndIndex();
                    deleteStr(operatorStartIndex,operatorEndIndex + 1);
                }
            }
        }

        return false;
    }



    private int getAppendLength(){
        if (appender instanceof StringBuffer){

            return ((StringBuffer) appender).length();

        }else if(appender instanceof StringBuilder){


            return ((StringBuilder) appender).length();
        }

        return 0;
    }

    private void deleteStr(int startIndex,int endIndex){
        if(appender instanceof StringBuffer){
            ((StringBuffer) appender).delete(startIndex, endIndex);//删除目标字符
        }else if(appender instanceof StringBuilder){//同上
           ((StringBuilder) appender).delete(startIndex, endIndex);
        }

    }

    @Override
    public boolean visit(SQLInListExpr x) {
        SQLExpr sqlExpr = x.getExpr();
        String sqlExprValue = SQLUtils.toSQLString(sqlExpr);
        boolean contains = condition.contains(sqlExprValue);//判断是否包涵
        if(contains){
            print0(" " + SQLUtils.toSQLString(x) + " ");
            WhereInfo whereInfo = whereInfos.peek();//获取where信息
            if(whereInfo != null){
                whereInfo.addConditionCount();//计数器+1
            }
        }

        return false;
    }

    //解析表达式
    private void visitBinaryExpr(SQLExpr sqlExpr,boolean isRight){
        if(sqlExpr instanceof SQLBinaryOpExpr  && ((SQLBinaryOpExpr) sqlExpr).getOperator().isRelational()){//判断是否是普通的等式,如果不是就继续迭代
            SQLExpr conditionName = ((SQLBinaryOpExpr) sqlExpr).getLeft();//获取表达式左边的条件名称
            SQLExpr right = ((SQLBinaryOpExpr) sqlExpr).getRight();
            if(right instanceof SQLNumericLiteralExpr){//判断右边的值是不是数字
                handCondition(sqlExpr,isRight);
                return;
            }else if(right instanceof SQLCharExpr){//判断右边的值是不是字符串
                String rightValue = right.toString();
                boolean contains = rightValue.contains("?");
                if(!contains){
                    handCondition(sqlExpr,isRight);
                    return;
                }
            }else if(right instanceof SQLPropertyExpr){
                handCondition(sqlExpr,isRight);
                return;
            }


            boolean contains = condition.contains(SQLUtils.toSQLString(conditionName));//在条件字段中判断是否包涵,如果不包含,就剔除该条件,当然不是修ast
            if(contains){
                if(((SQLBinaryOpExpr) sqlExpr).getRight() instanceof SQLQueryExpr){
                    print0(((SQLBinaryOpExpr) sqlExpr).getLeft().toString() + " ");
                    print0(((SQLBinaryOpExpr) sqlExpr).getOperator().name);
                    print0(" ( ");
                    SQLSelect subQuery = ((SQLQueryExpr) ((SQLBinaryOpExpr) sqlExpr).getRight()).subQuery;
                    visit(subQuery);
                    print0(" )");
                }else{
                    handCondition(sqlExpr,isRight);
                }

            }
        }else if(sqlExpr instanceof SQLInListExpr){//判断是否是in
            String conditionName = ((SQLInListExpr) sqlExpr).getExpr().toString();//获取in的左边的名称
            boolean contains = condition.contains(conditionName);//在条件字段中判断是否包涵,如果不包含,就剔除该条件,当然不是修ast
            if(contains){
                handCondition(sqlExpr,isRight);
            }

        }else if (sqlExpr instanceof SQLBetweenExpr){//判断是否是between
            SQLExpr conditionName = ((SQLBetweenExpr) sqlExpr).getTestExpr();
            boolean contains = condition.contains(conditionName.toString());//在条件字段中判断是否包涵,如果不包含,就剔除该条件,当然不是修ast
            if(contains){
                handCondition(sqlExpr,isRight);
            }

        }else if (sqlExpr instanceof SQLInSubQueryExpr){//判断是否是in里面的子查询
            SQLExpr conditionName = ((SQLInSubQueryExpr) sqlExpr).getExpr();
            boolean contains = condition.contains(conditionName.toString());//在条件字段中判断是否包涵,如果不包含,就剔除该条件,当然不是修ast
            if(contains){
                print0(" "+conditionName +" ");
                if(((SQLInSubQueryExpr) sqlExpr).isNot()){
                     print0(ucase ? "NOT IN " : "not in ");
                }else{
                     print0(ucase ? "NOT IN " : "NOT in ");
                }

                print0(" ( ");
                SQLSelect subQuery = ((SQLInSubQueryExpr) sqlExpr).getSubQuery();
                visit(subQuery);
                print0(" ) ");
                WhereInfo whereInfo = whereInfos.peek();//获取where信息
                if(whereInfo != null){
                    whereInfo.addConditionCount();//计数器+1
                }

                OperatorInfo operatorInfo = operatorInfoMpa.get(sqlExpr.getParent().toString());
                if(operatorInfo != null){//判断是否为空
                    if(isRight){//判断是否是左边还是右边的表达式
                        operatorInfo.setRightCondition(true);
                    }else{
                        operatorInfo.setLeftCondition(true);
                    }
                }
            }
        }else{
            OperatorInfo operatorInfo = operatorInfoMpa.get(sqlExpr.getParent().toString());
            printExpr(sqlExpr);
            OperatorInfo info = operatorInfoMpa.get(sqlExpr.toString());
            if(info.isLeftCondition() || info.isRightCondition()){
                if(isRight){
                   operatorInfo.setRightCondition(true);
                }else{
                   operatorInfo.setLeftCondition(true);
                }
            }

        }
    }

    //条件处理
    private void handCondition(SQLExpr sqlExpr,boolean isRight){
        print0(" " + SQLUtils.toSQLString(sqlExpr) + " \n");
        BracketsInfo bracketsInfo = bracketsInfos.peek();//获取括号信息
        WhereInfo whereInfo = whereInfos.peek();//获取where信息
        if(bracketsInfo != null){//如果不为空的话,说明之前添加过右括号的,所以这里要判断一下
            bracketsInfo.addConditionCount();//计数器+1
        }

        if(whereInfo != null){
            whereInfo.addConditionCount();//计数器+1
        }

//                OperatorInfo operatorInfo = operatorInfos.peek();//获取表达式连接符信息
        OperatorInfo operatorInfo = operatorInfoMpa.get(sqlExpr.getParent().toString());
        if(operatorInfo != null){//判断是否为空
            if(isRight){//判断是否是左边还是右边的表达式
                operatorInfo.setRightCondition(true);
            }else{
                operatorInfo.setLeftCondition(true);
            }
        }

    }

}
