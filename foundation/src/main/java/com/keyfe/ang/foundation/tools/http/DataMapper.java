package com.keyfe.ang.foundation.tools.http;

public interface DataMapper<T>
{
  T mapData(String data) throws Exception;
}
