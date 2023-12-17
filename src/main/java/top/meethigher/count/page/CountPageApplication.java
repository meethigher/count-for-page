package top.meethigher.count.page;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CountPageApplication {

    public static void main(String[] args) {
        SpringApplication.run(CountPageApplication.class, args);
    }

}
