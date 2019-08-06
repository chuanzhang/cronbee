package org.cronbee.constant;


public final class CronConstant {

    
    public static final String RUN_WAY_ALL = "all";// 执行方式-所有机器都执行
    public static final String RUN_WAY_SINGLE = "single";// 执行方式-仅某几台执行且在本机执行
    public static final String RUN_WAY_NONE = "none";// 执行方式-仅某几台执行且在本机执行

    public static final int SUCCESS_CODE = 200; //操作成功
    public static final int TASK_ERROR_CODE = 402;// 任务本身抛错
    public static final String MESSAGE_OK = "success";
    public static final int MAX_MESSAGE_LENGTH = 128;
    public static final long WAIT_TIME_IN_MILLS = 100; //设置优先运行ip时，非对应ip等待的毫秒数
    
    
    public static enum CronExecuteHistoryStatus  {
        FINISH ("finish"),
        INIT ("init"),
        FAIL("fail");
        
        private String status;
        private CronExecuteHistoryStatus(String status) {
            this.status = status;
        }
        public String  getCronExecuteHistoryStatus() {
            return this.status;
        }
        
    }
}
