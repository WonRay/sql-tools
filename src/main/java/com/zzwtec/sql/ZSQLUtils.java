package com.zzwtec.sql;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.zzwtec.sql.visitor.MysqlConditionVisitor;

import java.util.List;

/**
 * 用于解析sql语句的工具类
 * @author 毛文超
 * */
public abstract class ZSQLUtils {

    public static String mysqlSqlFilter(String sql, List<String> conditionNames){
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement sqlStatement = parser.parseStatement();
        StringBuilder buffer = new StringBuilder();
        MysqlConditionVisitor mysqlConditionVisitor = new MysqlConditionVisitor(conditionNames,buffer);
        sqlStatement.accept(mysqlConditionVisitor);
        return buffer.toString();
    }

}
