package top.meethigher.countforpage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import top.meethigher.countforpage.dto.Header;
import top.meethigher.countforpage.dto.TopResponse;
import top.meethigher.countforpage.service.CountService;
import top.meethigher.countforpage.utils.Utils;
import top.meethigher.czip.IPSearcher;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * HtmlController
 *
 * @author chenchuancheng
 * @github https://github.com/meethigher
 * @blog https://meethigher.top
 * @time 2021/8/4 23:48
 */
@Controller
public class HtmlController {

    @Resource
    private CountService countService;

    @Resource
    private ExecutorService asyncExecutor;

    @GetMapping(value = "/today")
    public String today(ModelMap map) {
        List<TopResponse> top = countService.getTop();
        if (!ObjectUtils.isEmpty(top)) {
            String time = new SimpleDateFormat("MM月dd日").format(new Date());
            map.put("title", time + "统计" + top.size() + "条");
            map.put("today", top);
        }
        return "/index";
    }

    @GetMapping(value = "/head")
    public void getHeader(HttpServletRequest request, ModelMap map) {
        List<Header> list = new LinkedList<>();
        Header ip = new Header("ip", Utils.getIpAddr(request));
        Header ipLoc = new Header("ip-loc", IPSearcher.getInstance().search(ip.getValue()));
        list.add(ip);
        list.add(ipLoc);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String header = request.getHeader(key);
            Header dto = new Header(key, header);
            list.add(dto);
        }
        map.put("title", "请求信息");
        map.put("headList", list);
    }

    @GetMapping("/thread")
    public void getThread(ModelMap map) {
        ThreadPoolExecutor asyncExecutor = (ThreadPoolExecutor) this.asyncExecutor;
        map.put("queueNumber", asyncExecutor.getQueue().size());
        map.put("activeNumber", asyncExecutor.getActiveCount());
        map.put("completedNumber", asyncExecutor.getCompletedTaskCount());
        map.put("allNumber", asyncExecutor.getTaskCount());
        map.put("remainingCapacity", asyncExecutor.getQueue().remainingCapacity());
        map.put("maxNumber", asyncExecutor.getMaximumPoolSize());
        map.put("realNumber",asyncExecutor.getPoolSize());
    }


}
