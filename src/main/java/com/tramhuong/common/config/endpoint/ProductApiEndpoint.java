package com.tramhuong.common.config.endpoint;

import com.tramhuong.common.integration.IApiEndpoint;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ProductApiEndpoint implements IApiEndpoint {

    @Value("http://localhost:9001/api/v1/product")
    private String url;
}
