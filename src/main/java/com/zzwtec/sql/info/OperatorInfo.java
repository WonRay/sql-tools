package com.zzwtec.sql.info;
/**
 * 用于记录表达式连接符的信息
 * @author 毛文超
 * */
public class OperatorInfo {

    private int operatorStartIndex;//表达式起始位置
    private int operatorEndIndex;//表达式结束位置
    private boolean leftCondition;//左边条件是否成立
    private boolean rightCondition;//右边条件是否成立

    public OperatorInfo() {
    }

    public int getOperatorStartIndex() {
        return operatorStartIndex;
    }

    public void setOperatorStartIndex(int operatorStartIndex) {
        this.operatorStartIndex = operatorStartIndex;
    }

    public int getOperatorEndIndex() {
        return operatorEndIndex;
    }

    public void setOperatorEndIndex(int operatorEndIndex) {
        this.operatorEndIndex = operatorEndIndex;
    }

    public boolean isLeftCondition() {
        return leftCondition;
    }

    public void setLeftCondition(boolean leftCondition) {
        this.leftCondition = leftCondition;
    }

    public boolean isRightCondition() {
        return rightCondition;
    }

    public void setRightCondition(boolean rightCondition) {
        this.rightCondition = rightCondition;
    }

    public boolean isOk(){
        return leftCondition && rightCondition;
    }
}
