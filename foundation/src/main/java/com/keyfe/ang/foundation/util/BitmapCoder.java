package com.keyfe.ang.foundation.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.IntRange;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class that handles encoding and decoding bitmaps.
 */
public final class BitmapCoder
{
  /**
   * Produces the raw byte array from an existing bitmap.
   */
  public static byte[] encodeBitmap (Bitmap bitmap, int quality)
  {
    byte[] photo;

    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    bitmap.compress(CompressFormat.JPEG, quality, bos);
    photo = bos.toByteArray();

    try
    {
      bos.close();
    } catch (IOException e)
    {
      /* Do nothing. */
    }
    return photo;
  }

  /**
   * Creates a bitmap from a raw byte array.
   */
  public static Bitmap createBitmap (byte[] photo)
  {
    return BitmapFactory.decodeByteArray(photo, 0, photo.length);
  }

  /**
   * Creates a bitmap from a file.
   */
  public static Bitmap createBitmap (String filePath) throws IOException
  {
    Bitmap bitmap = BitmapFactory.decodeFile(filePath);

    /* Evaluate if we need to rotate the bitmap and replace the old bitmap. */
    bitmap = fixImageRotation(bitmap, filePath);
    return bitmap;
  }

  public static Bitmap createBitmapStream (byte[] photo)
  {
    Bitmap bitmap = null;

    try
    {
      InputStream is = new ByteArrayInputStream(photo);
      bitmap = BitmapFactory.decodeStream(is, null, null);
      is.close();
    } catch (IOException e)
    {
      e.printStackTrace();
    }
    return bitmap;
  }

  /**
   * Creates a bitmap from a raw byte array.
   *
   * @param photo  the raw byte array of the bitmap
   * @param width  the integer target width
   * @param height the integer target height
   * @return the Bitmap instance
   */
  public static Bitmap createBitmap (byte[] photo, int width, int height)
  {
    /* Determine the raw bitmap dimensions. */
    int outWidth;
    int outHeight;
    {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeByteArray(photo, 0, photo.length, options);

      outWidth = options.outWidth;
      outHeight = options.outHeight;
    }

    /* Evaluate possible sample size. */
    int inSampleSize = 1;
    if (   outWidth > height
        || outHeight > width)
    {
      final int halfHeight = outWidth / 2;
      final int halfWidth = outHeight / 2;

      /*
       * Calculate the largest inSampleSize value that is a power of 2 and keeps
       * both height and width larger than the requested height and width.
       */
      while (   (halfHeight / inSampleSize) > height
             && (halfWidth / inSampleSize) > width)
      {
        inSampleSize *= 2;
      }
    }

    /* Decode actual bitmap. */

    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = false;
    options.inSampleSize = inSampleSize;

    return BitmapFactory.decodeByteArray(photo, 0, photo.length, options);
  }

  /**
   * Creates a bitmap from a file path.
   *
   * @param filePath the String path to the raw image file
   * @param width    the integer target width
   * @param height   the integer target height
   * @return the Bitmap instance
   * @throws IOException if filePath is not readable or of the wrong format
   */
  public static Bitmap createBitmap (String filePath, int width, int height) throws IOException
  {
    /* Determine the raw bitmap dimensions. */
    int outWidth;
    int outHeight;
    {
      BitmapFactory.Options options = new BitmapFactory.Options();
      options.inJustDecodeBounds = true;
      BitmapFactory.decodeFile(filePath, options);

      outWidth = options.outWidth;
      outHeight = options.outHeight;
    }

    /* Evaluate possible sample size. */
    int inSampleSize = 1;
    if (   outWidth > height
        || outHeight > width)
    {
      final int halfHeight = outWidth / 2;
      final int halfWidth = outHeight / 2;

      /* Calculate the largest inSampleSize value that is a power of 2 and keeps  both height and
       * width larger than the requested height and width.
       */
      while (   (halfHeight / inSampleSize) > height
             && (halfWidth / inSampleSize) > width)
      {
        inSampleSize *= 2;
      }
    }

    /* Decode actual bitmap. */

    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = false;
    options.inSampleSize = inSampleSize;

    Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

    /* Evaluate if we need to rotate the bitmap and replace the old bitmap. */
    bitmap = fixImageRotation(bitmap, filePath);

    return bitmap;
  }

  /**
   * Creates a compressed bitmap.
   */
  public static Bitmap compressBitmap (Bitmap bitmap,
      @IntRange(from = 0, to = 100) int quality)
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    bitmap.compress(CompressFormat.JPEG, quality, out);
    return BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
  }

  /**
   * Common method to fix image rotation.
   */
  private static Bitmap fixImageRotation (Bitmap bitmap, String rawImageFilePath) throws IOException
  {
    /* Evaluate if we need to rotate the bitmap and replace the old bitmap. */
    float angle = getRawImageRotation(rawImageFilePath);
    if (   bitmap != null
        && angle != 0)
    {
      Matrix matrix = new Matrix();
      matrix.postRotate(angle);

      Bitmap oldBitmap = bitmap;

      bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
          bitmap.getHeight(), matrix, true);

      oldBitmap.recycle();
    }
    return bitmap;
  }

  /**
   * Convenience method to get the adjusted rotation of a raw image on a specified file path.
   *
   * @param rawImageFilePath the String path to the raw image file
   * @throws IOException if rawImageFilePath is not readable or of the wrong format
   */
  public static float getRawImageRotation (String rawImageFilePath)
      throws IOException
  {
    float angle = 0;
    ExifInterface exifReader;
    exifReader = new ExifInterface(rawImageFilePath);

    int orientation =
        exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

    switch (orientation)
    {
      case ExifInterface.ORIENTATION_NORMAL:
      case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
        angle = 0;
        break;
      case ExifInterface.ORIENTATION_ROTATE_180:
      case ExifInterface.ORIENTATION_FLIP_VERTICAL:
        angle = 180;
        break;
      case ExifInterface.ORIENTATION_TRANSPOSE:
      case ExifInterface.ORIENTATION_ROTATE_90:
        angle = 90;
        break;
      case ExifInterface.ORIENTATION_TRANSVERSE:
      case ExifInterface.ORIENTATION_ROTATE_270:
        angle = 270;
      default:
        break;
    }
    return angle;
  }
}
