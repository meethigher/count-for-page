package top.meethigher.count.page.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import top.meethigher.count.page.entity.IP;
import top.meethigher.count.page.entity.Link;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ip
 *
 * @author chenchuancheng github.com/meethigher
 * @since 2022/11/20 02:07
 */
@SpringBootTest
class IPRepositoryTest {

    @Resource
    private IPRepository ipRepository;

    @Test
    void findByLinkAndIpAddr() {
        Link link = new Link();
        link.setLinkId(192);
        List<IP> list = ipRepository.findByLinkIdAndIpAddr(link.getLinkId(), "171.214.159.32");
        assertNotNull(list);
    }
}