package top.meethigher.count.page.entity;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Set;

/**
 * 每个页面Link的统计信息
 *
 * @author chenchuancheng
 * @since 2022/11/19 15:41
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "url")})
@EntityListeners(AuditingEntityListener.class)
public class Link {
    /**
     * 编号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer linkId;

    /**
     * link地址
     */
    private String url;


    /**
     * 数量
     */
    private Integer count;


    public Integer getLinkId() {
        return linkId;
    }

    public void setLinkId(Integer linkId) {
        this.linkId = linkId;
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

}
