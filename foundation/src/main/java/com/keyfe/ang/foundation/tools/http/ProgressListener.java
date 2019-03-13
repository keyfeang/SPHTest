package com.keyfe.ang.foundation.tools.http;

/**
 * Interface definition current progress of the request.
 */
public interface ProgressListener
{
  /**
   * Called to inform the progress of the request. This is usually called every time it receives a
   * response data chunk.
   *
   * @param progress the reference progress value of the request download. (value from 0-100).
   */
  void onProgressUpdate(int progress);
}
