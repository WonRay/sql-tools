package com.zzwtec.test.sql;

import com.zzwtec.sql.ZSQLUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.LongSummaryStatistics;

/**
 * 用于测试
 * @author 毛文超
 * */
public class TestSql {

    @Test
    public void test(){
        String sql = "SELECT ad.* FROM ad " +
                "LEFT JOIN ad_position_ad apa ON apa.ad_id = ad.id " +
                "LEFT JOIN ad_position ON ad_position.id = apa.ad_position_id " +
                "WHERE ad.fabu = ? AND ad.pass=? AND ad.on_time<=? AND ad.off_time>? AND ad_position.door_id=?";
        List<String> para = new ArrayList<>();
        para.add("ad.fabu");

        String s = ZSQLUtils.mysqSqlFilter(sql, para);
        System.out.println(s);


      /*  List<Long> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            long startTime = System.currentTimeMillis();
            String s = ZSQLUtils.mysqSqlFilter(sql, para);
            System.out.println(s);
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            list.add(totalTime);
            System.out.println("本次用时:"+totalTime);
        }


        LongSummaryStatistics longSummaryStatistics = list.stream().mapToLong(r -> r).summaryStatistics();
        long count = longSummaryStatistics.getCount();
        long max = longSummaryStatistics.getMax();
        long min = longSummaryStatistics.getMin();
        double average = longSummaryStatistics.getAverage();
        long sum = longSummaryStatistics.getSum();

        System.out.println("总共测试了:"+count);
        System.out.println("用时最长:"+max);
        System.out.println("用时最短:"+min);
        System.out.println("平均用时:"+average);
        System.out.println("总共用时:"+sum);*/


    }
}
