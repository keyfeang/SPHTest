package com.keyfe.ang.foundation.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileUtil
{
  /**
   * Move file to target file path.
   *
   * @param fromFile the file to move
   * @param toFile   the target file path
   */
  public static void moveFile (File fromFile, File toFile) throws Exception
  {
    FileInputStream fis = null;
    FileOutputStream fos = null;

    try
    {
      fis = new FileInputStream(fromFile);
      fos = new FileOutputStream(toFile);

      byte[] buffer = new byte[1024];
      int count;
      while ((count = fis.read(buffer)) > -1)
      {
        fos.write(buffer, 0, count);
      }
    }
    finally
    {
      closeStream(fis);
      closeStream(fos);

      int retry = 0;
      do
      {
        if (fromFile.delete())
        {
          break;
        }
        retry++;
      } while (retry < 5);
    }
  }

  public static boolean deleteFile (File file)
  {
    if (!file.exists())
    {
      return false;
    }

    boolean deleted = false;
    int retry = 0;
    do
    {
      if (file.delete())
      {
        deleted = true;
        break;
      }
      retry++;
    } while (retry < 5);
    return deleted;
  }

  private static void closeStream (Closeable closeable)
  {
    if (closeable == null)
    {
      return;
    }
    try
    {
      closeable.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
