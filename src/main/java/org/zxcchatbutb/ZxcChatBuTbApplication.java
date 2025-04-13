package org.zxcchatbutb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;

@SpringBootApplication
public class ZxcChatBuTbApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZxcChatBuTbApplication.class, args);
    }

}
