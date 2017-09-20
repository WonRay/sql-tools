package com.zzwtec.sql.info;

/**
 * 封装括号信息
 * @author 毛文超
 * **/
public class BracketsInfo {
    //左括号的索引
    private int leftBracketsIndex;
    //左括号的索引
    private int rightBracketsIndex;
    //该括号是否有效
    //括号内的有效条件个个数
    private int effectiveConditionCount;

    public BracketsInfo(int leftBracketsIndex, int rightBracketsIndex) {
        this.leftBracketsIndex = leftBracketsIndex;
        this.rightBracketsIndex = rightBracketsIndex;
    }

    public BracketsInfo() {
    }

    public int getLeftBracketsIndex() {
        return leftBracketsIndex;
    }

    public void setLeftBracketsIndex(int leftBracketsIndex) {
        this.leftBracketsIndex = leftBracketsIndex;
    }

    public int getRightBracketsIndex() {
        return rightBracketsIndex;
    }

    public void setRightBracketsIndex(int rightBracketsIndex) {
        this.rightBracketsIndex = rightBracketsIndex;
    }

    public void addConditionCount(){
        this.effectiveConditionCount++;
    }

    public boolean isEffectiveCondition(){
        return this.effectiveConditionCount > 1;
    }
}
