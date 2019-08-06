package org.cronbee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.cronbee.config.CronConfig;
import org.cronbee.model.Cron;
import org.cronbee.util.CronUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CronDao {
    
    private final String SELECT_SQL = "select * from tb_cron_bee ";
    private final String WHERE_SQL_GROUP = " where group_name=? and run_way!='none'";
    private final String WHERE_SQL_GROUP_NULL = " where group_name is null or group_name='' and run_way!='none'";
    
    private final String UPDATE_TIME_SQL = "update tb_cron_bee set last_run_time =CURRENT_TIMESTAMP where id=?";
    
    private static final Logger logger = LoggerFactory.getLogger(CronDao.class);
    
    @Autowired
    private CronConfig cronConfig;


	public void updateLastRunTime(String cronId){
        
        PreparedStatement pst = null;
        Connection connection = null;
        try {
        	connection = cronConfig.getDataSource().getConnection();
            pst = connection.prepareStatement(UPDATE_TIME_SQL);

            pst.setString(1, cronId);
            pst.executeUpdate();
        } catch (Exception e) {
            logger.error("Exception occurs while reading data from database", e);
        }finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                }
            }
            
            //关闭数据库连接
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
        
    }
    
    
    public List<Cron> getAllCron(String group) {

        List<Cron> cronModelList = new ArrayList<Cron>();
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rst = null;
        try {
            
            connection = cronConfig.getDataSource().getConnection();
            if (CronUtil.isEmpty(group)) {
                String sql = SELECT_SQL + WHERE_SQL_GROUP_NULL;
                logger.debug("[getAllCron] select cron sql :" + sql);
                pst = connection.prepareStatement(sql);
                // pst.setString(1, Constant.SYSTEM_CRON_NAME);
            } else {
                String sql = SELECT_SQL + WHERE_SQL_GROUP;
                pst = connection.prepareStatement(sql);
                logger.debug("[getAllCron] select cron sql :" + sql);
                // pst.setString(1, Constant.SYSTEM_CRON_NAME);
                pst.setString(1, group);
            }
            rst = pst.executeQuery();
            while (rst.next()) {
                Cron cron = createCronModel(rst, false);
                cronModelList.add(cron);
                logger.debug("[getAllCron] select a cron from DB :" + cron.toString());
            }
            logger.debug("[getAllCron] select some cron from DB. [cronModelListSize = " + cronModelList.size() + "].");
        } catch (Exception e) {
            logger.error("Exception occurs while reading data from database", e);
        } finally {
            if (rst != null) {
                try {
                    rst.close();
                } catch (SQLException e) {
                }
            }
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
        return cronModelList;
    }
    
    
    
    private Cron createCronModel(ResultSet rst, boolean isSetLastRunTime) throws SQLException {

        Cron cron = new Cron();
        cron.setBeanName(rst.getString("bean_name"));
        cron.setMethodName(rst.getString("method_name"));
        cron.setParameter(rst.getString("parameter"));
        cron.setCronExpression(rst.getString("cron_expression"));
        cron.setCronDesc(rst.getString("cron_desc"));
        cron.setId(rst.getString("id"));
        cron.setGroup(rst.getString("group_name"));
        cron.setRunWay(rst.getString("run_way"));
        if(isSetLastRunTime){
            cron.setLastRunTime(rst.getTimestamp("last_run_time"));
        }
        return cron;
    }

}
