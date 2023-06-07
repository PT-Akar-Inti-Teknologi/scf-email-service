package bca.mbb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FoEmailConsumer {

  public static void main(String[] args) {
    SpringApplication.run(FoEmailConsumer.class, args);
  }
}
