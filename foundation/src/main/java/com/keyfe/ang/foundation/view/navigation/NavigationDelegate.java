package com.keyfe.ang.foundation.view.navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;

import com.google.common.reflect.TypeToken;
import com.keyfe.ang.foundation.R;
import com.keyfe.ang.foundation.tools.JsonSerializer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

/**
 * Navigation delegate class definition.
 */
class NavigationDelegate
{
  /* Properties */

  private FragmentManager m_fm;
  private String m_currBackStack = "default";
  private Map<String, Stack<String>> m_fragmentTagStack = new HashMap<>();

  NavigationDelegate(FragmentManager fm, Bundle savedInstanceState)
  {
    m_fm = fm;

    /* Restore fragment tag stack if able. */
    if (savedInstanceState != null)
    {
      final String stack = savedInstanceState.getString("stack");
      if (stack != null)
      {
        Type type = new TypeToken<Map<String, Stack<String>>>()
        {
        }.getType();
        m_fragmentTagStack = JsonSerializer.serialize(stack, type);
      }
    }
  }

  void saveInstanceState(Bundle outState)
  {
    int size = m_fragmentTagStack.size();
    if (size == 0)
    {
      return;
    }

    /* Save fragment tag stack. */
    final String stack = JsonSerializer.deserialize(m_fragmentTagStack);
    outState.putString("stack", stack);
  }

  /**
   * Returns the number of entries currently in the back stack.
   */
  int getBackStackSize()
  {
    return Math.max(0, getStack().size() - 1);
  }

  /**
   * Replaces the current fragment and clears fragment stack.
   *
   * @param fragment the {@link Fragment} to attach
   * @param tag      the String fragment tag. Later can be used to retrieve fragment instance
   */
  void setRootFragment(Fragment fragment, String tag)
  {
    Stack<String> stack = getStack();
    FragmentManager manager = m_fm;
    FragmentTransaction transaction = manager.beginTransaction();

    transaction.setCustomAnimations(
        R.anim.fragment_show_enter,
        R.anim.fragment_show_exit);

    /* Clear all fragment from stack. */
    for (String stackTag : stack)
    {
      Fragment stackFragment = m_fm.findFragmentByTag(stackTag);
      transaction.remove(stackFragment);
    }
    stack.clear();

    /* Set root boolean flag if able. */
    if (fragment instanceof NavigationItemFragment)
    {
      ((NavigationItemFragment) fragment).setIsRoot(true);
    }

    /* If no string tag is specified. Generate default fragment tag. Later to be
     * used to retrieve the fragment in the stack.
     */
    if (tag == null || tag.isEmpty())
    {
      tag = generateDefaultTagForIndex(0);
    }

    transaction.replace(R.id.content, fragment, tag);
    transaction.commit();

    stack.push(tag);
  }

  /**
   * Pushes the {@link Fragment} to the navigation stack.
   *
   * @param fragment the {@link Fragment} to push into the navigation stack
   * @param tag      the String fragment tag (Optional). Later to be used to retrieve fragment instance
   * @param animated true if animated, otherwise, false
   * @return current fragment in stack
   */
  Fragment pushFragmentToNavigation(Fragment fragment, String tag, boolean animated)
  {
    Stack<String> stack = getStack();
    FragmentManager manager = m_fm;
    FragmentTransaction transaction = manager.beginTransaction();

    /* If no string tag is specified. Generate default fragment tag. Later to be
     * used to retrieve the fragment in the stack.
     */
    int stackSize = stack.size();
    if (tag == null || tag.isEmpty())
    {
      tag = generateDefaultTagForIndex(stackSize);
    }
    stack.push(tag);

    if (animated)
    {
      transaction.setCustomAnimations(
          R.anim.fragment_push_enter,
          R.anim.fragment_push_exit);
    }

    /* Retrieve current fragment instance and hide if able. */
    Fragment curFragment = null;
    if (stackSize > 0)
    {
      String curFragmentTag = stack.get(stackSize - 1);
      curFragment = manager.findFragmentByTag(curFragmentTag);
      transaction.detach(curFragment);
    }

    transaction.add(R.id.content, fragment, tag);
    transaction.commit();

    return curFragment;
  }

  /**
   * Pops the current {@link Fragment} from the stack.
   *
   * @param animated true if animated, otherwise, false
   * @return first as the fragment to be removed from stack, while the second is the fragment that
   * will be shown. Null non of the navigation state has changed.
   */
  Pair<Fragment, Fragment> popFragmentFromNavigation(boolean animated)
  {
    Pair<Fragment, Fragment> tuple = null;

    Stack<String> stack = getStack();
    int stackSize = stack.size();
    if (stackSize > 1)
    {
      FragmentManager manager = m_fm;
      FragmentTransaction transaction = manager.beginTransaction();

      String curFragmentTag = stack.pop();
      String prevFragmentTag = stack.get(stackSize - 2);

      /* Retrieve current and previous fragment instances. */
      Fragment curFragment = manager.findFragmentByTag(curFragmentTag);
      Fragment prevFragment = manager.findFragmentByTag(prevFragmentTag);

      if (animated)
      {
        transaction.setCustomAnimations(
            R.anim.fragment_pop_enter,
            R.anim.fragment_pop_exit);
      }

      transaction.remove(curFragment);

      /* Checks if the previous fragment is detach from UI. See
       * onSaveInstanceState(Bundle).
       */
      if (prevFragment.isDetached())
      {
        transaction.attach(prevFragment);
      }

      if (prevFragment.isHidden())
      {
        transaction.show(prevFragment);
      }

      transaction.commit();

      tuple = new Pair<>(curFragment, prevFragment);
    }
    return tuple;
  }

  /**
   * Overrides current back stack. This mean that all transaction will be committed into the
   * specified back stack.
   */
  Fragment setBackStack(String backStack)
  {
    FragmentManager manager = m_fm;
    FragmentTransaction trx = manager.beginTransaction();

    /* Retrieve current fragment instance and hide if able. */
    Fragment curFragment = getCurrentFragment();
    if (curFragment != null)
    {
      trx.setCustomAnimations(
          R.anim.fragment_show_enter,
          R.anim.fragment_show_exit);

      trx.detach(curFragment);
    }

    /* Switch back stack. */
    m_currBackStack = backStack;

    Fragment stackCurFragment = getCurrentFragment();
    if (stackCurFragment != null)
    {
      trx.attach(stackCurFragment);
    }

    trx.commit();
    return stackCurFragment;
  }

  /**
   * Returns the current fragment.
   */
  Fragment getCurrentFragment()
  {
    Fragment fragment = null;
    Stack<String> tagBackStack = getStack();
    if (!tagBackStack.isEmpty())
    {
      String topTag = tagBackStack.get(tagBackStack.size() - 1);
      fragment = m_fm.findFragmentByTag(topTag);
    }
    return fragment;
  }

  /**
   * Return the current back stack.
   */
  Stack<String> getStack()
  {
    Stack<String> stack = m_fragmentTagStack.get(m_currBackStack);
    if (stack == null)
    {
      m_fragmentTagStack.put(m_currBackStack, stack = new Stack<>());
    }
    return stack;
  }

  /**
   * Returns a default string fragment tag for the specified index from the
   * stack.
   */
  private String generateDefaultTagForIndex(int stackIndex)
  {
    return String.format(Locale.US, "%s-%d", m_currBackStack, stackIndex);
  }
}
