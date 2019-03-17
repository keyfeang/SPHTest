package sg.sph.test.core.network;

import io.realm.RealmObject;

public class Breakdown extends RealmObject
{
  private int quarter;
  private double volume;

  public Breakdown()
  {
  }

  public Breakdown(int quarter, double volume)
  {
    this.quarter = quarter;
    this.volume = volume;
  }

  public double getVolume()
  {
    return volume;
  }

  public void setVolume(double volume)
  {
    this.volume = volume;
  }

  public int getQuarter()
  {
    return quarter;
  }

  public void setQuarter(int quarter)
  {
    this.quarter = quarter;
  }
}
