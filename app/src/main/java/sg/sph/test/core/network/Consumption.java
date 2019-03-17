package sg.sph.test.core.network;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Consumption extends RealmObject
{
  @PrimaryKey
  private int year;
  private double totalVolume;

  private RealmList<Breakdown> breakdowns;

  public int getYear()
  {
    return year;
  }

  public void setYear(int year)
  {
    this.year = year;
  }

  public RealmList<Breakdown> getBreakdowns()
  {
    if (breakdowns == null)
    {
      breakdowns = new RealmList<>();
    }
    return breakdowns;
  }

  public void setBreakdowns(List<Breakdown> breakdowns)
  {
    getBreakdowns().clear();
    totalVolume = 0;

    if (breakdowns != null)
    {
      getBreakdowns().addAll(breakdowns);

      for(Breakdown breakdown : breakdowns)
      {
        totalVolume += breakdown.getVolume();
      }
    }
  }

  public void addData(Breakdown breakdown)
  {
    getBreakdowns().add(breakdown);
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
