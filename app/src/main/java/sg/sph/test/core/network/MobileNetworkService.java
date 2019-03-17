package sg.sph.test.core.network;

import com.keyfe.ang.foundation.core.ServiceExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import sg.sph.test.core.network.api.INetworkApi;
import sg.sph.test.core.network.data.Record;
import sg.sph.test.core.network.storage.INetworkStorage;

public class MobileNetworkService extends ServiceExecutor
                               implements INetworkService
{
  private final INetworkApi mApi;
  private final INetworkStorage mStorage;

  public MobileNetworkService(INetworkApi api, INetworkStorage storage)
  {
    mApi = api;
    mStorage = storage;
  }

  @Override
  public Observable<List<Consumption>> getConsumptions()
  {
    return Observable.just(mStorage.listAll())
        .subscribeOn(Schedulers.from(getExecutor()));
  }

  @Override
  public Observable<List<Consumption>> syncConsumption(int fromYear, int toYear)
  {
    // TODO Hard offset and limit. If need paging. Need to redesign result data model to
    // provide paging properties. HasNext(), current offset and limit
    return mApi.GetDataUsage(0, 56)
        .subscribeOn(Schedulers.newThread())
        .map(args ->
        {
          List<Consumption> result = new ArrayList<>(toYear - fromYear);

          Map<Integer, Consumption> dataMap = new HashMap<>();

          for (Record r : args)
          {
            String[] yearQuarter = r.getQuarter().split("-");
            int year = Integer.parseInt(yearQuarter[0]);
            int quarter = Integer.parseInt(String.valueOf(yearQuarter[1].charAt(1)));

            if (year < fromYear || year > toYear)
            {
              continue;
            }

            Consumption consumption = dataMap.get(year);
            if (consumption == null)
            {
              dataMap.put(year, consumption = new Consumption());
              consumption.setYear(year);
              result.add(consumption);
            }

            consumption.addData(new Breakdown(quarter, r.getVolume()));
          }

          mStorage.clearAll();
          mStorage.bulkInsertOrReplace(result);

          return result;
        });
  }
}
