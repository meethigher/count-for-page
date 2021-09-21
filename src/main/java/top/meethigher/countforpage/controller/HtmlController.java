package top.meethigher.countforpage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import top.meethigher.countforpage.dto.TopResponse;
import top.meethigher.countforpage.service.CountService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    @Autowired
    CountService countService;
    @GetMapping(value="/today")
    public String today(ModelMap map){
        List<TopResponse> top = countService.getTop();
        if(!ObjectUtils.isEmpty(top)){
            String time = new SimpleDateFormat("MM月dd日").format(new Date());
            map.put("title",time+"统计"+top.size()+"条");
            map.put("today",top);
        }
        return "/index";
    }
}
