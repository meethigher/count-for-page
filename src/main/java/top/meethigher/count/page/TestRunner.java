package top.meethigher.count.page;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import top.meethigher.count.page.entity.IP;
import top.meethigher.count.page.entity.Link;
import top.meethigher.count.page.repository.IPRepository;
import top.meethigher.count.page.repository.LinkRepository;
import top.meethigher.count.page.utils.UrlUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TestRunner implements CommandLineRunner {


    @Resource
    private LinkRepository linkRepository;

    @Resource
    private IPRepository ipRepository;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Value("${domain}")
    private String domain;

    @Override
    public void run(String... args) throws Exception {
        linkClean();
        ipClean();
        createMapping();


        System.exit(0);
    }


    private void createMapping() {
        List<Link> linkAll = linkRepository.findAll();
        Map<String, Integer> map = linkAll.stream().collect(Collectors.toMap(Link::getUrl, Link::getLinkId));
        List<IP> ipAll = ipRepository.findAll();
        for (IP ip : ipAll) {
            String url = ip.getTargetLink().replace("https://", "");
            Integer i = map.get(url);
            ip.setLinkId(i);
        }
        ipRepository.saveAll(ipAll);
    }

    private void linkClean() {
        List<Link> linkList = jdbcTemplate.query("select url,count from link_old order by 2 asc", new BeanPropertyRowMapper<>(Link.class));
        Map<String, Long> map = new LinkedHashMap<>();
        for (Link link : linkList) {
            if (link.getUrl() != null && link.getUrl().contains(domain)) {
                if (link.getUrl().contains("//") || link.getUrl().contains("%2")) {
                    continue;
                }
                String url = link.getUrl();
                if (map.containsKey(url)) {
                    map.put(url, map.get(url) + link.getCount());
                } else {
                    map.put(url, Long.valueOf(link.getCount()));
                }
            }
        }
        List<Link> newList = new ArrayList<>();
        for (String url : map.keySet()) {
            long l = map.get(url);
            Link link = new Link();
            link.setUrl(url);
            link.setCount((int) l);
            newList.add(link);
        }
        linkRepository.saveAll(newList);
        System.out.println("linkClean完啦");
    }

    private void ipClean() {
        List<IP> ipList = jdbcTemplate.query("select * from ip_old order by ip_id", new BeanPropertyRowMapper<>(IP.class));
        System.out.println();
        List<IP> newList = new ArrayList<>();
        for (IP ip : ipList) {
            if (ObjectUtils.isEmpty(ip.getTargetLink())) {
                continue;
            }
            if (ip.getTargetLink().contains(domain)) {
                ip.setIpId(null);
                String requestURI = UrlUtils.getRequestURI(ip.getTargetLink(), true);
                ip.setTargetLink(requestURI);
                newList.add(ip);
            }
        }
        ipRepository.saveAll(newList);
        System.out.println("ipClean完啦");
    }
}
