package com.zzwtec.sql;


import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.zzwtec.sql.visitor.MysqlConditionVisitor;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String sql = "insert into ccp_mem(age) SELECT age FROM user LEFT JOIN ( SELECT user_id FROM order_item WHERE status = ?) orderItem ON orderItem.user_id = user.id WHERE user.name LIKE ? or sex = ?";
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement sqlStatement = parser.parseStatement();
        StringBuffer buffer = new StringBuffer();

        List<String> para = new ArrayList<>();
        para.add("sex");
        MysqlConditionVisitor mysqlConditionVisitor = new MysqlConditionVisitor(para,buffer);
        sqlStatement.accept(mysqlConditionVisitor);
        System.out.printf(buffer.toString());


    }

}
