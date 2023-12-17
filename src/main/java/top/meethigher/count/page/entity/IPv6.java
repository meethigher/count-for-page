package top.meethigher.count.page.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class IPv6 {
    @Id
    private String ipv6;

    private String loc;


    public String getIpv6() {
        return ipv6;
    }

    public void setIpv6(String ipv6) {
        this.ipv6 = ipv6;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public IPv6(String ipv6, String loc) {
        this.ipv6 = ipv6;
        this.loc = loc;
    }

    public IPv6() {
    }
}
