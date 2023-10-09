package com.tramhuong.common.integration;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import org.springframework.web.util.UriComponents;



import javax.annotation.PostConstruct;

import java.util.List;

import java.util.stream.Collectors;



@Slf4j

@Component

public class IntegrationSecuritySupporter {
    private static final String ENDPOINT = "endpoint";
    private static final String IS_NOT_A_VALID_WHITELIST_ENDPOINT = "is not a valid/ whitelist endpoint";
    private List<IApiEndpoint> iApiEndpoints;
    private static ImmutableList<String> endpointsWhiteList;

    // TODO Keep only for hotfix. Permanent solution: Move endpoint to config
    private static final String CAS_ENDPOINT = "webcas.parkwayhealth.local";
    private static final String CAS_ENDPOINT_UAT = "casuat.parkwayhealth.local";

    @Autowired
    public void setIApiEndpoints(List<IApiEndpoint> iApiEndpoints) {
        this.iApiEndpoints = iApiEndpoints;
    }
    @PostConstruct

    public void afterConstruct() {
        setEndpointsWhiteList(ImmutableList.copyOf(iApiEndpoints.stream()
                .map(IApiEndpoint::getUrl).collect(Collectors.toList())));
    }

    private static void setEndpointsWhiteList(ImmutableList<String> endpointsWhiteList) {
        IntegrationSecuritySupporter.endpointsWhiteList = endpointsWhiteList;

    }



    public static String validateEndpoint(UriComponents requestedUri) {

        log.debug("CAS endpoint: {}", CAS_ENDPOINT);
        List<String> a =endpointsWhiteList;
        final boolean isValidEndpoint = validate(requestedUri);
        String encodedRequestUri = requestedUri.encode().toUriString();
        if (isValidEndpoint || encodedRequestUri.contains(CAS_ENDPOINT)
                || encodedRequestUri.contains(CAS_ENDPOINT_UAT)) {
            return encodedRequestUri;
        }
        log.error("[Endpoint Error] {} is not a valid/ whitelist endpoint", encodedRequestUri);
//        throw "";
        return "";

    }



    private static boolean validate(UriComponents requestedUri) {

        return IntegrationSecuritySupporter.endpointsWhiteList.stream().anyMatch(requestedUri.toUriString()::startsWith);

    }

}
