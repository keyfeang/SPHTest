package com.keyfe.ang.foundation.core;

import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ServiceExecutor
{
  /* Properties */

  private ExecutorService m_internalQueue;

  private volatile boolean m_isManagerActive = false;

  private ExecutorThreadFactory m_internalThreadFactory;

  /**
   * @return {@link Executor}.
   */
  protected Executor getExecutor ()
  {
    if (!m_isManagerActive)
    {
      return null;
    }
    return getInternalQueue();
  }

  /* Initialization */

  /**
   * Generic constructor.
   */
  @MainThread
  public ServiceExecutor ()
  {
    /* Do nothing. */
  }

  /* Manager life-cycle methods */

  /**
   * Called when the application is starting.
   */
  @CallSuper
  @MainThread
  public void onCreate ()
  {
    m_isManagerActive = true;
  }

  /**
   * This is called when the overall system is running low on memory.
   */
  @MainThread
  public void onLowMemory ()
  {
    /* Do nothing */
  }

  /**
   * Remove all pending scheduled messages queues.
   */
  @CallSuper
  @MainThread
  public void onDestroy ()
  {
    m_isManagerActive = false;

    synchronized (this)
    {
      if (m_internalQueue != null)
      {
        m_internalQueue.shutdown();
        m_internalQueue = null;
      }

      if (m_internalThreadFactory != null)
      {
        m_internalThreadFactory = null;
      }
    }
  }

  /* Property methods */

  /**
   * Internal and lazy-loading property method of a queued ServiceExecutor instance.
   */
  private ExecutorService getInternalQueue ()
  {
    if (m_internalQueue == null)
    {
      synchronized (this)
      {
        m_internalQueue = Executors.newSingleThreadExecutor(getThreadFactory());
      }
    }
    return m_internalQueue;
  }

  /**
   * Internal and lazy-loading property method of a InternalThreadFactory
   * instance.
   */
  private ExecutorThreadFactory getThreadFactory ()
  {
    if (m_internalThreadFactory == null)
    {
      synchronized (this)
      {
        m_internalThreadFactory = new ExecutorThreadFactory();
      }
    }
    return m_internalThreadFactory;
  }

  /* Manager thread confinement methods */

  /**
   * Submits a Runnable for internal execution.
   *
   * @param r the runnable that will be executed.
   */
  protected void execute (final Runnable r)
  {
    if (!m_isManagerActive)
    {
      return;
    }

    if (r instanceof AsyncRunnable)
    {
      ((AsyncRunnable) r).setExecutor(this);
    }

    try
    {
      Runnable wrapper = () ->
      {
        try
        {
          r.run();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      };

      if (getThreadFactory().isCurrentThreadFromThisFactory())
      {
        wrapper.run();
      }
      else
      {
        getInternalQueue().submit(wrapper);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  /**
   * Submits a callback for internal execution.
   *
   * @param c the callback that will be executed.
   * @return the return value of the Callable.call() method
   */
  protected <T> T execute (Callable<T> c)
  {
    if (!m_isManagerActive)
    {
      return null;
    }

    T value = null;
    if (getThreadFactory().isCurrentThreadFromThisFactory())
    {
      try
      {
        value = c.call();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    else
    {
      try
      {
        value = getInternalQueue().submit(c).get();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
    return value;
  }
}
