package com.leyu.authservice.domain;

import lombok.Data;

@Data
public class AccessTokenParam {

    private String grant_type;
    private String client_id;
    private String client_secret;
    private String username;
    private String password;
}
