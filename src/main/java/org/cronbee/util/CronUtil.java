package org.cronbee.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

public class CronUtil {
    
    private static Pattern p = Pattern.compile("^(10\\.|172\\.(1[6-9]|2[0-9]|3[01])\\.|192\\.168\\.)");
    
    /**
     * if the string paramter is null or "", return true
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
    	return s == null || s.length() == 0;
    }
    
    
    /**
     * 根据方法生成一个任务名
     * @param beanName
     * @param methodName
     * @return
     */
    public static String getTaskName(String beanName, String methodName) {
    	if(beanName == null || methodName == null) return null;
    	return beanName + "." + methodName;
    }
    
    /**
     * 计算两个时间的间隔秒数
     * 
     * @param time
     * @return
     */
    public static long getIntervalSeconds(Timestamp startTime, Timestamp endTime) {

        long interval = endTime.getTime() - startTime.getTime();
        long seconds = interval / 1000L;
        return Math.abs(seconds);
    }

    
    public static Timestamp changeToTimeStamp(Date date){
        return new Timestamp(date.getTime());
    }
    
    public static String getTaskTime(Timestamp time) {

        return new SimpleDateFormat("yyyyMMdd-HHmmss").format(time);
    }
    
    public static String getTaskTimeDayBefore(int dayBefore) {

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -dayBefore);
        return new SimpleDateFormat("yyyyMMdd-HHmmss").format(c.getTime());
    }
    
    public static long formatLong(String s, long l) {

        long res = l;
        if (!isEmpty(s)) {
            try {
                res = Long.valueOf(s);
            } catch (NumberFormatException e) {
            }
        }
        return res;
    }

    /**
     * 调用shell取服务器的ip地址。多个内外网地址之间用,分隔。
     * 
     * @return 用分隔符,分隔的多个ip地址。
     */
    public static String getLinuxServerIp() {

        StringBuffer ips = new StringBuffer();
        try {
            String[] command = { "/bin/sh", "-c", "/sbin/ifconfig | grep 'Bcast' | awk '{print $2}' | sed -e 's/addr.//g'" };
            Process process = Runtime.getRuntime().exec(command);
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            BufferedReader input = new BufferedReader(ir);
            String line = "";
            while ((line = input.readLine()) != null) {
                ips.append(line).append(",");
            }
            input.close();
            ir.close();
            input = null;
            ir = null;
            process.getInputStream().close();
            process.getOutputStream().close();
            process.getErrorStream().close();
            process.destroy();
            process = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ips.toString();
    }
    
    /**
     * 获取linux机器的内网ip地址。
     * 
     * @return
     */
    public static String getIP() {
        if(isWindows())return getWindowsIp();
        String serverIP = getLinuxServerIp();
        if (serverIP != null) {
            String ipArray[] = serverIP.split(",");
            // 取最后一个IP地址 , 内网IP地址。
            if (ipArray != null && ipArray.length > 0) {
                return ipArray[ipArray.length - 1];
            }
            return null;
        }
        return null;
    }
    
    /**
     * windows环境下通过命令获取本机ip
     * @return
     */
    public static String getWindowsIp() {
        String command = "ipconfig";
        try {
            Process process = Runtime.getRuntime().exec(command);
            InputStreamReader ir = new InputStreamReader(process
                    .getInputStream());
            BufferedReader input = new BufferedReader(ir);
            String line = input.readLine();
            while (line != null) {
                if (line.contains("IP Address")) {
                    String[] segs = line.split(":");
                    if (segs.length > 1) {
                        return segs[1].trim();
                    }
                }
                line = input.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 判断是否是windows环境
     * @return
     */
    public static boolean isWindows() {
        String osName = System.getProperty("os.name");
        if (osName.matches("[Ww][Ii][Nn][Dd][Oo][Ww][Ss].*")
                && !File.separator.equals("/")) {
            return true;
        }
        return false;
    }
    
    private static boolean isInnerIp(String ip) {
        return p.matcher(ip).find();
    }
    
    /**
     * 获取服务器ip,适用于redhat7
     * @param incluedOuter
     * @return
     * @throws Exception
     */
    public static List<String> getServerIps(boolean incluedOuter) throws Exception {
        List<String> list = new ArrayList<String>();
        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        for (; netInterfaces.hasMoreElements();) {
            NetworkInterface netInterface = netInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = netInterface.getInetAddresses();
            for (; inetAddresses.hasMoreElements();) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (inetAddress instanceof Inet4Address) {
                    String localIp = inetAddress.getHostAddress();
                    if (localIp.equals("127.0.0.1")) {
                        continue;
                    }
                    if (incluedOuter || isInnerIp(localIp)) {
                        list.add(localIp);
                    }
                }
            }
        }
        return list;
    }
    
}
