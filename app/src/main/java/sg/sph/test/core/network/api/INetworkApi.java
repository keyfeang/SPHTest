package sg.sph.test.core.network.api;

import java.util.List;

import io.reactivex.Observable;
import sg.sph.test.core.network.data.Record;

public interface INetworkApi
{
  /**
   * Fetch data usage.
   *
   * @param offset the data page offset
   * @param limit the data page limit
   * @return the list of consumption
   */
  Observable<List<Record>> GetDataUsage(int offset, int limit);
}
