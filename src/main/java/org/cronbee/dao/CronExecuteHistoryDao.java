package org.cronbee.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class CronExecuteHistoryDao {
    private final String UPDATE_SQL = "update tb_cron_execute_history set status =? where id=?";
    
    private final String INSERT_SQL = "insert into tb_cron_execute_history (id, cron_id,bean_name) values (?,?,?)";
    
    private static final Logger logger = LoggerFactory.getLogger(CronExecuteHistoryDao.class);
    
    private DataSource dataSource;
    
    public CronExecuteHistoryDao(DataSource dataSource) {

        this.dataSource = dataSource;
    }
    
    /**
     * 获取数据库连接
     * 
     * @return
     */
    public DataSource getDataSource() {

        return dataSource;
    }
    
    /**
     * 更新执行记录
     * 
     * @param conn
     * @param id
     * @return
     */
    public int updateCronExecuteHistory(String id, String status) {

        PreparedStatement pst = null;
        Connection conn = null;
        int ret = 0;
        try {
            conn = this.dataSource.getConnection();
            pst = conn.prepareStatement(UPDATE_SQL);
            pst.setString(1, status);
            pst.setString(2, id);
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
    public int insertCronExecuteHistory(String id, String cronId, String beanName) {

        PreparedStatement pst = null;
        Connection conn = null;
        int ret = 0;
        try {
            conn = this.dataSource.getConnection();
            pst = conn.prepareStatement(INSERT_SQL);
            pst.setString(1, id);
            pst.setString(2, cronId);
            pst.setString(3, beanName);
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
