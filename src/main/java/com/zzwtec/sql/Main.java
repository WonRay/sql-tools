package com.zzwtec.sql;


import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.zzwtec.sql.visitor.MysqlConditionVisitor;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String sql = "select name,age from ccp_mem where age between ? and ?";
        String data = "SELECT result.id AS id, result.name AS name FROM ( SELECT user.* FROM user LEFT JOIN ( SELECT user_id FROM order_item WHERE status = ?) orderItem ON orderItem.user_id = user.id WHERE user.name LIKE ?) result GROUP BY result.id, result.name";

        MySqlStatementParser parser = new MySqlStatementParser(data);
        SQLStatement sqlStatement = parser.parseStatement();
        StringBuffer buffer = new StringBuffer();
        SQLASTOutputVisitor visitor = new SQLASTOutputVisitor(buffer);
        List<String> para = new ArrayList<>();
       // para.add("status");
        para.add("user.name");




        MysqlConditionVisitor mysqlConditionVisitor = new MysqlConditionVisitor(para,buffer);
        sqlStatement.accept(mysqlConditionVisitor);

        System.out.printf(buffer.toString());


    }

}
