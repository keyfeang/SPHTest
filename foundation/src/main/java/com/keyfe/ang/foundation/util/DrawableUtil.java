package com.keyfe.ang.foundation.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.util.StateSet;

public final class DrawableUtil
{
  private static final int[] NormalState = StateSet.WILD_CARD;
  private static final int[] DisableState = new int[]{-android.R.attr.state_enabled};

  private static final int[] PressState = new int[]{
      android.R.attr.state_pressed,
      android.R.attr.state_enabled};

  private static final int[] SelectedState = new int[]{
      android.R.attr.state_selected,
      android.R.attr.state_enabled};

  /**
   * Produces an alpha tint color of the specified color.
   *
   * @param color the color.
   * @return the alpha color of the specified color.
   */
  public static int createAlphaTint(int color)
  {
    return Color.argb(
      Math.round(Color.alpha(color) * (77.0f / 255.0f)),
      Color.red(color),
      Color.green(color),
      Color.blue(color));
  }

  /**
   * Produces an state list drawable of specified color.
   *
   * @param normalColor the normal state color
   */
  public static Drawable createColorDrawable (int normalColor)
  {
    return createColorDrawable(normalColor, 0x33000000);
  }

  /**
   * Produces an state list color drawable based on the specified parameters.
   *
   * @param normalColor the normal state color
   * @param maskColor the mask color to apply for pressed state
   */
  public static Drawable createColorDrawable (int normalColor, int maskColor)
  {
    Drawable disableDrawable = new ColorDrawable(createAlphaTint(normalColor));

    Drawable pressedDrawable = new ColorDrawable(normalColor);
    pressedDrawable.setColorFilter(maskColor, PorterDuff.Mode.SRC_ATOP);

    Drawable normalDrawable = new ColorDrawable(normalColor);

    return createDrawable(normalDrawable, pressedDrawable, disableDrawable);
  }

  /**
   * Produces a state list drawable tinted with specified drawable.
   *
   * @param context the context
   * @param drawableResID the resource drawable ID
   */
  public static Drawable createDrawable (Context context, @DrawableRes int drawableResID)
  {
    return createDrawable(context, drawableResID, 0x33000000);
  }

  /**
   * Produces a state list drawable tinted with specified color.
   *
   * @param context the context
   * @param drawableResID the resource drawable ID
   * @param maskColor the mask color to apply
   */
  public static Drawable createDrawable (Context context, @DrawableRes int drawableResID,
      int maskColor)
  {
    Drawable disableDrawable = ContextCompat.getDrawable(context, drawableResID);
    disableDrawable.setAlpha(77);

    Drawable pressedDrawable = ContextCompat.getDrawable(context, drawableResID);
    pressedDrawable.mutate();
    pressedDrawable.setColorFilter(maskColor, PorterDuff.Mode.MULTIPLY);

    Drawable normalColor = ContextCompat.getDrawable(context, drawableResID);

    return createDrawable(normalColor, pressedDrawable, disableDrawable);
  }

  /**
   * Produces a state list drawable based on the specified parameters.
   *
   * @param normalDrawable the normal state drawable
   * @param pressedDrawable the press/selected state drawable
   * @param disableDrawable the disable state drawable
   */
  public static Drawable createDrawable (Drawable normalDrawable, Drawable pressedDrawable,
      Drawable disableDrawable)
  {
    StateListDrawable stateList = new StateListDrawable();
    stateList.addState(DisableState, disableDrawable);
    stateList.addState(PressState, pressedDrawable);
    stateList.addState(SelectedState, pressedDrawable);
    stateList.addState(NormalState, normalDrawable);
    return stateList;
  }
}
