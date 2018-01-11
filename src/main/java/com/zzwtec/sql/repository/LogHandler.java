package com.zzwtec.sql.repository;

import com.alibaba.fastjson.JSONObject;
/**
 * 用于处理日志
 * @author 毛文超
 * */
@FunctionalInterface
public interface LogHandler {
    /**
     * @param operate 操作类型
     * @param tableId 表id
     * @param tableName 表名
     * @param detail 详情
     * */
    void handle(LogOperate operate, String tableId, String tableName, JSONObject detail);

}
