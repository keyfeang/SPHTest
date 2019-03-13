package com.keyfe.ang.foundation.tools.http;

import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class MultipartHttpConnection extends HttpConnection
{
  /* Constants */

  private static final String BOUNDARY = "******";
  private static final String LINE_FEED = "\r\n";
  private static final String HYPHENS = "--";

  /* Properties */

  private Map<String, String> m_formFields = new HashMap<>();
  private Map<String, File> m_files = new HashMap<>();

  /* Initializations */

  /**
   * Generic constructor.
   *
   * @param context the reference {@link Context}
   * @param baseUrl string base url
   */
  public MultipartHttpConnection (Context context, String baseUrl)
  {
    super(context, baseUrl);
    setRequestMethod(RequestMethod.METHOD_POST);
    setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
    setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
    setRequestProperty("Connection", "Keep-Alive");
  }

  public void addFormField (String name, String value)
  {
    m_formFields.put(name, value);
  }

  public void addFilePart (String fileName, File file)
  {
    m_files.put(fileName, file);
  }

  @Override
  protected void postContentBody (HttpURLConnection urlConnection)
    throws IOException
  {
    if (   m_files.isEmpty()
        && m_formFields.isEmpty())
    {
      return;
    }

    OutputStream out = null;
    BufferedWriter writer = null;
    try
    {
      out = new BufferedOutputStream(urlConnection.getOutputStream());
      writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

      for (Map.Entry<String, String> entry : m_formFields.entrySet())
      {
        addFormField(writer, entry.getKey(), entry.getValue());
      }

      for (Map.Entry<String, File> entry : m_files.entrySet())
      {
        addFilePart(out, writer, entry.getKey(), entry.getValue());
      }

      writer.append(LINE_FEED).flush();
      writer.append(String.format("%s%s%s", HYPHENS, BOUNDARY, HYPHENS))
        .append(LINE_FEED);
      writer.close();
    }
    finally
    {
      closeStream(writer);
      closeStream(out);
    }
  }

  /**
   * Adds a form field to the request
   *
   * @param name field name
   * @param value field value
   */
  private void addFormField (BufferedWriter writer, String name, String value) throws IOException
  {
    writer.append(String.format("%s%s", HYPHENS, BOUNDARY))
      .append(LINE_FEED)
      .append(String.format("Content-Disposition: form-data; name=\"%s\"", name))
      .append(LINE_FEED)
      .append("Content-Type: text/plain; charset=UTF-8")
      .append(LINE_FEED)
      .append(LINE_FEED)
      .append(value)
      .append(LINE_FEED)
      .flush();
  }

  /**
   * Adds a upload file section to the request
   *
   * @param fileName file name />
   * @param uploadFile the File to be uploaded
   * @throws IOException
   */
  private void addFilePart (OutputStream outputStream, BufferedWriter writer, String fileName,
    File uploadFile) throws IOException
  {
    writer.append(String.format("%s%s", HYPHENS, BOUNDARY))
      .append(LINE_FEED)
      .append(String.format("Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"",
        fileName,
        fileName))
      .append(LINE_FEED)
      .append(String.format("Content-Type: %s", URLConnection.guessContentTypeFromName(fileName)))
      .append(LINE_FEED)
      .append("Content-Transfer-Encoding: binary")
      .append(LINE_FEED)
      .append(LINE_FEED)
      .flush();

    FileInputStream inputStream = new FileInputStream(uploadFile);
    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = inputStream.read(buffer)) != -1)
    {
      outputStream.write(buffer, 0, bytesRead);
    }
    outputStream.flush();
    inputStream.close();

    writer.append(LINE_FEED);
    writer.flush();
  }
}
