package org.cronbee.model;

import java.sql.Timestamp;

public class Cron {
    
    private String id;
    
    private String beanName; // 服务名称,bean name
    
    private String methodName; //方法名
    
    private String cronExpression;// 定时执行策略
    
    private String cronDesc;// cron描述
    
    private String group;// 分组
    
    private Timestamp lastRunTime; // 上一次执行时间
    
    private String runWay; //该cron的执行方式：all,所有机器都执行 single,仅某台执行 none,都不执行
    
    
    public Cron() {

    }
    
    public Cron(Cron cron) {

        this.beanName = cron.getBeanName();
        this.cronExpression = cron.getCronExpression();
        this.cronDesc = cron.getCronDesc();
        this.group = cron.getGroup();
    }
    
    @Override
    public String toString() {

        StringBuilder b = new StringBuilder();
        b.append("beanName=").append(this.beanName);
        b.append(", cronExpression=").append(this.cronExpression);
        b.append(", runWay=").append(this.runWay);
        return b.toString();
    }
    
    @Override
    public int hashCode() {

        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((this.cronExpression == null) ? 0 : this.cronExpression.hashCode());
        result = PRIME * result + ((this.beanName == null) ? 0 : this.beanName.hashCode());
        result = PRIME * result + ((this.runWay == null) ? 0 : this.runWay.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Cron other = (Cron) obj;
        if (this.cronExpression == null) {
            if (other.cronExpression != null) {
                return false;
            }
        } else if (!this.cronExpression.equals(other.cronExpression)) {
            return false;
        }
        if (this.beanName == null) {
            if (other.beanName != null) {
                return false;
            }
        } else if (!this.beanName.equals(other.beanName)) {
            return false;
        }
        
        if (this.methodName == null) {
            if (other.methodName != null) {
                return false;
            }
        } else if (!this.methodName.equals(other.methodName)) {
            return false;
        }
        
        if (this.runWay == null) {
            if (other.runWay != null) {
                return false;
            }
        } else if (!this.runWay.equals(other.runWay)) {
            return false;
        }
        
        return true;
    }
    
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //
    // Setter and Getters
    //
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    
    public String getId() {

        return this.id;
    }
    
    public void setId(String id) {

        this.id = id;
    }
    
    public String getCronDesc() {

        return this.cronDesc;
    }
    
    public void setCronDesc(String cronDesc) {

        this.cronDesc = cronDesc;
    }
    
    public String getBeanName() {

        return this.beanName;
    }
    
    public void setBeanName(String beanName) {

        this.beanName = beanName;
    }
    
    public String getMethodName() {

        return this.methodName;
    }
    
    public void setMethodName(String methodName) {

        this.methodName = methodName;
    }
    
    
    public String getCronExpression() {

        return this.cronExpression;
    }
    
    public void setCronExpression(String cronExpression) {

        this.cronExpression = cronExpression;
    }
    
    public String getRunWay() {

        return this.runWay;
    }
    
    public void setRunWay(String runWay) {

        this.runWay = runWay;
    }
    
    
    
    public String getGroup() {

        return this.group;
    }
    
    public void setGroup(String group) {

        this.group = group;
    }
    
    public Timestamp getLastRunTime() {

        return lastRunTime;
    }
    
    public void setLastRunTime(Timestamp lastRunTime) {

        this.lastRunTime = lastRunTime;
    }
    
}
