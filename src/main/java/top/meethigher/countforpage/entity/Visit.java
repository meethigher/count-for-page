package top.meethigher.countforpage.entity;

import javax.persistence.*;
import java.util.Set;

/**
 * 用于统计每个url访问的总次数
 *
 * @author kit chen
 * @github https://github.com/meethigher
 * @blog https://meethigher.top
 * @time 2021/7/19
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "url")})
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer vId;

    private String url;


    private Integer count;

    @OneToMany(targetEntity = IP.class, cascade = CascadeType.ALL, mappedBy = "visit")
    private Set<IP> ips;

    public Integer getvId() {
        return vId;
    }

    public void setvId(Integer vId) {
        this.vId = vId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Set<IP> getIps() {
        return ips;
    }

    public void setIps(Set<IP> ips) {
        this.ips = ips;
    }
}
