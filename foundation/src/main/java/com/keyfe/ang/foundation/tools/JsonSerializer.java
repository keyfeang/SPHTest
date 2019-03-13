package com.keyfe.ang.foundation.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

import java.lang.reflect.Type;

public class JsonSerializer
{
  public static GsonBuilder getBuilderInstance()
  {
    GsonBuilder builder = new GsonBuilder();
    builder.setLongSerializationPolicy(LongSerializationPolicy.STRING);
    builder.setDateFormat("yyyy-MM-dd'T'hh:mm:ss");
    builder.excludeFieldsWithoutExposeAnnotation();
    return builder;
  }

  private static Gson getGson ()
  {
    return getBuilderInstance().create();
  }

  public static <T> T serialize (String rawData, Class<T> cls)
  {
    Gson gson = getGson();
    return gson.fromJson(rawData, cls);
  }

  public static <T> T serialize (String rawData, Type type)
  {
    Gson gson = getGson();
    return gson.fromJson(rawData, type);
  }

  public static String deserialize (Object src)
  {
    Gson gson = getGson();
    return gson.toJson(src);
  }

  public static <T> T serializeFromObject (Object src, Type type)
  {
    Gson gson = getGson();
    String rawData = gson.toJson(src);
    return gson.fromJson(rawData, type);
  }
}
