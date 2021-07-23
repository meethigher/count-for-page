package top.meethigher.countforpage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.meethigher.countforpage.entity.IP;

import java.util.Date;
import java.util.List;

/**
 * IPRepository
 *
 * @author kit chen
 * @github https://github.com/meethigher
 * @blog https://meethigher.top
 * @time 2021/7/19
 */
public interface IPRepository extends JpaRepository<IP,Integer> {

    List<IP> findByFirstVisitTimeBetween(String startTime, String endTime);
}
