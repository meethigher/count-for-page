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
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"ip\":\"")
                .append(ip).append('\"');
        sb.append(",\"firstVisitTime\":\"")
                .append(firstVisitTime).append('\"');
        sb.append(",\"userAgent\":\"")
                .append(userAgent).append('\"');
        sb.append(",\"url\":\"")
                .append(url).append('\"');
        sb.append(",\"originReferer\":\"")
                .append(originReferer).append('\"');
        sb.append(",\"location\":\"")
                .append(location).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
