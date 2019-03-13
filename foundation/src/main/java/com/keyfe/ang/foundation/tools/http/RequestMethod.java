package com.keyfe.ang.foundation.tools.http;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({RequestMethod.METHOD_GET, RequestMethod.METHOD_POST, RequestMethod.METHOD_PUT, RequestMethod.METHOD_DELETE})
public @interface RequestMethod
{
  String METHOD_GET = "GET";
  String METHOD_POST = "POST";
  String METHOD_DELETE = "DELETE";
  String METHOD_PUT = "PUT";
}
