package com.leyu.authservice.controller;

import com.alibaba.fastjson.JSON;
import com.leyu.authservice.api.CommonResult;
import com.leyu.authservice.domain.AccessTokenParam;
import com.leyu.authservice.domain.Oauth2TokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * 自定义Oauth2获取令牌接口
 * Created by chuyan on 2020/7/17.
 */
@RestController
@RequestMapping("/oauth")
public class AuthController {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    /**
     * Oauth2登录认证
     */
    @PostMapping(value = "/token")
    public CommonResult<Oauth2TokenDto> postAccessToken(Principal principal, AccessTokenParam param) throws HttpRequestMethodNotSupportedException {
        Map<String, String> parameters = JSON.parseObject(JSON.toJSONString(param), Map.class);
        OAuth2AccessToken oAuth2AccessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        Oauth2TokenDto oauth2TokenDto = Oauth2TokenDto.builder()
                .token(oAuth2AccessToken.getValue())
                .refreshToken(oAuth2AccessToken.getRefreshToken().getValue())
                .expiresIn(oAuth2AccessToken.getExpiresIn())
                .tokenHead("Bearer ").build();

        return CommonResult.success(oauth2TokenDto);
    }

    @PostMapping("/hello")
    public String hello(String name){
        return name;
    }
}
