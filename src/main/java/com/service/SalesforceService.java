package com.service;

import com.alibaba.fastjson.JSON;
import com.config.SFConstant;
import com.config.URLConstant;
import com.model.SFEvent;
import com.model.SFEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class SalesforceService {

    private static final Logger logger = LoggerFactory.getLogger(SalesforceService.class);
    
    private String getToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", SFConstant.GRANT_TYPE);
        map.add("client_id", SFConstant.CLIENT_ID);
        map.add("client_secret", SFConstant.CLIENT_SECRET);
        map.add("username", SFConstant.USERNAME);
        map.add("password", SFConstant.PASSWORD);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<Map> response = restTemplate.exchange(URLConstant.SF_TOKEN_URL, HttpMethod.POST, request, Map.class);

        if (response != null && response.getStatusCode().value() == 200) {
            String accessToken = (String) response.getBody().get("access_token");
            return accessToken;
        } else {
            throw new IllegalStateException("Cannot retrieve salesforce token: " + response.getStatusCode());
        }
    }

    public void sync(SFEvent sfEvent) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+ getToken());
        HttpEntity<SFEventDTO> request = new HttpEntity<>(sfEvent.toSFEventDTO(), headers);

        ResponseEntity<Map> response = restTemplate.exchange(URLConstant.SF_DINGDING_INTEGRATION_URL, HttpMethod.POST, request, Map.class);

        if (response != null && response.getStatusCode().value() == 200) {
            logger.info("Generated event from SF: " + JSON.toJSONString(response.getBody()));
        } else {
            throw new IllegalStateException("Fail to sync to salesforce: " + response.getStatusCode());
        }
    }

    public static void main(String[] args) {
        SalesforceService service = new SalesforceService();
        String token = service.getToken();
        System.out.println(token);
    }
}
