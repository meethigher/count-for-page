package top.meethigher.countforpage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CountForPageApplication {

    public static void main(String[] args) {
        SpringApplication.run(CountForPageApplication.class, args);
    }

}
