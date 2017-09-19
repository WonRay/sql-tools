package com.zzwtec.sql.visitor;

import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.zzwtec.sql.info.BracketsInfo;

import java.util.concurrent.LinkedBlockingDeque;

public class MysqlConditionVisitor extends MySqlASTVisitorAdapter {
    private StringBuffer buffer = new StringBuffer();
    private LinkedBlockingDeque<BracketsInfo> bracketsInfos = new LinkedBlockingDeque<BracketsInfo>(10);

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



        return false;
    }
}
