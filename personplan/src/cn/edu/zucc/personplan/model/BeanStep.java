package cn.edu.zucc.personplan.model;

import javax.swing.tree.DefaultTreeCellEditor;
import java.util.Date;

public class BeanStep {
    public static final String[] tblStepTitle = {"序号", "名称", "计划开始时间", "计划完成时间", "实际开始时间", "实际完成时间"};
    private int stepId;
    private int planId;
    private int stepOrder;
    private String stepName;
    private Date planBeginTime;
    private Date planEndTime;
    private Date realBeginTime;
    private Date realEndTime;

    public int getStepId() {
        return stepId;
    }

    public void setStepId(int stepId) {
        this.stepId = stepId;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public int getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(int stepOrder) {
        this.stepOrder = stepOrder;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public Date getPlanBeginTime() {
        return planBeginTime;
    }

    public void setPlanBeginTime(Date planBeginTime) {
        this.planBeginTime = planBeginTime;
    }

    public Date getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    public Date getRealBeginTime() {
        return realBeginTime;
    }

    public void setRealBeginTime(Date realBeginTime) {
        this.realBeginTime = realBeginTime;
    }

    public Date getRealEndTime() {
        return realEndTime;
    }

    public void setRealEndTime(Date realEndTime) {
        this.realEndTime = realEndTime;
    }

    public String getCell(int col) {
        if (col == 0) return String.valueOf(getStepOrder());
        else if (col == 1) return getStepName();
        else if (col == 2) return String.valueOf(getPlanBeginTime());
        else if (col == 3) return String.valueOf(getPlanEndTime());
        else if (col == 4) return String.valueOf(getRealBeginTime());
        else if (col == 5) return String.valueOf(getRealEndTime());
        else return "";
    }
}
