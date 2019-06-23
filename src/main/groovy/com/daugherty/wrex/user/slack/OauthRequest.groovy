package com.daugherty.wrex.user.slack

import groovy.transform.ToString

@ToString
class OauthRequest {
    String client_id
    String client_secret
    String code
}