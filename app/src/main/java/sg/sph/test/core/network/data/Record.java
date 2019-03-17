package sg.sph.test.core.network.data;

import com.google.gson.annotations.SerializedName;

public class Record
{
  @SerializedName("_id")
  private long ID;

  private String quarter;

  @SerializedName("volume_of_mobile_data")
  private double volume;

  public Record()
  {
  }

  public Record(long id, String quarter, double volume)
  {
    this.ID = id;
    this.quarter = quarter;
    this.volume = volume;
  }

  public long getID()
  {
    return ID;
  }

  public void setID(long ID)
  {
    this.ID = ID;
  }

  public String getQuarter()
  {
    return quarter;
  }

  public void setQuarter(String quarter)
  {
    this.quarter = quarter;
  }

  public double getVolume()
  {
    return volume;
  }

  public void setVolume(double volume)
  {
    this.volume = volume;
  }
}
