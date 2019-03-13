package com.keyfe.ang.foundation.util;

public class Validation
{
  /**
   * Checks if value is empty.
   */
  public static boolean isEmpty (String value)
  {
    return   value == null
          || value.trim().isEmpty();
  }
}
