package top.meethigher.countforpage.sheduling;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import top.meethigher.countforpage.dto.TopResponse;
import top.meethigher.countforpage.service.CountService;

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
    @Resource
    JavaMailSender javaMailSender;
    @Value("${mail.to}")
    String mailTo;
    @Value("${spring.mail.username}")
    String mailFrom;
    @Resource
    CountService countService;

    @Async
    @Scheduled(cron = "0 0 8 * * ? ")
    public void countByDay() {
        List<TopResponse> responses = countService.getTop();
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            String time = new SimpleDateFormat("MM月dd日").format(new Date());
            mimeMessageHelper.setSubject(time+"访问量"+responses.size()+"次");
            mimeMessageHelper.setTo(mailTo);
            mimeMessageHelper.setFrom(mailFrom);
            mimeMessage.setContent(generateHtml(responses), "text/html;charset=utf-8");
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public String generateHtml(List<TopResponse> responses) {
        if (ObjectUtils.isEmpty(responses)) {
            throw new RuntimeException("没有查到数据");
        }
        StringBuilder builder = new StringBuilder("<html><body><table border=\"1\"><tbody><thead>" +
                "<tr><th>url</th><th>ip</th><th>位置</th><th>时间</th><th>来源</th><th>设备</th></thead>");
        responses.stream().forEach(x -> {
            String pattern = "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>";
            String row = String.format(pattern, x.getUrl(), x.getIp(), x.getLocation(), x.getFirstVisitTime(), x.getReferer(), x.getUserAgent());
            builder.append(row);
        });
        builder.append("</tbody></table></body></html>");
        return builder.toString();
    }

    public String generateText(List<TopResponse> responses) {
        if (ObjectUtils.isEmpty(responses)) {
            return "今日没有访问";
        }
        StringBuilder stringBuilder = new StringBuilder(responses.get(0).toString());
        for (int i = 0; i < responses.size(); i++) {
            if (i == 0) {
                continue;
            }
            stringBuilder.append("\n" + responses.get(i));
        }
        return stringBuilder.toString();
    }
}
