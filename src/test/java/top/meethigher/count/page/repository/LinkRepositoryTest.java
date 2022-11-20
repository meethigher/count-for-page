package top.meethigher.count.page.repository;

import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.meethigher.count.page.config.CrossOriginConfigure;
import top.meethigher.count.page.entity.IP;
import top.meethigher.count.page.entity.Link;

import javax.annotation.Resource;

@SpringBootTest
class LinkRepositoryTest {

    @Resource
    private LinkRepository linkRepository;


    @Test
    void oneToMany() {
        Link link = new Link();
        link.setLinkId(1);
        link.setUrl("");
        link.setCount(0);



        linkRepository.save(link);
        linkRepository.delete(link);
    }
}
