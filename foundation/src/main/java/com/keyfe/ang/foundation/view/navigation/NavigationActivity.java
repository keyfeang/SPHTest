package com.keyfe.ang.foundation.view.navigation;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;

import com.keyfe.ang.foundation.R;
import com.keyfe.ang.foundation.view.BaseActivity;
import com.keyfe.ang.foundation.view.BaseFragment;

public class NavigationActivity extends BaseActivity
    implements NavigationHandler,
    NavigationBarHandler
{
  /* Properties */

  private NavigationDelegate m_navigationDelegate;

  /* Life-cycle methods */

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    m_navigationDelegate = new NavigationDelegate(getSupportFragmentManager(), savedInstanceState);
  }

  @Override
  public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)
  {
    super.onSaveInstanceState(outState, outPersistentState);
    m_navigationDelegate.saveInstanceState(outState);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
    boolean handled = false;
    Fragment fragment = m_navigationDelegate.getCurrentFragment();
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

  /* Navigation methods */

  /**
   * Called navigation has changed. This give chance to listen to navigation change.
   */
  @CallSuper
  protected void onNavigationChanged(Fragment oldFragment, Fragment newFragment)
  {
    /* Clean toolbar old state. */
    final Toolbar toolbar = getToolbar();

    Menu menu = toolbar.getMenu();
    if (menu.size() > 0)
    {
      menu.clear();
    }
  }

  @Override
  public Toolbar getToolbar()
  {
    return (Toolbar) findViewById(R.id.toolbar);
  }

  @Override
  public int getBackStackSize()
  {
    return m_navigationDelegate.getBackStackSize();
  }

  /**
   * See {@link #setRootFragment(Fragment, String)}.
   */
  public void setRootFragment(Fragment fragment)
  {
    setRootFragment(fragment, null);
  }

  @Override
  public void setRootFragment(Fragment fragment, String tag)
  {
    m_navigationDelegate.setRootFragment(fragment, tag);

    /* Inform navigation changed. */
    onNavigationChanged(null, fragment);
  }

  @Override
  public void pushFragmentToNavigation(Fragment fragment, String tag, boolean animated)
  {
    final Fragment curFragment =
        m_navigationDelegate.pushFragmentToNavigation(fragment, tag, animated);

    /* Inform navigation changed. */
    onNavigationChanged(curFragment, fragment);
  }

  @Override
  public void popFragmentFromNavigation(boolean animated)
  {
    final Pair<Fragment, Fragment> tuple = m_navigationDelegate.popFragmentFromNavigation(animated);

    /* Inform navigation changed. */
    if (tuple != null)
    {
      onNavigationChanged(tuple.first, tuple.second);
    }
  }
}
