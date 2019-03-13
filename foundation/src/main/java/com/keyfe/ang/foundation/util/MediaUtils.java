package com.keyfe.ang.foundation.util;

import android.os.Environment;
import android.support.annotation.IntDef;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MediaUtils
{
  /* Constants */

  public static final int MEDIA_TYPE_IMAGE = 1;
  public static final int MEDIA_TYPE_VIDEO = 2;

  @IntDef({MEDIA_TYPE_IMAGE, MEDIA_TYPE_VIDEO})
  public @interface MediaType
  {
  }

  /* Create a File for saving an image or video */
  public static File getOutputMediaFile (@MediaType int type, String directory)
  {
    // To be safe, you should check that the SDCard is mounted
    // using Environment.getExternalStorageState() before doing this.

    File mediaStorageDir = new File(
      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
      directory);
    // This location works best if you want the created images to be shared
    // between applications and persist after your app has been uninstalled.

    // Create the storage directory if it does not exist
    if (!mediaStorageDir.exists())
    {
      if (!mediaStorageDir.mkdirs())
      {
        return null;
      }
    }

    // Create a media file name
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
    File mediaFile;
    if (type == MEDIA_TYPE_IMAGE)
    {
      mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"
        + timeStamp + ".jpg");
    }
    else if (type == MEDIA_TYPE_VIDEO)
    {
      mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_"
        + timeStamp + ".mp4");
    }
    else
    {
      return null;
    }

    return mediaFile;
  }
}
