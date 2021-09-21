package top.meethigher.countforpage.dto;

/**
 * 因为异步任务在执行过程中，主线程直接结束了，异步线程还在运行
 * 此时主线程传给异步线程的变量就会变为null，为了解决这个问题
 * 将所有用到的信息提取成一个对象。
 *
 * @author chenchuancheng
 * @github https://github.com/meethigher
 * @blog https://meethigher.top
 * @time 2021/9/21 20:54
 */
public class SaveInfo {
    private String url;
    private String userAgent;
    private String originReferer;
    private String ip;

    public SaveInfo(String url, String userAgent, String originReferer, String ip) {
        this.url = url;
        this.userAgent = userAgent;
        this.originReferer = originReferer;
        this.ip = ip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getOriginReferer() {
        return originReferer;
    }

    public void setOriginReferer(String originReferer) {
        this.originReferer = originReferer;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
