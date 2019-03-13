package com.keyfe.ang.foundation.tools.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.keyfe.ang.foundation.BuildConfig;

public class HttpConnection
{
  /* Properties */

  private Context m_context;
  private String m_baseUrl;
  private String m_requestBody;

  @RequestMethod
  private String m_requestMethod = RequestMethod.METHOD_GET;

  private Map<String, Object> m_paramMap = new HashMap<>();
  private Map<String, String> m_propertyMap = new HashMap<>();

  private int m_requestTimeout = 60 * 1000;
  private int m_connectionTimeout = 30 * 1000;

  private ProgressListener m_progressListener;

  /* Initialization */

  /**
   * Generic constructor.
   *
   * @param context the reference {@link Context}
   */
  public HttpConnection (Context context, String baseUrl)
  {
    m_context = context;
    m_baseUrl = baseUrl;
  }

  /* Property methods */

  /**
   * @return base url.
   */
  public String getBaseUrl ()
  {
    return m_baseUrl;
  }

  /**
   * Gets request methods.
   */
  @RequestMethod
  public String getRequestMethod ()
  {
    return m_requestMethod;
  }
  /**
   * Sets request method.
   */
  public void setRequestMethod (@RequestMethod String method)
  {
    m_requestMethod = method;
  }

  /**
   * Sets the rest request body content.
   */
  public void setRequestBody (String requestBody)
  {
    m_requestBody = requestBody;
    if (    requestBody != null
        && !RequestMethod.METHOD_POST.equals(m_requestMethod)
        && !RequestMethod.METHOD_PUT.equals(m_requestMethod))
    {
      m_requestMethod = RequestMethod.METHOD_POST;
    }
  }

  /**
   * Returns the request body. See also {@link #setRequestBody(String)}.
   */
  public String getRequestBody ()
  {
    return m_requestBody;
  }

  /**
   * Sets the request timeout on each request.
   *
   * @param timeoutInMillis the timeout time in milliseconds to be set on each requests
   */
  public void setRequestTimeout (int timeoutInMillis)
  {
    m_requestTimeout = timeoutInMillis;
  }

  /**
   * @return request timeout in milliseconds. See also {@link #setRequestTimeout(int)}.
   */
  public long getRequestTimeout ()
  {
    return m_requestTimeout;
  }

  /**
   * Sets the connection timeout on each request.
   *
   * @param connectionTimeoutInMillis the timeout in milliseconds to be set on each requests
   */
  public void setConnectionTimeout (int connectionTimeoutInMillis)
  {
    m_connectionTimeout = connectionTimeoutInMillis;
  }

  /**
   * @return connection timout in milliseconcs. See also {@link #setConnectionTimeout(int)}.
   */
  public long getConnectionTimeout ()
  {
    return m_connectionTimeout;
  }

  /**
   * Sets the value of the specified request header field. The value will only be used by the
   * current URLConnection instance. This method can only be called before the connection is
   * established.
   */
  public void setRequestProperty (String field, String value)
  {
    m_propertyMap.put(field, value);
  }

  /**
   * Adds the specified request parameter specified by key on request.
   *
   * @param key the parameter key
   * @param value the value
   */
  public void addRequestParameter (String key, Object value)
  {
    if (    value != null
        && !String.valueOf(value).isEmpty())
    {
      m_paramMap.put(key, value);
    }
  }

  /**
   * Ads the map of key and value parameters.
   *
   * @param params the map of key of string values
   */
  public void addRequestParameters (Map<String, Object> params)
  {
    if (params != null)
    {
      for (Map.Entry<String, Object> entry : params.entrySet())
      {
        if (    entry.getValue() != null
            && !String.valueOf(entry.getValue()).isEmpty())
        {
          m_paramMap.put(entry.getKey(), entry.getValue());
        }
      }
    }
  }

  /**
   * @return request parameters. See also {@link #addRequestParameters(Map)}.
   */
  public Map<String, Object> getRequestParameters ()
  {
    return new HashMap<String, Object>(m_paramMap);
  }

  /**
   * Sets progress listener instance.
   */
  public void setProgressListener (ProgressListener progressListener)
  {
    m_progressListener = progressListener;
  }

  /**
   * Triggers fetch response.
   */
  public String getResponse () throws Exception
  {
    /* Check network connection. */
    if (!isNetworkAvailable())
    {
      throw new HttpException(StatusCode.CODE_NETWORK_UNAVAILABLE);
    }

    String data = null;
    String message = null;
    @StatusCode
    int statusCode = StatusCode.CODE_OK;

    HttpURLConnection urlConnection = null;
    try
    {
      /* Setup URLConnection */

      urlConnection = (HttpURLConnection) buildBaseURLConnection();
      urlConnection.setRequestMethod(m_requestMethod);

      /* Append request properties. */
      appendRequestProperties(urlConnection);

      /* Post post request body to urlConnection */
      postContentBody(urlConnection);

      /* Fetch and pass request callback instance for the download updates */
      int responseCode = urlConnection.getResponseCode();
      InputStream inputStream =  responseCode >= 400
                               ? urlConnection.getErrorStream()
                               : urlConnection.getInputStream();
      int contentLength = urlConnection.getContentLength();

      data = fetchData(inputStream, contentLength);
    }
    catch (  SocketTimeoutException
           | SocketException e)
    {
      statusCode = StatusCode.CODE_TIMEOUT;
      e.printStackTrace();
    }
    /*
     * Catch reported existing Androids bug.
     */
    catch (UnknownHostException e)
    {
      statusCode = StatusCode.CODE_UNKNOWN_HOST;
      e.printStackTrace();
    }
    /*
     * NOTE: If we do start to support error. This is where we should implement
     * it.
     */
    catch (Exception e)
    {
      statusCode = StatusCode.CODE_SERVER_ERROR;
      e.printStackTrace();
    }
    finally
    {
      if (urlConnection != null)
      {
        urlConnection.disconnect();
      }
    }

    if (statusCode != StatusCode.CODE_OK)
    {
      throw new HttpException(statusCode, message);
    }
    return data;
  }

