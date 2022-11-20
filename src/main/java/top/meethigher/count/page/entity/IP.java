package top.meethigher.count.page.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

/**
 * ip记录信息
 *
 * @author chenchuancheng
 * @since 2022/11/19 15:42
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class IP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ipId;

    /**
     * ip地址
     */
    private String ipAddr;

    /**
     * ip地址描述
     */
    private String ipLoc;


    /**
     * 目标地址
     */
    private String targetLink;


    /**
     * 来源地址
     */
    private String originLink;

    /**
     * 设备
     */
    private String device;

    /**
     * 初次访问时间
     */
    private String firstVisitTime;


    /**
     * 链接编号
     */
    private Integer linkId;

    public Integer getIpId() {
        return ipId;
    }

    public void setIpId(Integer ipId) {
        this.ipId = ipId;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getIpLoc() {
        return ipLoc;
    }

    public void setIpLoc(String ipLoc) {
        this.ipLoc = ipLoc;
    }

    public String getTargetLink() {
        return targetLink;
    }

    public void setTargetLink(String targetLink) {
        this.targetLink = targetLink;
    }

    public String getOriginLink() {
        return originLink;
    }

    public void setOriginLink(String originLink) {
        this.originLink = originLink;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getFirstVisitTime() {
        return firstVisitTime;
    }

    public void setFirstVisitTime(String firstVisitTime) {
        this.firstVisitTime = firstVisitTime;
    }

    public Integer getLinkId() {
        return linkId;
    }

    public void setLinkId(Integer linkId) {
        this.linkId = linkId;
    }
}
