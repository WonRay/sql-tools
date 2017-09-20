package com.zzwtec.sql.info;
/**
 * 用于描述ON关键字信息
 * @author 毛文超
 * */
public class JoinOnInfo {

    private int onStartIndex;
    private int onEndIndex;
    private int effectiveConditionCount;

    public JoinOnInfo() {
    }

    public int getOnStartIndex() {
        return onStartIndex;
    }

    public void setOnStartIndex(int onStartIndex) {
        this.onStartIndex = onStartIndex;
    }

    public int getOnEndIndex() {
        return onEndIndex;
    }

    public void setOnEndIndex(int onEndIndex) {
        this.onEndIndex = onEndIndex;
    }

    public int getEffectiveConditionCount() {
        return effectiveConditionCount;
    }

    public void setEffectiveConditionCount(int effectiveConditionCount) {
        this.effectiveConditionCount = effectiveConditionCount;
    }
}
