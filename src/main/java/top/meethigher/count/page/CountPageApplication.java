package top.meethigher.count.page;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableScheduling
public class CountPageApplication {

    public static void main(String[] args) {
        SpringApplication.run(CountPageApplication.class, args);
    }

}
