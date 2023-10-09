package com.tramhuong.common.integration.api;

import com.tramhuong.common.integration.ApiIntegrationBuilder;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@AllArgsConstructor
public class ProductApiIntegration {

    private static ProductApiIntegration instance;
    private final ApiIntegrationBuilder apiIntegrationBuilder;

    public static ProductApiIntegration get() {
        return instance;
    }

    @PostConstruct
    void setInstance() {
        setInstance(this);
    }

    private static void setInstance(ProductApiIntegration instance) {
        ProductApiIntegration.instance = instance;
    }
    public String demo() {
        return apiIntegrationBuilder.get("http://localhost:9001/api/v1/product/demo", String.class);
    }
}
