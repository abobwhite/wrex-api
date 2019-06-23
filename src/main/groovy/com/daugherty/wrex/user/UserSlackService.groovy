package com.daugherty.wrex.tag

import com.daugherty.wrex.ExternalConfig
import com.daugherty.wrex.user.slack.OauthRequest
import com.daugherty.wrex.user.slack.OauthResponse
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service
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
        OauthRequest oauthRequest = new OauthRequest(
                client_id: System.getenv(SLACK_CLIENT_ID),
                client_secret: System.getenv(SLACK_CLIENT_SECRET),
                code: code
        )
        OauthResponse oauthResponse = restTemplate.postForObject(externalConfig.slackAccessTokenUrl, oauthRequest, OathResponse)
        log.info(oauthResponse.access_token)
        return oauthResponse.access_token
    }
}
