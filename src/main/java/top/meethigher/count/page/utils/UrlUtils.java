package top.meethigher.count.page.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

public class UrlUtils {

    private static final String URL_REGEX = "^(https?)://[^\\s/$.?#].[^\\s]*$";
    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    /**
     * 校验是否是合法的url请求。格式为[protocol]://[path]
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        return URL_PATTERN.matcher(url).matches();
    }

    /**
     * 根据输入的url参数提取出其中不带参数的实际请求uri地址
     */
    public static String getRequestURI(String url, boolean includeProtocol) {
        try {
            if (!(url.startsWith("http://") || url.startsWith("https://"))) {
                url = "https://" + url;
            }
            URL parsedUrl = new URL(url);
            if (includeProtocol) {
                return parsedUrl.getProtocol() + "://" + parsedUrl.getHost() + parsedUrl.getPath();
            } else {
                return parsedUrl.getHost() + parsedUrl.getPath();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
