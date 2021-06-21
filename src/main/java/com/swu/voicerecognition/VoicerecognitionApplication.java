package com.swu.voicerecognition;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.swu.voicerecognition"})
@MapperScan("com.swu.voicerecognition.dao")
public class VoicerecognitionApplication {

    public static void main(String[] args) {
        SpringApplication.run(VoicerecognitionApplication.class, args);
    }

}
