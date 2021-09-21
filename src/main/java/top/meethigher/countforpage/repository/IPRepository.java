package top.meethigher.countforpage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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


    @Query("select i.ip from IP i where i.visit.vId=:vid ")
    List<String> findIpByVid(Integer vid);
}
