package top.meethigher.count.page.rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import top.meethigher.cache.CacheStore;
import top.meethigher.count.page.entity.IP;
import top.meethigher.count.page.rest.controller.service.CountService;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 页面展示内容
 *
 * @author chenchuancheng
 * @github https://github.com/meethigher
 * @blog https://meethigher.top
 * @time 2021/8/4 23:48
 */
@Controller
public class HTMLController {

    @Resource
    private CountService countService;

    @Resource
    private ExecutorService asyncExecutor;

    @Resource
    private CacheStore<Integer, IP> cacheStore;

    @GetMapping(value = "/today")
    public String today(ModelMap map) {
        List<IP> top = countService.getTodayIP();
        if (!ObjectUtils.isEmpty(top)) {
            String time = DateTimeFormatter.ofPattern("MM月dd日").format(LocalDate.now());
            map.put("title", time + "统计" + top.size() + "条");
            map.put("today", top);
        }
        return "/index";
    }

    @GetMapping("realTime")
    public String realTime(ModelMap map) {
        Collection<IP> values = cacheStore.toMap().values();
        if (!ObjectUtils.isEmpty(values)) {
            map.put("title", String.format("前%s条访问记录", values.size()));
            map.put("today", values);
        }
        return "/index";
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
        map.put("realNumber", asyncExecutor.getPoolSize());
    }

}
