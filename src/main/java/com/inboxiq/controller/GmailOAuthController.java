package com.inboxiq.controller;

import com.inboxiq.service.GmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GmailOAuthController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private GmailService gmailService;

    @GetMapping("/emails")
    @ResponseBody
    public String getEmails(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());

        String accessToken = client.getAccessToken().getTokenValue();

        return gmailService.fetchEmails(accessToken);
    }
}

