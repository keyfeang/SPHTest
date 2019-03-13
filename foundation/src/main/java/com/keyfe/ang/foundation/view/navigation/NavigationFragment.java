package com.keyfe.ang.foundation.view.navigation;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;

import com.keyfe.ang.foundation.R;
import com.keyfe.ang.foundation.view.BaseFragment;

public class NavigationFragment extends BaseFragment
    implements NavigationHandler,
    NavigationBarHandler
{
  /* Properties */

  private NavigationDelegate m_navigationDelegate;

  /* Fragment life-cycle methods */

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    m_navigationDelegate = new NavigationDelegate(getChildFragmentManager(), savedInstanceState);
  }

  @Override
  public void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);
    m_navigationDelegate.saveInstanceState(outState);
  }

  /* Custom callback methods */

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
    boolean handled = false;
    Fragment fragment = getCurrentFragment();
    if (fragment != null
        && fragment instanceof BaseFragment)
    {
      handled = ((BaseFragment) fragment).onKeyDown(keyCode, event);
    }

    if (!handled
        && getBackStackSize() > 0
        && keyCode == KeyEvent.KEYCODE_BACK)
    {
      handled = true;
      popFragmentFromNavigation(true);
    }

    return handled
        || super.onKeyDown(keyCode, event);
  }

  /* Property methods */

  /**
   * Returns the number of entries currently in the back stack.
   */
  public int getBackStackSize()
  {
    return m_navigationDelegate.getBackStackSize();
  }

  @Override
  public Toolbar getToolbar()
  {
    final View view = getView();
    return view != null ? (Toolbar) view.findViewById(R.id.toolbar) : null;
  }

  /* Navigation methods */

  /**
   * See {@link #setRootFragment(Fragment, String)}.
   */
  public void setRootFragment(Fragment fragment)
  {
    setRootFragment(fragment, null);
  }

  /* Navigation handler methods */

  /**
   * Returns the current fragment.
   */
  protected Fragment getCurrentFragment()
  {
    return m_navigationDelegate.getCurrentFragment();
  }

  /**
   * Overrides current back stack. This mean that all transaction will be committed into the
   * specified back stack.
   */
  protected Fragment setBackStack(String name)
  {
    return m_navigationDelegate.setBackStack(name);
  }

  /**
   * Called navigation has changed. This give chance to listen to navigation change.
   */
  @CallSuper
  protected void onNavigationChanged(Fragment oldFragment, Fragment newFragment)
  {
    /* Clean toolbar old state. */
    Toolbar toolbar = getToolbar();

    Menu menu = toolbar.getMenu();
    if (menu.size() > 0)
    {
      menu.clear();
    }
  }

  @Override
  public void setRootFragment(Fragment fragment, String tag)
  {
    m_navigationDelegate.setRootFragment(fragment, tag);

    /* Inform navigation changed. */
    onNavigationChanged(null, fragment);
  }

  @Override
  public void pushFragmentToNavigation(Fragment fragment, String tag,
                                       boolean animated)
  {
    Fragment curFragment = m_navigationDelegate.pushFragmentToNavigation(fragment, tag, animated);

    /* Inform navigation changed. */
    onNavigationChanged(curFragment, fragment);
  }

  @Override
  public void popFragmentFromNavigation(boolean animated)
  {
    Pair<Fragment, Fragment> tuple = m_navigationDelegate.popFragmentFromNavigation(animated);

    /* Inform navigation changed. */
    if (tuple != null)
    {
      onNavigationChanged(tuple.first, tuple.second);
    }
  }
}
