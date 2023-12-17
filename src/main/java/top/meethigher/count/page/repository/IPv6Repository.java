package top.meethigher.count.page.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.meethigher.count.page.entity.IPv6;

public interface IPv6Repository extends JpaRepository<IPv6, String>, JpaSpecificationExecutor<IPv6> {
}
