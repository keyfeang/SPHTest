package com.keyfe.ang.foundation.tools.http;

public class HttpException extends Exception
{
  /* Properties */

  @StatusCode
  public final int statusCode;

  /* Initializations */

  public HttpException (@StatusCode int statusCode)
  {
    this(statusCode, null);
  }

  public HttpException (@StatusCode int statusCode, String message)
  {
    super(message);
    this.statusCode = statusCode;
  }
}
