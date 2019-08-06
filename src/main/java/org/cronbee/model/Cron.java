package org.cronbee.model;

import java.sql.Timestamp;

public class Cron {
    
    private String id;
    
    private String beanName; // 服务名称,bean name
    
    private String methodName; //方法名
    
    private String parameter; //参数，目前只允许一个String类型参数，复杂情况可自行转换或分割字符串解决
    
    private String cronExpression;// 定时执行策略
    
    private String cronDesc;// cron描述
    
    private String group;// 分组
    
    private String runWay; //该cron的执行方式：all,所有应用都执行 single,仅某个应用执行 none,都不执行
    
    private String preferIp;
    
    
    private Timestamp lastRunTime; // 上一次执行时间
    
    
    
    
    @Override
    public String toString() {

        StringBuilder b = new StringBuilder();
        b.append("id=").append(this.id);
        b.append("beanName=").append(this.beanName);
        b.append(",methodName=").append(this.methodName);
        b.append(",parameter=").append(this.parameter);
        b.append(",cronExpression=").append(this.cronExpression);
        b.append(",group=").append(this.group);
        b.append(",cronDesc=").append(this.cronDesc);
        b.append(",runWay=").append(this.runWay);
        b.append(",preferIp=").append(this.preferIp);
        return b.toString();
    }
    
    @Override
    public int hashCode() {

        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((this.id == null) ? 0 : this.id.hashCode());
        result = PRIME * result + ((this.beanName == null) ? 0 : this.beanName.hashCode());
        result = PRIME * result + ((this.cronExpression == null) ? 0 : this.cronExpression.hashCode());
        
        result = PRIME * result + ((this.methodName == null) ? 0 : this.methodName.hashCode());
        result = PRIME * result + ((this.runWay == null) ? 0 : this.runWay.hashCode());
        
        result = PRIME * result + ((this.parameter == null) ? 0 : this.parameter.hashCode());
        result = PRIME * result + ((this.cronDesc == null) ? 0 : this.cronDesc.hashCode());
        
        result = PRIME * result + ((this.group == null) ? 0 : this.group.hashCode());
        result = PRIME * result + ((this.preferIp == null) ? 0 : this.preferIp.hashCode());
        
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
        
        if (this.id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!this.id.equals(other.id)) {
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
        
        if (this.parameter == null) {
            if (other.parameter != null) {
                return false;
            }
        } else if (!this.parameter.equals(other.parameter)) {
            return false;
        }
        
        if (this.cronExpression == null) {
            if (other.cronExpression != null) {
                return false;
            }
        } else if (!this.cronExpression.equals(other.cronExpression)) {
            return false;
        }
        
        if (this.cronDesc == null) {
            if (other.cronDesc != null) {
                return false;
            }
        } else if (!this.cronDesc.equals(other.cronDesc)) {
            return false;
        }
        
        if (this.group == null) {
            if (other.group != null) {
                return false;
            }
        } else if (!this.group.equals(other.group)) {
            return false;
        }
        
        
        if (this.runWay == null) {
            if (other.runWay != null) {
                return false;
            }
        } else if (!this.runWay.equals(other.runWay)) {
            return false;
        }
        
        if (this.preferIp == null) {
            if (other.preferIp != null) {
                return false;
            }
        } else if (!this.preferIp.equals(other.preferIp)) {
            return false;
        }
        
        //上次执行时间并非关键项，而且动态变化，此处不比较
        
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
    
    public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getPreferIp() {
		return preferIp;
	}

	public void setPreferIp(String preferIp) {
		this.preferIp = preferIp;
	}

	public Timestamp getLastRunTime() {

        return lastRunTime;
    }
    
    public void setLastRunTime(Timestamp lastRunTime) {

        this.lastRunTime = lastRunTime;
    }
    
}
