package com.tramhuong.common.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.tramhuong.common.response.PagingResponseModel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Component
public class ApiIntegrationBuilder {


    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    private final RestTemplate systemUserRestTemplate;

    public ApiIntegrationBuilder(RestTemplate restTemplate, ObjectMapper objectMapper, RestTemplate systemUserRestTemplate)
                                 {
        super();
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.systemUserRestTemplate = systemUserRestTemplate;
    }

    @PostConstruct
    private void configureDefaultRestTemplate() {
        this.restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

    public <T> T get(String url, Class<T> t) {
        return get(url, null, null, t);
    }

    public <T> T get(String url, HttpHeaders httpHeaders, Class<T> t) {
        return get(url, null, httpHeaders, t);
    }

    public <T> T get(String url, List<Object> params, Class<T> t) {
        return get(url, params, null, t);
    }

    public <T> T get(String url, List<Object> params, HttpHeaders httpHeaders, Class<T> t) {
        final ApiIntegration<T> apiIntegration = ApiIntegration.<T>builder()
                .url(url)
                .httpMethod(HttpMethod.GET)
                .headers(httpHeaders)
                .params(params)
                .type(t)
                .restTemplate(this.restTemplate)
                .build();
        return apiIntegration.exchangeWithType().getBody();
    }

    public <T> T delete(String url, List<Object> params, Class<T> t) {
        return update(url, params, null, null, t, HttpMethod.DELETE);
    }

    public <T> T getInternal(String url, List<Object> params, HttpHeaders httpHeaders, Class<T> t) {
        final ApiIntegration<T> apiIntegration = ApiIntegration.<T>builder()
                .url(url)
                .httpMethod(HttpMethod.GET)
                .headers(httpHeaders)
                .params(params)
                .type(t)
                .restTemplate(this.systemUserRestTemplate)
                .build();
        return apiIntegration.exchangeWithType().getBody();
    }

    public <T> T getInternal(String url, Class<T> t) {
        final ApiIntegration<T> apiIntegration = ApiIntegration.<T>builder()
                .url(url)
                .httpMethod(HttpMethod.GET)
                .type(t)
                .restTemplate(this.systemUserRestTemplate)
                .build();
        return apiIntegration.exchangeWithType().getBody();
    }

    public <T> T postInternal(String url, Object body, HttpHeaders httpHeaders, Class<T> t) {
        final ApiIntegration<T> apiIntegration = ApiIntegration.<T>builder()
                .url(url)
                .httpMethod(HttpMethod.POST)
                .headers(httpHeaders)
                .body(body)
                .type(t)
                .restTemplate(this.systemUserRestTemplate)
                .build();
        return apiIntegration.exchangeWithType().getBody();
    }

    public <T> T postInternal(String url, List<Object> params, Object body, HttpHeaders httpHeaders, Class<T> t) {
        final ApiIntegration<T> apiIntegration = ApiIntegration.<T>builder()
                .url(url)
                .httpMethod(HttpMethod.POST)
                .headers(httpHeaders)
                .params(params)
                .body(body)
                .type(t)
                .restTemplate(this.systemUserRestTemplate)
                .build();
        return apiIntegration.exchangeWithType().getBody();
    }

    @SuppressWarnings("unchecked")
    public <T> PagingResponseModel<T> getForPage(String url, List<Object> params, Class<?> t) {
        final ApiIntegration<PagingResponseModel<T>> apiIntegration = ApiIntegration.<PagingResponseModel<T>>builder()
                .url(url)
                .httpMethod(HttpMethod.GET)
                .params(params)
                .typeReference(new ParameterizedTypeReference<PagingResponseModel<T>>() {
                })
                .restTemplate(this.restTemplate)
                .build();
        PagingResponseModel<T> responseModel = apiIntegration.exchange().getBody();
        if (null == responseModel) {
            return new PagingResponseModel<>(null);
        }
        List<T> ts = responseModel.getRows().stream().map(t1 -> (T) objectMapper.convertValue(t1, t)).collect(Collectors.toList());
        responseModel.setRows(ts);
        return responseModel;
    }

    public <T> PagingResponseModel<T> getForPage(String url, ParameterizedTypeReference<PagingResponseModel<T>> typeReference) {
        final ApiIntegration<PagingResponseModel<T>> apiIntegration = ApiIntegration.<PagingResponseModel<T>>builder()
                .url(url)
                .httpMethod(HttpMethod.GET)
                .typeReference(typeReference)
                .restTemplate(this.restTemplate)
                .build();
        return apiIntegration.exchange().getBody();
    }

    public <T> PagingResponseModel<T> getForPageInternal(String url, ParameterizedTypeReference<PagingResponseModel<T>> typeReference) {
        final ApiIntegration<PagingResponseModel<T>> apiIntegration = ApiIntegration.<PagingResponseModel<T>>builder()
                .url(url)
                .httpMethod(HttpMethod.GET)
                .typeReference(typeReference)
                .restTemplate(this.systemUserRestTemplate)
                .build();
        return apiIntegration.exchange().getBody();
    }

    public <T> List<T> getForList(String url, ParameterizedTypeReference<List<T>> t) {
        return getForList(url, null, null, t);
    }


    public <T> List<T> getForList(String url, List<Object> params, ParameterizedTypeReference<List<T>> t) {
        return getForList(url, null, params, t);
    }

    public <T> Set<T> getForSet(String url, List<Object> params, ParameterizedTypeReference<Set<T>> t) {
        final ApiIntegration<Set<T>> apiIntegration = ApiIntegration.<Set<T>>builder()
                .url(url)
                .httpMethod(HttpMethod.GET)
                .params(params)
                .typeReference(t)
                .restTemplate(this.restTemplate)
                .build();
        return apiIntegration.exchange().getBody();
    }

    public <T> List<T> getForList(String url, HttpHeaders httpHeaders, List<Object> params, ParameterizedTypeReference<List<T>> t) {
        final ApiIntegration<List<T>> apiIntegration = ApiIntegration.<List<T>>builder()
                .url(url)
                .headers(httpHeaders)
                .httpMethod(HttpMethod.GET)
                .params(params)
                .typeReference(t)
                .restTemplate(this.restTemplate)
                .build();
        return apiIntegration.exchange().getBody();
    }

    public <T> List<T> getForListInternal(String url, HttpHeaders httpHeaders, List<Object> params, ParameterizedTypeReference<List<T>> t) {
        final ApiIntegration<List<T>> apiIntegration = ApiIntegration.<List<T>>builder()
                .url(url)
                .headers(httpHeaders)
                .httpMethod(HttpMethod.GET)
                .params(params)
                .typeReference(t)
                .restTemplate(this.systemUserRestTemplate)
                .build();
        return apiIntegration.exchange().getBody();
    }

    public <T, U> Map<T, U> getForMap(String url, List<Object> params, ParameterizedTypeReference<Map<T, U>> t) {
        final ApiIntegration<Map<T, U>> apiIntegration = ApiIntegration.<Map<T, U>>builder()
                .url(url)
                .httpMethod(HttpMethod.GET)
                .params(params)
                .typeReference(t)
                .restTemplate(this.restTemplate)
                .build();
        return apiIntegration.exchange().getBody();
    }

    public <T> T post(String url, Class<T> t) {
        return post(url, null, null, null, t);
    }

    public <T> T post(String url, List<Object> params, Class<T> t) {
        return post(url, params, null, null, t);
    }

    public <T> T post(String url, Object body, Class<T> t) {
        return post(url, null, body, null, t);
    }
    public <T> T postBySystemUser(String url, Object body, Class<T> t) {
        return postBySystemUser(url, null, body, null, t);
    }

    public <T> T post(String url, HttpHeaders httpHeaders, Class<T> t) {
        return post(url, null, null, httpHeaders, t);
    }

    public <T> T post(String url, List<Object> params, Object body, Class<T> t) {
        return post(url, params, body, null, t);
    }

    public <T> T postBySystemUser(String url, List<Object> params, Object body, Class<T> t) {
        return postBySystemUser(url, params, body, null, t);
    }

    public <T> T post(String url, List<Object> params, HttpHeaders httpHeaders, Class<T> t) {
        return post(url, params, null, httpHeaders, t);
    }

    public <T> T post(String url, Object body, HttpHeaders httpHeaders, Class<T> t) {
        return post(url, null, body, httpHeaders, t);
    }

    public <T> T post(String url, List<Object> params, Object body, HttpHeaders httpHeaders, Class<T> t) {
        return update(url, params, body, httpHeaders, t, HttpMethod.POST);
    }

    public <T> T postBySystemUser(String url, List<Object> params, Object body, HttpHeaders httpHeaders, Class<T> t) {
        return systemUpdate(url, params, body, httpHeaders, t, HttpMethod.POST);
    }

    public <T> PagingResponseModel<T> postForPage(String url, List<Object> params, Object body, ParameterizedTypeReference<PagingResponseModel<T>> typeReference) {
        final ApiIntegration<PagingResponseModel<T>> apiIntegration = ApiIntegration.<PagingResponseModel<T>>builder()
                .url(url)
                .httpMethod(HttpMethod.POST)
                .params(params)
                .body(body)
                .typeReference(typeReference)
                .restTemplate(this.restTemplate)
                .build();
        return apiIntegration.exchange().getBody();
    }

    public <T> List<T> postForList(String url, ParameterizedTypeReference<List<T>> t) {
        return postForList(url, null, null, null, t);
    }

    public <T> List<T> postForList(String url, Object body, ParameterizedTypeReference<List<T>> t) {
        return postForList(url, null, body, null, t);
    }

    public <T> List<T> postForListInternal(String url, Object body, ParameterizedTypeReference<List<T>> t) {
        return postForListInternal(url, null, body, null, t);
    }

    public <T, U> Map<T, U> postForMap(String url, Object body, ParameterizedTypeReference<Map<T, U>> t) {
        return postForMap(url, null, body, t);
    }

    public <T, U> Map<T, U> postForMapInternal(String url, Object body, ParameterizedTypeReference<Map<T, U>> t) {
        return postForMapInternal(url, null, body, t);
    }

    public <T, U> Map<T, U> postForMap(String url, List<Object> params, Object body, ParameterizedTypeReference<Map<T, U>> t) {
        final ApiIntegration<Map<T, U>> apiIntegration = ApiIntegration.<Map<T, U>>builder()
                .url(url)
                .httpMethod(HttpMethod.POST)
                .params(params)
                .body(body)
                .typeReference(t)
                .restTemplate(this.restTemplate)
                .build();
        return apiIntegration.exchange().getBody();
    }

    public <T, U> Map<T, U> postForMapInternal(String url, List<Object> params, Object body, ParameterizedTypeReference<Map<T, U>> t) {
        return postForMapInternal(url, null, body, null, t);
    }

    public <T, U> Map<T, U> postForMapInternal(String url, List<Object> params, Object body, HttpHeaders httpHeaders, ParameterizedTypeReference<Map<T, U>> t) {
        final ApiIntegration<Map<T, U>> apiIntegration = ApiIntegration.<Map<T, U>>builder()
                .url(url)
                .httpMethod(HttpMethod.POST)
                .params(params)
                .body(body)
                .typeReference(t)
                .headers(httpHeaders)
                .restTemplate(this.systemUserRestTemplate)
                .build();
        return apiIntegration.exchange().getBody();
    }

    public <T> Set<T> postForSet(String url, Object body, ParameterizedTypeReference<Set<T>> t) {
        final ApiIntegration<Set<T>> apiIntegration = ApiIntegration.<Set<T>>builder()
                .url(url)
                .httpMethod(HttpMethod.POST)
                .body(body)
                .typeReference(t)
                .restTemplate(this.restTemplate)
                .build();
        return apiIntegration.exchange().getBody();
    }

    public <T> Set<T> postForSet(String url, List<Object> params, Object body, ParameterizedTypeReference<Set<T>> t) {
        final ApiIntegration<Set<T>> apiIntegration = ApiIntegration.<Set<T>>builder()
                .url(url)
                .httpMethod(HttpMethod.POST)
                .body(body)
                .params(params)
                .typeReference(t)
                .restTemplate(this.restTemplate)
                .build();
        return apiIntegration.exchange().getBody();
    }

    public <T> List<T> postForList(String url, List<Object> params, ParameterizedTypeReference<List<T>> t) {
        return postForList(url, params, null, null, t);
    }

    public <T> List<T> postForList(String url, HttpHeaders httpHeaders, ParameterizedTypeReference<List<T>> t) {
        return postForList(url, null, null, httpHeaders, t);
    }

    public <T> List<T> postForList(String url, List<Object> params, Object body, ParameterizedTypeReference<List<T>> t) {
        return postForList(url, params, body, null, t);
    }

    public <T> List<T> postForList(String url, List<Object> params, HttpHeaders httpHeaders, ParameterizedTypeReference<List<T>> t) {
        return postForList(url, params, null, httpHeaders, t);
    }

    public <T> List<T> postForList(String url, Object body, HttpHeaders httpHeaders, ParameterizedTypeReference<List<T>> t) {
        return postForList(url, null, body, httpHeaders, t);
    }

    public <T> List<T> postForList(String url, List<Object> params, Object body, HttpHeaders httpHeaders, ParameterizedTypeReference<List<T>> t) {
        final ApiIntegration<List<T>> apiIntegration = ApiIntegration.<List<T>>builder()
                .url(url)
                .httpMethod(HttpMethod.POST)
                .params(params)
                .body(body)
                .typeReference(t)
                .headers(httpHeaders)
                .restTemplate(this.restTemplate)
                .build();
        return apiIntegration.exchange().getBody();
    }

    public <T> List<T> postForListInternal(String url, List<Object> params, Object body, HttpHeaders httpHeaders, ParameterizedTypeReference<List<T>> t) {
        final ApiIntegration<List<T>> apiIntegration = ApiIntegration.<List<T>>builder()
                .url(url)
                .httpMethod(HttpMethod.POST)
                .params(params)
                .body(body)
                .typeReference(t)
                .headers(httpHeaders)
                .restTemplate(this.systemUserRestTemplate)
                .build();
        return apiIntegration.exchange().getBody();
    }

    public <T> List<T> putForList(String url, Object body, ParameterizedTypeReference<List<T>> t) {
        return putForList(url, null, body, null, t);
    }

    public <T> List<T> putForList(String url, List<Object> params, Object body, HttpHeaders httpHeaders, ParameterizedTypeReference<List<T>> t) {
        final ApiIntegration<List<T>> apiIntegration = ApiIntegration.<List<T>>builder()
                .url(url)
                .httpMethod(HttpMethod.PUT)
                .params(params)
                .body(body)
                .typeReference(t)
                .headers(httpHeaders)
                .restTemplate(this.restTemplate)
                .build();
        return apiIntegration.exchange().getBody();
    }

    public <T> List<T> putForListInternal(String url, List<Object> params, Object body, HttpHeaders httpHeaders, ParameterizedTypeReference<List<T>> t) {
        final ApiIntegration<List<T>> apiIntegration = ApiIntegration.<List<T>>builder()
                .url(url)
                .httpMethod(HttpMethod.PUT)
                .params(params)
                .body(body)
                .typeReference(t)
                .headers(httpHeaders)
                .restTemplate(this.systemUserRestTemplate)
                .build();
        return apiIntegration.exchange().getBody();
    }

    public <T> T patch(String url, Class<T> t) {
        return patch(url, null, null, null, t);
    }

    public <T> T patch(String url, List<Object> params, Class<T> t) {
        return patch(url, params, null, null, t);
    }

    public <T> T patch(String url, Object body, Class<T> t) {
        return patch(url, null, body, null, t);
    }

    public <T> T patch(String url, HttpHeaders httpHeaders, Class<T> t) {
        return patch(url, null, null, httpHeaders, t);
    }

    public <T> T patch(String url, List<Object> params, Object body, Class<T> t) {
        return patch(url, params, body, null, t);
    }

    public <T> T patch(String url, List<Object> params, HttpHeaders httpHeaders, Class<T> t) {
        return patch(url, params, null, httpHeaders, t);
    }

    public <T> T patch(String url, Object body, HttpHeaders httpHeaders, Class<T> t) {
        return patch(url, null, body, httpHeaders, t);
    }

    public <T> T patch(String url, List<Object> params, Object body, HttpHeaders httpHeaders, Class<T> t) {
        return update(url, params, body, httpHeaders, t, HttpMethod.PATCH);
    }

    public <T> T update(String url, List<Object> params, Object body, HttpHeaders httpHeaders, Class<T> t, HttpMethod httpMethod) {
        final ApiIntegration<T> apiIntegration = ApiIntegration.<T>builder()
                .url(url)
                .httpMethod(httpMethod)
                .params(params)
                .headers(httpHeaders)
                .body(body)
                .type(t)
                .restTemplate(this.restTemplate)
                .build();
        return apiIntegration.exchangeWithType().getBody();
    }

    public <T> T systemUpdate(String url, List<Object> params, Object body, HttpHeaders httpHeaders, Class<T> t, HttpMethod httpMethod) {
        final ApiIntegration<T> apiIntegration = ApiIntegration.<T>builder()
                .url(url)
                .httpMethod(httpMethod)
                .params(params)
                .headers(httpHeaders)
                .body(body)
                .type(t)
                .restTemplate(this.systemUserRestTemplate)
                .build();
        return apiIntegration.exchangeWithType().getBody();
    }

    public <T> List<T> patchForList(String url, ParameterizedTypeReference<List<T>> t) {
        return patchForList(url, null, null, null, t);
    }

    public <T> List<T> patchForList(String url, Object body, ParameterizedTypeReference<List<T>> t) {
        return patchForList(url, null, body, null, t);
    }

    public <T> List<T> patchForList(String url, List<Object> params, ParameterizedTypeReference<List<T>> t) {
        return patchForList(url, params, null, null, t);
    }

    public <T> List<T> patchForList(String url, HttpHeaders httpHeaders, ParameterizedTypeReference<List<T>> t) {
        return patchForList(url, null, null, httpHeaders, t);
    }

    public <T> List<T> patchForList(String url, List<Object> params, Object body, ParameterizedTypeReference<List<T>> t) {
        return patchForList(url, params, body, null, t);
    }

    public <T> List<T> patchForList(String url, List<Object> params, HttpHeaders httpHeaders, ParameterizedTypeReference<List<T>> t) {
        return patchForList(url, params, null, httpHeaders, t);
    }

    public <T> List<T> patchForList(String url, Object body, HttpHeaders httpHeaders, ParameterizedTypeReference<List<T>> t) {
        return patchForList(url, null, body, httpHeaders, t);
    }

    public <T> List<T> patchForList(String url, List<Object> params, Object body, HttpHeaders httpHeaders, ParameterizedTypeReference<List<T>> t) {
        final ApiIntegration<List<T>> apiIntegration = ApiIntegration.<List<T>>builder()
                .url(url)
                .httpMethod(HttpMethod.PATCH)
                .params(params)
                .body(body)
                .typeReference(t)
                .headers(httpHeaders)
                .restTemplate(this.restTemplate)
                .build();
        return apiIntegration.exchange().getBody();
    }

    public <T> T put(String url, Object body, Class<T> t) {
        final ApiIntegration<T> apiIntegration = ApiIntegration.<T>builder()
                .url(url)
                .httpMethod(HttpMethod.PUT)
                .body(body)
                .type(t)
                .restTemplate(this.restTemplate)
                .build();
        return apiIntegration.exchangeWithType().getBody();
    }

    public <T> T put(String url, List<Object> params, Object body, Class<T> t) {
        final ApiIntegration<T> apiIntegration = ApiIntegration.<T>builder()
                .url(url)
                .httpMethod(HttpMethod.PUT)
                .params(params)
                .body(body)
                .type(t)
                .restTemplate(this.restTemplate)
                .build();
        return apiIntegration.exchangeWithType().getBody();
    }

    public List<Object> buildParamsFromUnpage() {
        return new ArrayList<>(Arrays.asList(100, Integer.MAX_VALUE));
    }

    public List<Object> buildParamsFromPageable(Pageable pageable) {

        List<Object> params = new ArrayList<>(Arrays.asList(
                10, pageable.getPageNumber(),
                100, pageable.getPageSize()));
        Iterator<Sort.Order> iterator = pageable.getSort().iterator();
        String sortValue;
        while (iterator.hasNext()) {
            Sort.Order order = iterator.next();
            sortValue = String.format("%s,%s", order.getProperty(), order.getDirection().name());
//            params.addAll(Arrays.asList(EntityNameConstants.PagableConstant.SORT, sortValue));
        }
        return params;
    }
}
