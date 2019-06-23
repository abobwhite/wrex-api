package com.daugherty.wrex.user

import com.daugherty.wrex.ExternalConfig
import com.daugherty.wrex.user.slack.OauthRequest
import com.daugherty.wrex.user.slack.OauthResponse
import groovy.util.logging.Slf4j
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Slf4j
@Service
class UserSlackService {
    private final ExternalConfig externalConfig
    private final RestTemplate restTemplate

    UserSlackService(final ExternalConfig externalConfig, final RestTemplate restTemplate) {
        this.externalConfig = externalConfig
        this.restTemplate = restTemplate
    }

    String getAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED)

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>()
        map.add("client_id", externalConfig.slackClientId)
        map.add("client_secret", externalConfig.slackClientSecret)
        map.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers)

        ResponseEntity<Object> response = restTemplate.postForEntity(externalConfig.slackAccessTokenUrl, request, Object)

        log.info('Received OauthResponse: ' + response.toString())
        return response.access_code
    }
}
