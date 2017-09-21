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
        String sql = "insert into ccp_mem(age) SELECT age FROM user LEFT JOIN ( SELECT user_id FROM order_item WHERE status = ?) orderItem ON orderItem.user_id = user.id WHERE user.name LIKE ? or sex = ?";
        List<String> para = new ArrayList<>();
        para.add("sex");
       // para.add("user.name");
        String s = ZSQLUtils.mysqSqlFilter(sql, para);
        System.out.println(s);
    }
}
