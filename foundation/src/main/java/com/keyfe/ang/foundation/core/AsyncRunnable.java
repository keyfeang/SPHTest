package com.keyfe.ang.foundation.core;

public class AsyncRunnable<T> implements Runnable
{
  private ServiceExecutor m_executor;

  void setExecutor (ServiceExecutor executor)
  {
    m_executor = executor;
  }

  @Override
  public final void run ()
  {
    new Thread()
    {
      @Override
      public void run ()
      {
        T result = doInBackground();

        if (m_executor != null)
        {
          m_executor.execute(() -> onPostExecute(result));
        }
      }
    }.start();
  }

  /**
   * Override this method to perform computation on background thread.
   */
  protected T doInBackground ()
  {
    return null;
  }

  /**
   * Runs on the service thread after {@link #doInBackground()}.
   */
  protected void onPostExecute (T result)
  {
  }
}
