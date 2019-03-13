package com.keyfe.ang.foundation.view;

import android.content.Context;
import android.view.View;

import java.util.List;

/**
 * A concrete ViewController class that finalizes and exposes the methods to public so that it can
 * be useable by activities and fragments.
 */
public class RootViewController extends ViewController
{
  /* ViewController life-cycle methods */

  @Override
  public final void onCreate(Context context)
  {
    super.onCreate(context);
  }

  @Override
  public final List<ViewController> onCreateViewChildControllers(Context context)
  {
    return super.onCreateViewChildControllers(context);
  }

  @Override
  public final void onCreateView(View view)
  {
    super.onCreateView(view);
  }

  @Override
  public final void onResume()
  {
    super.onResume();
  }

  @Override
  public final void onPause()
  {
    super.onPause();
  }

  @Override
  public final void onLowMemory()
  {
    super.onLowMemory();
  }

  @Override
  public final void onDestroyView()
  {
    super.onDestroyView();
  }

  @Override
  public final void onDestroy()
  {
    super.onDestroy();
  }

  /* Child controller methods */

  @Override
  public List<ViewController> getChildControllers()
  {
    return super.getChildControllers();
  }

  /* Package level methods */

  /**
   * Sets the list of child view controllers.
   */
  void setChildViewControllers(List<ViewController> controllers)
  {
    addChildViewControllers(controllers);
  }
}