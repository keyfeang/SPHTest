package com.keyfe.ang.foundation.tools.http;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@IntDef({StatusCode.CODE_NETWORK_UNAVAILABLE, StatusCode.CODE_OK, StatusCode.CODE_CREATED, StatusCode.CODE_ERROR, StatusCode.CODE_TIMEOUT,
    StatusCode.CODE_UNAUTHORIZED, StatusCode.CODE_FORBIDDEN, StatusCode.CODE_SERVER_ERROR, StatusCode.CODE_UNKNOWN_HOST})
public @interface StatusCode
{
  int CODE_NETWORK_UNAVAILABLE = 0;
  int CODE_OK = 200;
  int CODE_CREATED = 201;
  int CODE_ERROR = 400;
  int CODE_UNAUTHORIZED = 401;
  int CODE_FORBIDDEN = 403;
  int CODE_TIMEOUT = 408;
  int CODE_SERVER_ERROR = 500;
  int CODE_UNKNOWN_HOST = 599;
}
