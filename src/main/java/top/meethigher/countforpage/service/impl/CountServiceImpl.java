package top.meethigher.countforpage.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import top.meethigher.countforpage.entity.IP;
import top.meethigher.countforpage.entity.Visit;
import top.meethigher.countforpage.repository.IPRepository;
import top.meethigher.countforpage.repository.VisitRepository;
import top.meethigher.countforpage.service.CountService;
import top.meethigher.countforpage.utils.HttpUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Resource
    VisitRepository visitRepository;

    @Override
    public Integer getStatistic(HttpServletRequest request) {
        String url = HttpUtils.getUrl(request);
        return getStatistic(request, url);
    }

    @Override
    public Integer getStatistic(HttpServletRequest request, String url) {
        System.out.println(url);
        String ipAddr = HttpUtils.getIpAddr(request);
        Visit visit = verifyVisit(url);
        Set<IP> ips = visit.getIps();
        Integer count = visit.getCount();
        IP ip = getFullIP(request);
        if (ObjectUtils.isEmpty(ips)) {
            return update(count, ip, visit);
        }
        List<String> ipList = ips.stream().map(IP::getIp).collect(Collectors.toList());
        if (!ipList.contains(ipAddr)) {
            return update(count, ip, visit);
        }
        return count;
    }

    /**
     * 验证用户
     *
     * @param url
     * @return
     */
    public Visit verifyVisit(String url) {
        Visit visit = visitRepository.findByUrl(url);
        if (ObjectUtils.isEmpty(visit)) {
            visit = new Visit();
            visit.setCount(0);
            visit.setUrl(url);
            return visit;
        } else {
            return visit;
        }
    }

    /**
     * 进行访问人数的统计
     *
     * @param count
     * @param ip
     * @param visit
     * @return
     */
    public int update(int count, IP ip, Visit visit) {
        Set<IP> ips = new HashSet<>();
        ip.setVisit(visit);
        ips.add(ip);
        visit.setIps(ips);
        visit.setCount(++count);
        visitRepository.save(visit);
        return count;
    }

    /**
     * 获取IP对象
     *
     * @param request
     * @return
     */
    public IP getFullIP(HttpServletRequest request) {
        IP ip = new IP();
        ip.setIp(HttpUtils.getIpAddr(request));
        ip.setUserAgent(HttpUtils.getUserAgent(request));
        ip.setFirstVisitTime(LocalDateTime.now().toString());
        return ip;
    }
}
