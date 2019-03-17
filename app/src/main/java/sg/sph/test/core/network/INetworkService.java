package sg.sph.test.core.network;

import java.util.List;

import io.reactivex.Observable;

public interface INetworkService
{
  /**
   * Retrieve data consumption from specified year range.
   *
   * @param fromYear the from year
   * @param toYear the to year
   * @return the data consumption from specified years
   */
  Observable<List<Consumption>> GetConsumption(int fromYear, int toYear);
}
