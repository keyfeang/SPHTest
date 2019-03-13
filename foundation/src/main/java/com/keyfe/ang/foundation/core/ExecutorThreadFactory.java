package com.keyfe.ang.foundation.core;

import java.util.Locale;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A ThreadFactory class that can detect if a thread was spawned from an instance of this
 * ThreadFactory.
 */
class ExecutorThreadFactory  implements ThreadFactory
{
  private static final AtomicInteger sm_poolNumberReference = new AtomicInteger(1);

  private final int m_poolNumber;
  private final ThreadGroup m_group;
  private final AtomicInteger m_threadNumber = new AtomicInteger(1);
  private final String m_namePrefix;

    /* Initialization */

  /**
   * Generic constructor.
   */
  ExecutorThreadFactory ()
  {
    m_poolNumber = sm_poolNumberReference.getAndIncrement();

    SecurityManager s = System.getSecurityManager();
    m_group = ((s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
    m_namePrefix = String.format(Locale.US, "pool-%d-thread-", m_poolNumber);
  }

    /* Thread checking methods */

  /**
   * Convenience method to check if the currently running thread was spawned
   * From this ThreadFactory instance.
   */
  boolean isCurrentThreadFromThisFactory ()
  {
    return isThreadFromThisFactory(Thread.currentThread());
  }

  /**
   * Returns true if the Thread instance provided was spawned from this
   * ThreadFactory instance, otherwise, false.
   */
  boolean isThreadFromThisFactory (Thread thread)
  {
    return    thread instanceof InternalThread
          && ((InternalThread) thread).getPoolNumber() == m_poolNumber;
  }

    /* ThreadFactory methods */

  @Override
  public Thread newThread (Runnable r)
  {
    Thread t = new InternalThread(
      m_group,
      r,
      String.format(
        Locale.US,
        "%s%d",
        m_namePrefix,
        m_threadNumber.getAndIncrement()),
      0,
      m_poolNumber);

    if (t.isDaemon())
    {
      t.setDaemon(false);
    }
    if (t.getPriority() != Thread.NORM_PRIORITY)
    {
      t.setPriority(Thread.NORM_PRIORITY);
    }
    return t;
  }

    /* InternalThread definition */

  /**
   * Internal definition of a Thread that tags its instance with a thread factory pool number.
   */
  static class InternalThread extends Thread
  {
    private final int m_poolNumber;

    /**
     * Constructor that includes a thread factory pool number tag it was created with.
     */
    InternalThread (ThreadGroup group, Runnable target, String name, long stackSize,
      int poolNumber)
    {
      super(group, target, name, stackSize);
      m_poolNumber = poolNumber;
    }

    /**
     * Returns the pool number it was created with.
     */
    int getPoolNumber ()
    {
      return m_poolNumber;
    }

    @Override
    public void run ()
    {
      try
      {
        super.run();
      }
      catch (Exception e)
      {
          /* Do nothing. But for debugging purposes, we should print the stack trace. */
        e.printStackTrace();
      }
    }
  }
}
