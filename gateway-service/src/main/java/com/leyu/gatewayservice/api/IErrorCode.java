package com.leyu.gatewayservice.api;

/**
 * 封装API的错误码
 * Created by chuyan on 2019/4/19.
 */
public interface IErrorCode {
    long getCode();

    String getMessage();
}
