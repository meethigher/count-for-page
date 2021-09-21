package top.meethigher.countforpage.dto;

/**
 * TopResponse
 *
 * @author kit chen
 * @github https://github.com/meethigher
 * @blog https://meethigher.top
 * @time 2021/7/20
 */
public class TopResponse {
    private String ip;
    private String firstVisitTime;
    private String userAgent;
    private String url;
    private String originReferer;
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getFirstVisitTime() {
        return firstVisitTime;
    }

    public void setFirstVisitTime(String firstVisitTime) {
        this.firstVisitTime = firstVisitTime;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginReferer() {
        return originReferer;
    }

    public void setOriginReferer(String originReferer) {
        this.originReferer = originReferer;
    }

    @Override
    public String toString() {
        return "TopResponse{" +
                "ip='" + ip + '\'' +
                ", firstVisitTime='" + firstVisitTime + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", url='" + url + '\'' +
                ", originReferer='" + originReferer + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
