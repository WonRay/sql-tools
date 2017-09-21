package com.zzwtec.sql;


import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.zzwtec.sql.visitor.MysqlConditionVisitor;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String sql = "insert into ccp_mem(age) SELECT age FROM user LEFT JOIN ( SELECT user_id FROM order_item WHERE status = ?) orderItem ON orderItem.user_id = user.id WHERE user.name LIKE ? or sex = ?";
        String data = "SELECT result.id AS id, result.name AS name FROM ( SELECT user.* FROM user LEFT JOIN ( SELECT user_id FROM order_item WHERE status = ?) orderItem ON orderItem.user_id = user.id WHERE user.name LIKE ? or sex = ?) result GROUP BY result.id, result.name";

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement sqlStatement = parser.parseStatement();
        StringBuffer buffer = new StringBuffer();
        MySqlOutputVisitor visitor = new MySqlOutputVisitor(buffer);
        List<String> para = new ArrayList<>();
       // para.add("ROW");
       // para.add("age");
        para.add("ID");
        para.add("age");
        para.add("sex");
        para.add("user.name");
        para.add("status");
        MysqlConditionVisitor mysqlConditionVisitor = new MysqlConditionVisitor(para,buffer);
        sqlStatement.accept(mysqlConditionVisitor);
        System.out.printf(buffer.toString());


    }

}
