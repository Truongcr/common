package com.tramhuong.common.integration;

import com.tramhuong.common.constant.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@Slf4j
public class ApiIntegration<T> {

    private String url;
    private HttpMethod httpMethod;
    private Object body;
    private List<Object> params;
    private HttpHeaders headers;
    private Class<T> type;
    private ParameterizedTypeReference<T> typeReference;
    private RestTemplate restTemplate;

    private MultiValueMap<String, String> buildParams() {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        if (this.params == null || this.params.isEmpty()) {
            return parameters;
        }
        for (int paramIndex = 0; paramIndex < this.params.size(); paramIndex += 2) {
            Object key = this.params.get(paramIndex);
            Object values = this.params.get(paramIndex + 1);
            if (key != null && ((paramIndex + 1) < this.params.size() && values != null)) {
                if (values instanceof Collection) {
                    parameters.put(key.toString(), ((Collection<?>) values).stream()
                            .filter(Objects::nonNull)
                            .map(Object::toString).collect(Collectors.toList()));

                } else if (values instanceof EnumInterface) {
                    parameters.put(key.toString(), Collections.singletonList(((EnumInterface) values).getValue()));
                } else {
                    parameters.put(key.toString(), Collections.singletonList(values.toString()));
                }
            }
        }
        return parameters;
    }

    public ResponseEntity<T> exchange() {
        return this.restTemplate.exchange(buildExchangeUrl(), this.httpMethod, buildRequest(), this.typeReference);
    }

    public ResponseEntity<T> exchangeWithType() {
        return this.restTemplate.exchange(buildExchangeUrl(), this.httpMethod, buildRequest(), type);
    }

    private String buildExchangeUrl() {
//        return "http://localhost:9001/api/v1/product/demo";
        String a = IntegrationSecuritySupporter.validateEndpoint(UriComponentsBuilder.fromHttpUrl(this.url).queryParams(this.buildParams()).build());;
        return IntegrationSecuritySupporter.validateEndpoint(UriComponentsBuilder.fromHttpUrl(this.url).queryParams(this.buildParams()).build());
    }

    private HttpEntity<Object> buildRequest() {
        final HttpEntity<Object> request;
        if (headers != null) {
            request = new HttpEntity<>(this.body, headers);
        } else {
            request = new HttpEntity<>(this.body);
        }
        return request;
    }
}