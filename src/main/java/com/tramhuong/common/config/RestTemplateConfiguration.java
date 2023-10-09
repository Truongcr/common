package com.tramhuong.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    @Bean("systemUserRestTemplate")
    public RestTemplate systemUserRestTemplate() {
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }


}
