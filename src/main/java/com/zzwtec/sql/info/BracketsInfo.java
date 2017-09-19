package com.zzwtec.sql.info;

public class BracketsInfo {
    //括号的索引
    private int bracketsIndex;
    //该括号是否有效
    private boolean conditionEffective;
    //括号内的有效条件个个数
    private int effectiveConditionCount;

    public BracketsInfo(int bracketsIndex, boolean conditionEffective, int effectiveConditionCount) {
        this.bracketsIndex = bracketsIndex;
        this.conditionEffective = conditionEffective;
        this.effectiveConditionCount = effectiveConditionCount;
    }

    public int getBracketsIndex() {
        return bracketsIndex;
    }

    public void setBracketsIndex(int bracketsIndex) {
        this.bracketsIndex = bracketsIndex;
    }

    public boolean isConditionEffective() {
        return conditionEffective;
    }

    public void setConditionEffective(boolean conditionEffective) {
        this.conditionEffective = conditionEffective;
    }

    public int getEffectiveConditionCount() {
        return effectiveConditionCount;
    }

    public void setEffectiveConditionCount(int effectiveConditionCount) {
        this.effectiveConditionCount = effectiveConditionCount;
    }
}
