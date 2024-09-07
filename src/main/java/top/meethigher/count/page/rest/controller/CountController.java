package top.meethigher.count.page.rest.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import top.meethigher.count.page.entity.IP;
import top.meethigher.count.page.rest.controller.service.CountService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CountController
 *
 * @author kit chen
 * @github https://github.com/meethigher
 * @blog https://meethigher.top
 * @time 2021/7/19
 */
@RestController
public class CountController {

    @Resource
    private CountService countService;

    @Resource
    private HttpServletRequest httpServletRequest;

    @Value("${protocol}")
    private String protocol;

    @Value("${domain}")
    private String domain;

    @PostMapping("/count")
    public Integer linkCount(@RequestBody String linkUrl) {
        return countService.getTotalVisit(linkUrl);
    }

    @GetMapping("/recentIp/{size}")
    public List<IP> recentIp(@PathVariable("size") int size) {
        return countService.getRecentIP(size);
    }

    @GetMapping("/todayIp")
    public List<IP> todayIP() {
        return countService.getTodayIP();
    }

    @GetMapping("/headers")
    public Object headers() {
        Map<String, String> headerMap = new LinkedHashMap<>();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headerMap.put(name, httpServletRequest.getHeader(name));
        }
        return headerMap;
    }


    @RequestMapping(value = "/*", method = {
            RequestMethod.GET,
            RequestMethod.HEAD,
            RequestMethod.POST,
            RequestMethod.PUT,
            RequestMethod.PATCH,
            RequestMethod.DELETE,
            RequestMethod.OPTIONS,
            RequestMethod.TRACE
    })
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect(protocol + "://" + domain);
    }

}
