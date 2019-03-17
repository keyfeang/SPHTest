package sg.sph.test.views.consumption;

import sg.sph.test.core.network.Consumption;

public interface OnArrowClickEventHandler
{
  /**
   * Called when arrow button is clicked.
   *
   * @param data the reference item data.
   */
  void onArrowClicked(Consumption data);
}
