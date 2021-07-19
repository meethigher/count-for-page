package top.meethigher.countforpage.controller;

import org.springframework.web.bind.annotation.*;
import top.meethigher.countforpage.service.CountService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
    CountService countService;

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.HEAD, RequestMethod.PUT}, value = {"/*"})
    public String root() {
        return "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width,user-scalable=no,initial-scale=1,maximum-scale=1,minimum-scale=1\"><title>言成言成啊</title><link rel=\"shortcut icon\" href=\"https://meethigher.top/images/favicon.ico\"><link rel=\"stylesheet\" href=\"https://meethigher.top/css/index.css\"><style>*{margin:0;padding:0}.error{display:flex;position:fixed;top:0;bottom:0;left:0;right:0}error{margin:auto;text-align:center}error span{padding:0 10px}div{font-size:20px}</style></head><body><div class=\"error\"><error><div>你好像迷路了！</div><div>等待<span>3</span>秒后自动跳转到首页</div><div>有问题有直接发送邮件到&nbsp;meethigher@qq.com</div></error></div><div id=\"particles-js\"></div><script src=\"https://cdn.jsdelivr.net/gh/meethigher/cdn@9.0/js/jquery.min.js\"></script><script src=\"https://cdn.jsdelivr.net/gh/meethigher/cdn@9.0/js/particles.min.js\"></script><script src=\"https://cdn.jsdelivr.net/gh/meethigher/cdn@9.0/js/app.js\"></script><script>$span = $(\"span\");let i = $span.text();let interval = setInterval(function() {if (i <= 0) {clearInterval(interval);window.location.href = \"https://meethigher.top/\";} else {i--;$span.text(i);}}, 1000);</script><script>window.imageLazyLoadSetting={isSPA:!1,processImages:null}</script><script>window.addEventListener(\"load\",function(){var a=/\\.(gif|jpg|jpeg|tiff|png)$/i,e=/^data:image\\/[a-z]+;base64,/;Array.prototype.slice.call(document.querySelectorAll(\".blog-markdown img[data-original]\")).forEach(function(t){var r=t.parentNode;\"A\"===r.tagName&&(r.href.match(a)||r.href.match(e))&&(r.href=t.dataset.original)})})</script><script>!function(t){function e(){n&&(o=Array.prototype.slice.call(document.querySelectorAll(\"img[data-original]\")));for(var e,i,r=0;r<o.length;r++)e=o[r],0<=(i=e.getBoundingClientRect()).bottom&&0<=i.left&&i.top<=(t.innerHeight||document.documentElement.clientHeight)&&function(){var t,e,n,i,a=o[r];t=a,e=function(){o=o.filter(function(t){return a===t&&t.removeAttribute(\"style\"),a!==t})},n=new Image,i=t.getAttribute(\"data-original\"),n.onload=function(){t.src=i,e&&e()},n.src=i}()}t.imageLazyLoadSetting.processImages=e;var n=t.imageLazyLoadSetting.isSPA,o=Array.prototype.slice.call(document.querySelectorAll(\".blog-markdown img[data-original]\"));e(),t.addEventListener(\"scroll\",function(){var n,o;n=e,o=t,clearTimeout(n.tId),n.tId=setTimeout(function(){n.call(o)},500)})}(this)</script></body></html>";
    }

    @PostMapping(value = "/count")
    public Integer getStatisticsByUrl(HttpServletRequest request, @RequestBody String url) {
        return countService.getStatistic(request, url);
    }
}
