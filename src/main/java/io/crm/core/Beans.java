package io.crm.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.crm.core.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class Beans {

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(App.getDefaultDateFormat());
        return objectMapper;
    }

    public static void main(String... args) {
        App.testRun();
    }
}
