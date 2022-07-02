package top.meethigher.countforpage.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * 每有一个新ip访问，就会存一个IP
 *
 * @author kit chen
 * @github https://github.com/meethigher
 * @blog https://meethigher.top
 * @time 2021/7/19
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class IP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String ip;

    private String originReferer;

    /**
     * 这个跟Visit中的url实际上是一个。
     * 之前统计时，没有这个字段，还需要通过外键关联到Visit去查访问的url，现在直接保存在这里，可以减少io
     */
    private String originUrl;

    private String userAgent;

    private String firstVisitTime;

    private String location;


    @ManyToOne(targetEntity = Visit.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "vId", referencedColumnName = "vId")
    private Visit visit;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getFirstVisitTime() {
        return firstVisitTime;
    }

    public void setFirstVisitTime(String firstVisitTime) {
        this.firstVisitTime = firstVisitTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOriginReferer() {
        return originReferer;
    }

    public void setOriginReferer(String originReferer) {
        this.originReferer = originReferer;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }
}
