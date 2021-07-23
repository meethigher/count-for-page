package top.meethigher.countforpage.service;

import top.meethigher.countforpage.dto.TopResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * CountService
 *
 * @author kit chen
 * @github https://github.com/meethigher
 * @blog https://meethigher.top
 * @time 2021/7/19
 */
public interface CountService {
    /**
     * 统计
     * @param request
     * @return
     */
    Integer getStatistic(HttpServletRequest request);

    /**
     * 统计指定url
     * @param request
     * @param url
     * @return
     */
    Integer getStatistic(HttpServletRequest request,String url);

    /**
     * 获取最新访问记录
     * @return
     */
    List<TopResponse> getTop();
}
