package dk.emilmadsen.deluminator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeluminatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeluminatorApplication.class, args);
	}

}
