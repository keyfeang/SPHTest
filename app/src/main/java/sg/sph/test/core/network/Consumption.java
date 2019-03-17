package sg.sph.test.core.network;

import java.util.ArrayList;
import java.util.List;

public class Consumption
{
  private int year;
  private double totalVolume;

  private List<Breakdown> breakdowns = new ArrayList<>();

  public int getYear()
  {
    return year;
  }

  public void setYear(int year)
  {
    this.year = year;
  }

  public List<Breakdown> getBreakdowns()
  {
    return breakdowns;
  }

  public void setBreakdowns(List<Breakdown> breakdowns)
  {
    this.breakdowns.clear();
    totalVolume = 0;

    if (breakdowns != null)
    {
      this.breakdowns.addAll(breakdowns);

      for(Breakdown breakdown : breakdowns)
      {
        totalVolume += breakdown.getVolume();
      }
    }
  }

  public void addData(Breakdown breakdown)
  {
    this.breakdowns.add(breakdown);
    totalVolume += breakdown.getVolume();
  }

  public double getTotalVolume()
  {
    return totalVolume;
  }

  public void setTotalVolume(double totalVolume)
  {
    this.totalVolume = totalVolume;
  }
}
