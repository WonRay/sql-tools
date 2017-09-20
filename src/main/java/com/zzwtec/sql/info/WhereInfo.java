package com.zzwtec.sql.info;

/**
 * 用于记录where关键字的信息
 *@author 毛文超
 * */
public class WhereInfo {

    private int whereStartIndex;
    private int whereEndIndex;
    private int effectiveConditionCount;

    public WhereInfo(int whereStartIndex, int whereEndIndex) {
        this.whereStartIndex = whereStartIndex;
        this.whereEndIndex = whereEndIndex;
    }

    public WhereInfo() {
    }

    public int getWhereStartIndex() {
        return whereStartIndex;
    }

    public void setWhereStartIndex(int whereStartIndex) {
        this.whereStartIndex = whereStartIndex;
    }

    public int getWhereEndIndex() {
        return whereEndIndex;
    }

    public void setWhereEndIndex(int whereEndIndex) {
        this.whereEndIndex = whereEndIndex;
    }

    public void addConditionCount(){
        this.effectiveConditionCount++;
    }

    public boolean isEffectiveCondition(){
        return this.effectiveConditionCount > 0;
    }
}
