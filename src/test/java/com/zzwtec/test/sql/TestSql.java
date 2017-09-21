package com.zzwtec.test.sql;

import com.zzwtec.sql.ZSQLUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于测试
 * @author 毛文超
 * */
public class TestSql {

    @Test
    public void test(){
        String sql = "select * from ccp_mem where Name = ? and Age = ?";
        List<String> para = new ArrayList<>();
        para.add("Name");
        String s = ZSQLUtils.mysqSqlFilter(sql, para);
        System.out.println(s);
    }
}
