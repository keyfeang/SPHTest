package com.keyfe.ang.foundation.view.navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Locale;

import com.keyfe.ang.foundation.R;

public abstract class TabNavigationFragment extends NavigationFragment
{
  /* Properties */

  private static final String STATE_SELECTED_TAB = "selectedTab";

  private int m_selectedTab;
  private TabAdapter m_adapter;

  /* Life-cycle callback. */

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    if (savedInstanceState != null)
    {
      m_selectedTab = savedInstanceState.getInt(STATE_SELECTED_TAB, 0);
    }
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);

    /* Init tab stack if able. */
    setupTabView();

    if (savedInstanceState == null)
    {
      switchTab(m_selectedTab);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState)
  {
    /* Save selected tab. */
    outState.putInt(STATE_SELECTED_TAB, m_selectedTab);
    super.onSaveInstanceState(outState);
  }

  public void setTabAdapter(TabAdapter adapter)
  {
    if (adapter == m_adapter)
    {
      return;
    }
    m_adapter = adapter;
    setupTabView();
  }

  /* Internal methods */

  private int getTabCount()
  {
    return m_adapter != null ? m_adapter.getItemCount() : 0;
  }

  private void setupTabView()
  {
    View tabContentView;
    View view = getView();
    if (view == null
        || m_adapter == null
        || (tabContentView = view.findViewById(R.id.tab_container)) == null)
    {
      return;
    } else if (!(tabContentView instanceof LinearLayout))
    {
      throw new RuntimeException("Tab container must be an instance of LinearLayout.");
    }

    int count = getTabCount();
    for (int i = 0; i < count; i++)
    {
      View itemView = m_adapter.getView(i, (ViewGroup) tabContentView);
      itemView.setEnabled(m_selectedTab != i);
      itemView.setTag(i);
      itemView.setOnClickListener(v -> switchTab((int) v.getTag()));

      LinearLayout.LayoutParams params =
          new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
      ((ViewGroup) tabContentView).addView(itemView, params);
    }
  }

  /**
   * Callback method that will be called when a tab has been selected.
   */
  protected void onTabSelected(int index)
  {
  }

  private void switchTab(int index)
  {
    if (getTabCount() <= 0
        || getView() == null)
    {
      return;
    }

    m_selectedTab = index;

    /* Switch back stack. */
    Fragment fragment = setBackStack(String.format(Locale.US, "Tab%d", index));
    if (fragment == null)
    {
      fragment = m_adapter.getFragment(index);
      setRootFragment(fragment);
    }

    /* Inform navigation changed. */
    onNavigationChanged(null, fragment);

    /* Disable selected tab. */
    ViewGroup tabContentView = getView().findViewById(R.id.tab_container);
    int childCount = tabContentView.getChildCount();
    for (int i = 0; i < childCount; i++)
    {
      boolean selected = i == m_selectedTab;
      View view = tabContentView.findViewWithTag(i);
      view.setEnabled(!selected);
      view.setSelected(selected);
    }

    onTabSelected(index);
  }

  /* Navigation methods */

  public static abstract class TabAdapter
  {
    /**
     * Returns the total number of tab items.
     */
    public abstract int getItemCount();

    /**
     * Get the fragment that will be displayed initially for the specified position.
     */
    public abstract Fragment getFragment(int position);

    /**
     * Get the view that displays the item for the specified position.
     */
    public abstract View getView(int position, ViewGroup parent);
  }
}
