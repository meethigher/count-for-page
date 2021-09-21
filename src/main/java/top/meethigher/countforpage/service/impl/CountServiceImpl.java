package top.meethigher.countforpage.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import top.meethigher.countforpage.utils.Utils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
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

    private static final Logger log = LoggerFactory.getLogger(CountServiceImpl.class);


    @Resource
    VisitRepository visitRepository;

    @Resource
    IPRepository ipRepository;

    @Resource
    RestTemplate restTemplate;

    /**
     * 通过ip获取详细信息api
     */
    private final static String GET_LOCATION_API = "http://ip-api.com/json/%s?lang=zh-CN";


    @Override
    public Integer getStatistic(HttpServletRequest request) {
        String url = Utils.getUrl(request);
        return getStatistic(request, url);
    }

    /**
     * 之前的做法，导致接口访问太慢了。
     * 现在的做法是直接返回上次的数据，本次的更新操作、ip信息的查询交给异步线程后台执行。
     *
     * @param request
     * @param url
     * @return
     */
    @Override
    public Integer getStatistic(HttpServletRequest request, String url) {
        System.out.println(url);
        SimpleDateFormat sdf = Utils.sdfThreadLocal.get();

        System.out.println(sdf.format(new Date()) + " start");

        Visit visit = verifyVisit(url);
        System.out.println(sdf.format(new Date()) + " verifyVisit");


        Integer count = visit.getCount();
        System.out.println(sdf.format(new Date()) + " getCount");

        asyncAsync(url, request);

        System.out.println(sdf.format(new Date()) + " returnCount");
        //防止太难看
        return count == 0 ? 1 : count;
    }

    /**
     * 进行访问人数的统计
     *
     * @param ip
     * @param visit
     * @return
     */
    private int update(IP ip, Visit visit) {
        Set<IP> ips = new HashSet<>();
        ip.setVisit(visit);
        ips.add(ip);
        visit.setIps(ips);
        Integer count = visit.getCount();
        visit.setCount(++count);
        visitRepository.save(visit);
        SimpleDateFormat sdf = Utils.sdfThreadLocal.get();
        return count;
    }


    /**
     * 异步执行数据更新，可以加快接口访问速度
     *
     * @author chenchuancheng
     * @since 2021/9/20 16:48
     */
    private void asyncAsync(String url, HttpServletRequest request) {
        SimpleDateFormat sdf = Utils.sdfThreadLocal.get();
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(new Supplier<Integer>() {
            @Override
            public Integer get() {
                Visit visit = verifyVisit(url);
                Integer count = visit.getCount();
                //之所有不用visit.getIp()，是因为在异步线程里会有懒加载问题，具体为啥不知道。
                List<String> ipList = ipRepository.findIpByVid(visit.getvId());
                if (ObjectUtils.isEmpty(ipList)) {
                    IP ip = getFullIP(url, request);
                    return update(ip, visit);
                }
                if (!ipList.contains(Utils.getIpAddr(request))) {
                    IP ip = getFullIP(url, request);
                    return update(ip, visit);
                }
                return count;
            }
        });
        //future成功后的回调
        future.thenAccept(integer -> System.out.println(sdf.format(new Date()) + " success"));
        //future异常后的回调。这个必须要有，不然即使有异常也没有日志。
        future.exceptionally(throwable -> {
            throwable.printStackTrace();
            System.out.println(sdf.format(new Date()) + " failure");
            return null;
        });
    }


    /**
     * 获取截止日期，取今天的00:00:01
     * 用Calendar可以自动处理月初、年初的问题
     *
     * @return
     */
    private Date getStartTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
//        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 1);
        return calendar.getTime();
    }

    /**
     * 获取截止日期，取当天时间的23:59:59
     *
     * @param date
     * @return
     */
    private Date getEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
//        calendar.add(Calendar.DATE, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    @Override
    public List<TopResponse> getTop() {
        Date currentTime = new Date();
        Date endTime = getEndTime(currentTime);
        Date startTime = getStartTime(currentTime);
        SimpleDateFormat sdf = Utils.sdfThreadLocal.get();
        List<IP> list = ipRepository.findByFirstVisitTimeBetween(sdf.format(startTime), sdf.format(endTime));
        if (ObjectUtils.isEmpty(list)) {
            return null;
        }
        List<TopResponse> responses = new LinkedList<>();
        list.stream().forEach(x -> {
            TopResponse response = new TopResponse();
            response.setUrl(x.getOriginUrl());
            response.setFirstVisitTime(x.getFirstVisitTime());
            response.setIp(x.getIp());
            response.setUserAgent(x.getUserAgent());
            response.setLocation(x.getLocation());
            response.setOriginReferer(x.getOriginReferer());
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
    private Visit verifyVisit(String url) {
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
     * 获取IP对象，里面存储访问者的详细信息
     *
     * @param url
     * @param request
     * @return
     */
    private IP getFullIP(String url, HttpServletRequest request) {
        IP ip = new IP();
        ip.setIp(Utils.getIpAddr(request));
        ip.setUserAgent(Utils.getUserAgent(request));
        SimpleDateFormat sdf = Utils.sdfThreadLocal.get();
        ip.setFirstVisitTime(sdf.format(new Date()));
        ip.setOriginReferer(request.getHeader("origin-referer"));
        ip.setOriginUrl(url);
        //通过第三方api获取ip的详细信息
        LocationInfo object = restTemplate.getForObject(String.format(GET_LOCATION_API, ip.getIp()), LocationInfo.class);
        String loc = object.getCountry() + object.getRegionName() + object.getCity();
        ip.setLocation(loc);
        return ip;
    }
}
