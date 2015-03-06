package com.github.lemniscate.hipstack;

import com.github.lemniscate.spring.typehint.beans.TypeHintListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

@SpringBootApplication
public class HipstackApplication {

    public static void main(String[] args) {
        new SpringApplication(HipstackApplication.class){
            @Override
            protected ConfigurableApplicationContext createApplicationContext() {
                ConfigurableApplicationContext result = super.createApplicationContext();
                TypeHintListableBeanFactory.hijack((AbstractApplicationContext) result);
                return result;
            }
        }.run(args);
    }
}
