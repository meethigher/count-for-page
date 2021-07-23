package top.meethigher.countforpage.utils;

import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;

/**
 * IPUtils
 *
 * @author kit chen
 * @github https://github.com/meethigher
 * @blog https://meethigher.top
 * @time 2021/7/19
 */
public class HttpUtils {
    /**
     * 获取真实ip
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取访问url
     *
     * @param request
     * @return
     */
    public static String getUrl(HttpServletRequest request) {
        // 访问协议
//        String agreement = request.getScheme();
        // 访问域名
        String serverName = request.getServerName();
        // 访问端口号
        int port = request.getServerPort();
        // 访问项目名
        String contextPath = request.getContextPath();
//        String url = "%s://%s%s%s";
        String url = "%s%s%s";
        String portStr = "";
        if (port != 80) {
            portStr += ":" + port;
        }
        return String.format(url, serverName, portStr, contextPath);
    }

    /**
     * 获取设备信息
     *
     * @param request
     * @return
     */
    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.USER_AGENT);
    }

    /**
     * 获取来源链接
     * @param request
     * @return
     */
    public static String getReferer(HttpServletRequest request){
        return request.getHeader(HttpHeaders.REFERER);
    }

    /**
     * 发送Get
     *
     * @param url
     * @param headers
     * @return
     */
    public static String sendGet(String url, Map<String, String> headers) {
        String result = "";
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            Set<Map.Entry<String, String>> set = headers.entrySet();
            for (Map.Entry<String, String> header : set) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
            conn.connect();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 发送Post
     *
     * @param url
     * @param param
     * @param headers
     * @return
     */
    public static String sendPost(String url, String param, Map<String, String> headers) {
        BufferedWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            Set<Map.Entry<String, String>> set = headers.entrySet();
            for (Map.Entry<String, String> header : set) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
            out.write(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
