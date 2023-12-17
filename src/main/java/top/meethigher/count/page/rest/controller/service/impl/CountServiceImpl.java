package top.meethigher.count.page.rest.controller.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.meethigher.cache.CacheStore;
import top.meethigher.count.page.entity.IP;
import top.meethigher.count.page.entity.Link;
import top.meethigher.count.page.repository.IPRepository;
import top.meethigher.count.page.repository.IPv6Repository;
import top.meethigher.count.page.repository.LinkRepository;
import top.meethigher.count.page.rest.controller.service.CountService;
import top.meethigher.count.page.utils.IPv4Validator;
import top.meethigher.count.page.utils.IPv6Utils;
import top.meethigher.czip.IPSearcher;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;

/**
 * CountServiceImpl
 *
 * @author kit chen
 * @github https://github.com/meethigher
 * @blog https://meethigher.top
 * @time 2021/7/19
 */
@Service
public class CountServiceImpl implements CountService {

    private static final Logger log = LoggerFactory.getLogger(CountServiceImpl.class);

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA);


    @Value("${domain}")
    private String domain;

    @Resource
    private IPRepository ipRepository;

    @Resource
    private LinkRepository linkRepository;

    @Resource
    private IPv6Repository iPv6Repository;

    @Resource
    private CacheStore<Integer, IP> cacheStore;

    @Resource
    private ExecutorService countForPage;


    /**
     * 通过链接查询该链接的访问统计
     *
     * @param linkUrl 链接
     * @return 链接的访问统计
     */
    private Link findByUrl(String linkUrl) {
        Link link = linkRepository.findByUrl(linkUrl);
        if (ObjectUtils.isEmpty(link)) {
            link = new Link();
            link.setCount(0);
            link.setUrl(linkUrl);
        }
        return link;
    }

    /**
     * 创建IP信息
     *
     * @param request http
     * @return ip
     */
    private IP createIP(HttpServletRequest request) {
        IP ip = new IP();
        ip.setDevice(request.getHeader(HttpHeaders.USER_AGENT));
        //由于使用了nginx，我在nginx配置的x-forwarded-for。所以此处要拿代理ip
        ip.setIpAddr(request.getHeader("x-forwarded-for"));
        if (ObjectUtils.isEmpty(ip.getIpAddr())) {
            ip.setIpLoc("ip地址为空");
        } else {
            //ip.setIpAddr(request.getRemoteAddr());
            ip.setFirstVisitTime(timeFormatter.format(LocalDateTime.now()));
            if (IPv4Validator.isValidIPv4(ip.getIpAddr())) {
                ip.setIpLoc(IPSearcher.getInstance().search(ip.getIpAddr()));
            } else {
                ip.setIpLoc(IPv6Utils.queryIPv6LocFromAPI(ip.getIpAddr()));
            }
        }
        //由于是静态页面，所以访问页面时，调用接口。此时接口拿到的referer就是访问的页面地址
        ip.setTargetLink(request.getHeader(HttpHeaders.REFERER));
        //获取客户端访问网页的referer，此处是网页js通过ajax添加到请求头将referer进行了一个转发。该功能需要配合我的主题使用
        ip.setOriginLink(request.getHeader("origin-referer"));
        return ip;
    }

    /**
     * 异步更新
     * 如果同Link下已有同IP不做数据库更新。数据库只存储初次访问
     *
     * @param ip ip信息
     */
    private void asyncSave(IP ip, Link link) {
        countForPage.execute(() -> {
            Integer linkId = link.getLinkId();
            Integer count = link.getCount();
            log.info("{}{} 访问链接 {} 存入缓存", ip.getIpLoc(), ip.getIpAddr(), link.getUrl());
            cacheStore.put(ip.hashCode(), ip);
            //新访问的链接直接count自增后存储即可。已存在的链接，需校验该ip是否存在，存在则跳过，不存在则count自增后存储。
            if (ObjectUtils.isEmpty(linkId)) {
                link.setCount(++count);
                linkRepository.save(link);
                ip.setLinkId(link.getLinkId());
                ipRepository.save(ip);
                log.info("=>新增链接 {}", link.getUrl());
            } else {
                List<IP> ipList = ipRepository.findByLinkIdAndIpAddr(linkId, ip.getIpAddr());
                if (ObjectUtils.isEmpty(ipList)) {
                    link.setCount(++count);
                    ipRepository.save(ip);
                    linkRepository.save(link);
                    log.info("=>链接 {} 访问次数 {}", link.getUrl(), link.getCount());
                }
            }
        });
    }


    @Override
    public Integer getTotalVisit(String linkUrl) {
        //获取当前线程绑定的request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null || attributes.getRequest() == null) {
            return -1;
        }
        if (!linkUrl.contains(domain)) {
            return -1;
        }
        //获取该link的访问统计信息
        Link link = findByUrl(linkUrl);
        //异步操作添加数量
        HttpServletRequest request = attributes.getRequest();
        //创建ip信息
        IP ip = createIP(request);
        //关联
        ip.setLinkId(link.getLinkId());
        //异步更新
        asyncSave(ip, link);
        return link.getCount() == 0 ? 1 : link.getCount();
    }


    @Override
    public List<IP> getRecentIP(int size) {
        return ipRepository.findAll(PageRequest.of(0, size, Sort.by("ipId").descending())).getContent();
    }

    @Override
    public List<IP> getTodayIP() {
        String format = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINA).format(LocalDate.now());
        String startTime = String.format("%s 00:00:00", format);
        String endTime = String.format("%s 23:59:59", format);
        return ipRepository.findByFirstVisitTimeBetween(startTime, endTime);
    }
}
