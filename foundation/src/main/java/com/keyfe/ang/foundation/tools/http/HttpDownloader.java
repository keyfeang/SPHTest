package com.keyfe.ang.foundation.tools.http;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class HttpDownloader extends HttpConnection
{
  /* Properties */

  private File m_file;

  /* Initializations */

  /**
   * Generic constructor.
   *
   * @param context the reference {@link Context}.
   */
  public HttpDownloader (Context context, String baseUrl, File file)
  {
    super(context, baseUrl);
    m_file = file;
  }

  public File download () throws HttpException
  {
    /* Check network connection. */
    if (!isNetworkAvailable())
    {
      throw new HttpException(StatusCode.CODE_NETWORK_UNAVAILABLE);
    }

    String message = null;
    @StatusCode
    int statusCode = StatusCode.CODE_OK;

    HttpURLConnection urlConnection = null;
    try
    {
      /* Setup URLConnection */

      urlConnection = (HttpURLConnection) buildBaseURLConnection();
      urlConnection.setRequestMethod(getRequestMethod());

      /* Append request properties. */
      appendRequestProperties(urlConnection);

      /* Post post request body to urlConnection */
      postContentBody(urlConnection);

      /* Fetch and pass request callback instance for the download updates */
      InputStream inputStream = urlConnection.getInputStream();
      fetchData(inputStream);
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
    return m_file;
  }

  /**
   * Common method to read data from the {@link InputStream}.
   *
   * @param inputStream the {@link InputStream} where the data will be read
   * @return the raw String data fetched
   * @throws IOException thrown when an error occurs while trying to connect to the resource or
   * while reading the response
   */
  private void fetchData (InputStream inputStream) throws IOException
  {
    /* Start download data */

    FileOutputStream outputStream = null;
    try
    {
      outputStream = new FileOutputStream(m_file);

      byte[] charBuffer = new byte[1024];
      int count;
      while ((count = inputStream.read(charBuffer)) > -1)
      {
        if (count > 0)
        {
          outputStream.write(charBuffer, 0, count);
        }
      }
    }
    finally
    {
      closeStream(outputStream);
    }
  }
}
