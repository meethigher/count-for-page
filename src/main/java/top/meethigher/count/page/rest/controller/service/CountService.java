package top.meethigher.count.page.rest.controller.service;

import top.meethigher.count.page.entity.IP;

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
     * 获取该link的访问数量
     *
     * @param linkUrl 页面地址
     * @return 数量
     */
    Integer getTotalVisit(String linkUrl);

    /**
     * 获取最新访问记录
     *
     * @param size 大小
     * @return 获取最新访问记录
     */
    List<IP> getRecentIP(int size);

    /**
     * 获取今天的ip
     *
     * @return 今天ip
     */
    List<IP> getTodayIP();
}

