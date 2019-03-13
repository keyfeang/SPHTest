package com.keyfe.ang.foundation.view.navigation;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

class NavigationItemDelegate
{
  /* Properties */

  private boolean m_isRoot;
  private NavigationHandler m_navigationHandler;

  /* Contract methods */

  NavigationHandler getNavigationHandler()
  {
    return m_navigationHandler;
  }

  void setNavigationHandler(NavigationHandler navigationHandler)
  {
    m_navigationHandler = navigationHandler;
  }

  /* Navigation methods */

  /**
   * Contract method between {@link NavigationFragment} that defines whether
   * this is a root fragment.
   */
  void setIsRoot(boolean isRoot)
  {
    m_isRoot = isRoot;
  }

  /**
   * Whether this is a root fragment from the navigation stack.
   */
  protected boolean isRoot()
  {
    return m_isRoot;
  }

  /**
   * Retrieves Toolbar instance.
   */
  public Toolbar getToolbar()
  {
    return m_navigationHandler != null ? m_navigationHandler.getToolbar() : null;
  }

  /**
   * Replaces the current fragment and clears fragment stack.
   *
   * @param fragment the {@link Fragment} to attach
   * @param tag      the String fragment tag. Later can be used to retrieve
   *                 fragment instance
   */
  protected void setRootFragment(Fragment fragment, String tag)
  {
    NavigationHandler navigationHandler = m_navigationHandler;
    if (navigationHandler != null)
    {
      navigationHandler.setRootFragment(fragment, tag);
    }
  }

  /**
   * Pushes a {@link Fragment} to the navigation stack.
   *
   * @param fragment the {@link Fragment} to push into
   *                 the navigation stack
   * @param tag      the String fragment tag (Optional). Later to be used to retrieve
   *                 fragment instance
   * @param animated true if animated, otherwise, false
   */
  protected void pushFragmentToNavigation(Fragment fragment, String tag, boolean animated)
  {
    NavigationHandler navigationHandler = m_navigationHandler;
    if (navigationHandler != null)
    {
      navigationHandler.pushFragmentToNavigation(fragment, tag, animated);
    }
  }

  /**
   * Pop the {@link Fragment} instance from the
   * navigation stack.
   *
   * @param animated true if animated, otherwise, false
   */
  protected void popFragmentFromNavigation(boolean animated)
  {
    NavigationHandler navigationHandler = m_navigationHandler;
    if (navigationHandler != null)
    {
      navigationHandler.popFragmentFromNavigation(animated);
    }
  }
}
