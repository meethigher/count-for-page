package top.meethigher.count.page.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import top.meethigher.count.page.entity.IP;
import top.meethigher.count.page.rest.controller.service.CountService;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * StatisticTask
 *
 * @author kit chen
 * @github https://github.com/meethigher
 * @blog https://meethigher.top
 * @time 2021/7/20
 */
@Component
public class StatisticTask {
    private static Logger log = LoggerFactory.getLogger(StatisticTask.class);

    @Resource
    JavaMailSender javaMailSender;
    @Value("${receiver}")
    String mailTo;
    @Value("${spring.mail.username}")
    String mailFrom;
    @Resource
    CountService countService;

    @Async("countForPage")
    @Scheduled(cron = "0 59 23 * * ? ")
    public void countByDay() {
        try {
            List<IP> responses = countService.getTodayIP();
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            String time = new SimpleDateFormat("MM月dd日").format(new Date());
            mimeMessageHelper.setSubject(time + "访问量" + responses.size() + "次");
            mimeMessageHelper.setTo(mailTo);
            mimeMessageHelper.setFrom(mailFrom);
            mimeMessage.setContent(generateHtml(responses), "text/html;charset=utf-8");
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 生成html邮件
     *
     * @param responses
     * @return
     */
    public String generateHtml(List<IP> responses) {
        if (ObjectUtils.isEmpty(responses)) {
            throw new RuntimeException("没有查到数据");
        }
        StringBuilder builder = new StringBuilder("<html><body><table border=\"1\"><tbody><thead>" +
                "<tr><th>url</th><th>来源</th><th>ip</th><th>位置</th><th>时间</th><th>设备</th></thead>");
        responses.forEach(x -> {
            String pattern = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
            String row = String.format(pattern, x.getTargetLink(), x.getOriginLink(), x.getIpAddr(), x.getIpLoc(), x.getFirstVisitTime(), x.getDevice());
            builder.append(row);
        });
        builder.append("</tbody></table></body></html>");
        return builder.toString();
    }
}
