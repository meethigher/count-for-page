package top.meethigher.count.page.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.meethigher.count.page.entity.Link;

/**
 * 页面Link信息
 *
 * @author chenchuancheng
 * @since 2022/11/19 15:46
 */
public interface LinkRepository extends JpaRepository<Link, String> {

    /**
     * 通过链接地址查询，加唯一约束
     *
     * @param url 链接地址
     * @return 链接访问信息
     */
    Link findByUrl(String url);
}
