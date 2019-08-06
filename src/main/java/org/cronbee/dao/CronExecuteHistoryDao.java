package org.cronbee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.cronbee.config.CronConfig;
import org.cronbee.model.Cron;
import org.cronbee.util.CronUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CronExecuteHistoryDao {
    private final String UPDATE_SQL = "update tb_cron_execute_history set status =?, message=? where id=?";
    
    private final String INSERT_SQL = "insert into tb_cron_execute_history (id, cron_id,task_name) values (?,?,?)";
    
    private static final Logger logger = LoggerFactory.getLogger(CronExecuteHistoryDao.class);
    
    @Autowired
    private CronConfig cronConfig;
    
    /**
     * 更新执行记录
     * 
     * @param conn
     * @param id
     * @return
     */
    public int updateCronExecuteHistory(String id, String status,String message) {

        PreparedStatement pst = null;
        Connection conn = null;
        int ret = 0;
        try {
            conn = cronConfig.getDataSource().getConnection();
            pst = conn.prepareStatement(UPDATE_SQL);
            pst.setString(1, status);
            pst.setString(2, message);
            pst.setString(3, id);
            ret = pst.executeUpdate();
            
        } catch (Exception e) {
            logger.error("Exception occurs while update data from database", e);
        }finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                }
            }
            
            //关闭数据库
            if(conn != null) {
                try {
                    conn.close();
                }catch(Exception ex) {
                    logger.error("Exception occurs while closing database connection", ex);
                }
            }
        }
        return ret;
    }
    
    /**
     * 插入执行记录
     * 
     * @param conn
     * @param id
     * @return
     */
    public int insertCronExecuteHistory(String id, Cron cron) {

        PreparedStatement pst = null;
        Connection conn = null;
        int ret = 0;
        try {
            conn = this.cronConfig.getDataSource().getConnection();
            pst = conn.prepareStatement(INSERT_SQL);
            pst.setString(1, id);
            pst.setString(2, cron.getId());
            pst.setString(3, CronUtil.getTaskNamebyCron(cron));
            ret = pst.executeUpdate();
            
        } catch (Exception e) {
            logger.error("Exception occurs while update data from database", e);
        }finally {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    logger.error("Exception occurs while update data from database", e);
                }
            }
            
            if(conn != null) {
                try {
                    conn.close();
                }catch(Exception ex) {
                    logger.error("Exception occurs while closing database connection", ex);
                }
            }
        }
        return ret;
    }
}
