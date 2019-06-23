package com.daugherty.wrex.user.slack

import groovy.transform.ToString

@ToString
class OauthResponse {
    String access_token
    User user
    Team team

    private class User {
        String name
        String id
    }

    private class Team {
        String id
    }
}