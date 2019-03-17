package sg.sph.test.core.network;

public class Consumption
{
  private int year;
  private double volume;

  public int getYear()
  {
    return year;
  }

  public void setYear(int year)
  {
    this.year = year;
  }

  public double getVolume()
  {
    return volume;
  }

  public void setVolume(double volume)
  {
    this.volume = volume;
  }

  public void addVolume(double volume)
  {
    this.volume += volume;
  }
}
