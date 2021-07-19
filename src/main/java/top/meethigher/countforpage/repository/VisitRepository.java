package top.meethigher.countforpage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.meethigher.countforpage.entity.Visit;

/**
 * VisitRepository
 *
 * @author kit chen
 * @github https://github.com/meethigher
 * @blog https://meethigher.top
 * @time 2021/7/19
 */
public interface VisitRepository extends JpaRepository<Visit,Integer> {
    Visit findByUrl(String url);
}