  /* Internal methods */

  /**
   * Common method to build {@link HttpURLConnection} instance initialized with the specified
   * parameters.
   *
   * @return the {@link HttpURLConnection} instance
   * @throws IOException thrown when an error occur when opening the connection
   */
  protected URLConnection buildBaseURLConnection () throws IOException
  {
    Uri.Builder builder = Uri.parse(m_baseUrl).buildUpon();
    for (Map.Entry<String, Object> param : m_paramMap.entrySet())
    {
      builder.appendQueryParameter(param.getKey(), String.valueOf(param.getValue()));
    }

    log(String.format("%s: %s", m_requestMethod, builder.toString()));

    URL url = new URL(builder.toString());
    URLConnection conn = url.openConnection();

    if (RequestMethod.METHOD_POST.equals(m_requestMethod))
    {
      conn.setDoInput(true);
      conn.setDoOutput(true);
    }
    conn.setUseCaches(false);

    System.setProperty("http.keepAlive", "false");

    /* Set connection timeout. */
    conn.setReadTimeout(m_requestTimeout);
    conn.setConnectTimeout(m_connectionTimeout);

    return conn;
  }

  /**
   * Method to append all request properties. See {@link #setRequestProperty(String, String)}.
   */
  protected void appendRequestProperties (HttpURLConnection urlConnection)
  {
    for (Map.Entry<String, String> property : m_propertyMap.entrySet())
    {
      urlConnection.setRequestProperty(property.getKey(), property.getValue());
    }
  }

  /**
   * Common method to post content data to request body.
   *
   * @param urlConnection the {@link URLConnection} instance where the data will be uploaded
   * @throws IOException thrown when no output stream could be created
   */
  protected void postContentBody (HttpURLConnection urlConnection) throws IOException
  {
    if (m_requestBody == null)
    {
      return;
    }

    OutputStream out = null;
    BufferedWriter writer = null;
    try
    {
      out = new BufferedOutputStream(urlConnection.getOutputStream());
      writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
      writer.write(m_requestBody);
      writer.flush();

      log("RequestBody: " + m_requestBody);
    }
    finally
    {
      closeStream(writer);
      closeStream(out);
    }
  }

  /**
   * Common method to read data from the {@link InputStream}.
   *
   * @param inputStream the {@link InputStream} where the data will be read
   * @param contentLength the content length that is usable on progress updates
   * @return the raw String data fetched
   * @throws IOException thrown when an error occurs while trying to connect to the resource or
   * while reading the response
   */
  private String fetchData (InputStream inputStream, int contentLength) throws IOException
  {
    contentLength = Math.max(contentLength, 1);

    /* Start download data */

    String data = null;
    Reader reader = null;
    try
    {
      StringBuilder builder = new StringBuilder();
      reader = new InputStreamReader(inputStream);

      char[] charBuffer = new char[1024];
      int count;
      while ((count = reader.read(charBuffer)) > -1)
      {
        if (count > 0)
        {
          builder.append(charBuffer, 0, count);
        }

        /* Trigger progress updates when able */
        if (m_progressListener != null)
        {
          int partialLength = builder.length();
          contentLength = Math.max(partialLength, contentLength);
          try
          {
            m_progressListener.onProgressUpdate(Math.round(100.0f * partialLength / contentLength));
          }
          catch (Exception e)
          {
            /* Catch callback trigger exceptions. */
            e.printStackTrace();
          }
        }
      }
      data = builder.toString();
      log("HttpResponse: " + data);
    }
    finally
    {
      closeStream(reader);
    }
    return data;
  }

  /* Utility methods */

  /**
   * Whether the device is connected to a network.
   *
   * @return true if connected to a network, otherwise, false
   */
  protected boolean isNetworkAvailable ()
  {
    ConnectivityManager cm = (ConnectivityManager)
      m_context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    return   networkInfo != null
          && networkInfo.isConnected();
  }

  /**
   * Common method to close stream instances.
   */
  protected void closeStream (Closeable stream)
  {
    /* Close stream if possible */
    if (stream != null)
    {
      try
      {
        stream.close();
      }
      catch (IOException e)
      {
        /*
         * Do nothing. At this point, we don't need to worry about the stream
         * instance's current state.
         */
      }
    }
  }

  /**
   * Utility method for logging.
   */
  protected void log (String log)
  {
    if (BuildConfig.DEBUG)
    {
      Log.d("HttpConnectionF", log);
    }
  }
}
