package top.meethigher.count.page.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.meethigher.count.page.entity.IP;
import top.meethigher.count.page.entity.Link;

import java.util.List;

/**
 * ip
 *
 * @author chenchuancheng
 * @since 2022/11/19 15:44
 */
public interface IPRepository extends JpaRepository<IP, String> {

    List<IP> findByLinkIdAndIpAddr(Integer linkId, String ipAddr);

    List<IP> findByFirstVisitTimeBetween(String startTime, String endTime);

}
