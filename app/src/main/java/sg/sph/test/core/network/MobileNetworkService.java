package sg.sph.test.core.network;

import com.keyfe.ang.foundation.core.ServiceExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import sg.sph.test.core.network.api.INetworkApi;
import sg.sph.test.core.network.data.Record;

public class MobileNetworkService extends ServiceExecutor
                               implements INetworkService
{
  private final INetworkApi mApi;

  public MobileNetworkService(INetworkApi api)
  {
    mApi = api;
  }

  @Override
  public Observable<List<Consumption>> GetConsumption(int fromYear, int toYear)
  {
    // TODO Hard offset and limit. If need paging. Need to redesign result data model to
    // provide paging properties. HasNext(), current offset and limit
    return mApi.GetDataUsage(0, 56)
        .subscribeOn(Schedulers.newThread())
        .observeOn(Schedulers.from(getExecutor()))
        .map(args ->
        {
          List<Consumption> result = new ArrayList<>(toYear - fromYear);

          Map<Integer, Consumption> dataMap = new HashMap<>();

          for (Record r : args)
          {
            String[] yearQuarter = r.getQuarter().split("-");
            int year = Integer.parseInt(yearQuarter[0]);

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

            consumption.addVolume(r.getVolume());
          }

          return result;
        });
  }
}
