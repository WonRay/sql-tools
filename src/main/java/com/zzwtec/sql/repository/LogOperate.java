package com.zzwtec.sql.repository;
/**
 * 日志操作类型
 * @author 毛文超
 * */
public enum LogOperate {
    /**
     * 增加
     * */
    INSERT(1),
    /**
     * 更新
     * */
    UPDATE(2),
    /**
     * 删除
     * */
    DELETE(3);


    LogOperate(int operate){
        this.operate = operate;
    }

    /**
     * 操作类型
     * */
    private int operate;
    /**
     * 获取操作类型
     * */
    public int getOperate() {
        return operate;
    }
    /**
     * 获取操作类型
     * */
    public static LogOperate getOperate(int operate){
        switch (operate){
            case 1:
                return INSERT;
            case 2:
                return UPDATE;
            case 3:
                return DELETE;
             default:
                 return null;
        }
    }

}
