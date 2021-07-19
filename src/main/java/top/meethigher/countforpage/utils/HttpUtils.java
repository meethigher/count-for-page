package top.meethigher.countforpage.utils;

import javax.servlet.http.HttpServletRequest;

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
        return request.getHeader("user-agent");
    }

}
