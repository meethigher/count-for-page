package top.meethigher.countforpage.service;

import javax.servlet.http.HttpServletRequest;

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
}
