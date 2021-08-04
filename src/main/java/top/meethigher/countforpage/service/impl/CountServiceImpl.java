package top.meethigher.countforpage.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import top.meethigher.countforpage.dto.LocationInfo;
import top.meethigher.countforpage.dto.TopResponse;
import top.meethigher.countforpage.entity.IP;
import top.meethigher.countforpage.entity.Visit;
import top.meethigher.countforpage.repository.IPRepository;
import top.meethigher.countforpage.repository.VisitRepository;
import top.meethigher.countforpage.service.CountService;
import top.meethigher.countforpage.utils.HttpUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
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

    @Resource
    IPRepository ipRepository;

    @Resource
    RestTemplate restTemplate;

    private final static String GET_LOCATION_API = "http://ip-api.com/json/%s?lang=zh-CN";

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
        if (ObjectUtils.isEmpty(ips)) {
            IP ip = getFullIP(request);
            return update(count, ip, visit);
        }
        List<String> ipList = ips.stream().map(IP::getIp).collect(Collectors.toList());
        if (!ipList.contains(ipAddr)) {
            IP ip = getFullIP(request);
            return update(count, ip, visit);
        }
        return count;
    }

    /**
     * 获取截止日期，取今天的0点时间以前的时间
     * 用Calendar可以自动处理月初、年初的问题
     *
     * @return
     */
    public Date getStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
//        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        return calendar.getTime();
    }

    public Date getEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
//        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    @Async
    @Override
    public List<TopResponse> getTop() {
        Date currentTime = new Date();
        Date endTime = getEndTime(currentTime);
        Date startTime = getStartTime(currentTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<IP> list = ipRepository.findByFirstVisitTimeBetween(sdf.format(startTime), sdf.format(endTime));
        if (ObjectUtils.isEmpty(list)) {
            return null;
        }
        List<TopResponse> responses = new LinkedList<>();
        list.stream().forEach(x -> {
            TopResponse response = new TopResponse();
            response.setUrl(x.getVisit().getUrl());
            response.setFirstVisitTime(x.getFirstVisitTime());
            response.setIp(x.getIp());
            response.setUserAgent(x.getUserAgent());
            response.setLocation(x.getLocation());
            responses.add(response);
        });
        return responses;
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ip.setFirstVisitTime(sdf.format(new Date()));
        LocationInfo object = restTemplate.getForObject(String.format(GET_LOCATION_API, ip.getIp()), LocationInfo.class);
        String loc = object.getCountry() + object.getRegionName() + object.getCity();
        ip.setLocation(loc);
        return ip;
    }
}
