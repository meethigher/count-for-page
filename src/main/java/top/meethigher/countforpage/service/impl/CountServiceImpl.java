package top.meethigher.countforpage.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.meethigher.countforpage.dto.SaveInfo;
import top.meethigher.countforpage.dto.TopResponse;
import top.meethigher.countforpage.entity.IP;
import top.meethigher.countforpage.entity.Visit;
import top.meethigher.countforpage.repository.IPRepository;
import top.meethigher.countforpage.repository.VisitRepository;
import top.meethigher.countforpage.service.CountService;
import top.meethigher.countforpage.utils.Utils;
import top.meethigher.czip.IPSearcher;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
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


    @Resource
    private VisitRepository visitRepository;

    @Resource
    private IPRepository ipRepository;

    @Resource
    private ExecutorService asyncExecutor;

    @Resource
    private RestTemplate restTemplate;

    /**
     * 通过ip获取详细信息api
     */
    private final static String GET_LOCATION_API = "http://ip-api.com/json/%s?lang=zh-CN";


    /**
     * 之前的做法，导致接口访问太慢了。
     * 现在的做法是直接返回上次的数据，本次的更新操作、ip信息的查询交给异步线程后台执行。
     *
     * @param url
     * @return
     */
    @Override
    public Integer getUserView(String url) {
        //获取当前线程绑定的request
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return 4444;
        }
        HttpServletRequest request = attributes.getRequest();
        if (url.contains("localhost") || url.contains("127.0.0.1")) {
            return 9999;
        }

        Visit visit = verifyVisit(url);


        Integer count = visit.getCount();

        //异步执行数据更新，可以加快接口访问速度
        //使用注解Async同样可以做到该功能，不过他的异步，需要在另一个bean里面定义异步方法。懒得再写一个类了。
        SaveInfo saveInfo = new SaveInfo(url, Utils.getUserAgent(request), Utils.getOriginReferer(request), Utils.getIpAddr(request));
        asyncExecutor.execute(() -> {
            Visit visit1 = verifyVisit(saveInfo.getUrl());
            Integer count1 = visit1.getCount();
            //之所有不用visit.getIp()，是因为在异步线程里会有懒加载问题，具体为啥不知道(异步的话，连接已经被关闭了)。
            List<String> ipList = ipRepository.findIpByVid(visit1.getvId());
            if (ObjectUtils.isEmpty(ipList)) {
                IP ip = getFullIP(url, saveInfo);
                count1 = update(ip, visit1);
            } else {
                if (!ipList.contains(saveInfo.getIp())) {
                    IP ip = getFullIP(url, saveInfo);
                    count1 = update(ip, visit1);
                }
            }
            log.info("{}--success 最新访问数{}", saveInfo.getUrl(), count1);
        });
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
        return count;
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
        list.forEach(x -> {
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


//    /**
//     * 获取IP对象，里面存储访问者的详细信息
//     *
//     * @param url
//     * @param info
//     * @return
//     */
//    private IP getFullIP(String url, SaveInfo info) {
//        IP ip = new IP();
//        ip.setIp(info.getIp());
//        ip.setUserAgent(info.getUserAgent());
//        SimpleDateFormat sdf = Utils.sdfThreadLocal.get();
//        ip.setFirstVisitTime(sdf.format(new Date()));
//        ip.setOriginReferer(info.getOriginReferer());
//        ip.setOriginUrl(url);
//        try{
//            //通过第三方api获取ip的详细信息
//            LocationInfo object = restTemplate.getForObject(String.format(GET_LOCATION_API, ip.getIp()), LocationInfo.class);
//            String loc = object.getCountry() + object.getRegionName() + object.getCity();
//            ip.setLocation(loc);
//        }catch (Exception e){
//            ip.setLocation("调用接口获取失败");
//        }
//        return ip;
//    }

    /**
     * 通过纯真ip数据库获取ip
     *
     * @param url
     * @param info
     * @return
     */
    private IP getFullIP(String url, SaveInfo info) {
        IP ip = new IP();
        ip.setIp(info.getIp());
        ip.setUserAgent(info.getUserAgent());
        SimpleDateFormat sdf = Utils.sdfThreadLocal.get();
        ip.setFirstVisitTime(sdf.format(new Date()));
        ip.setOriginReferer(info.getOriginReferer());
        ip.setOriginUrl(url);
        //调用纯真ip获取ip
        String location = IPSearcher.getInstance().search(ip.getIp());
        ip.setLocation(location);
        return ip;
    }
}
