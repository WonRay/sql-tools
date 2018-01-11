package com.zzwtec.test.sql;

import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.Test;

/**
 * 用于测试
 * @author 毛文超
 * */
public class TestSql {

    @Test
    public void test(){
       String sql = "update door set id=?,name=?,index=? where text = ?";
       MySqlStatementParser mySqlStatementParser = new MySqlStatementParser(sql);
      
    }
}
