package top.meethigher.countforpage.service;

import top.meethigher.countforpage.dto.TopResponse;

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
     * UserView即uv
     *
     * @param url
     * @return
     */
    Integer getUserView(String url);

    /**
     * 获取最新访问记录
     *
     * @return
     */
    List<TopResponse> getTop();
}
