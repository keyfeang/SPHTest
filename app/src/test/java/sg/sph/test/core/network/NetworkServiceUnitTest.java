package sg.sph.test.core.network;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import sg.sph.test.core.network.api.INetworkApi;
import sg.sph.test.core.network.data.Record;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class NetworkServiceUnitTest
{
  @Test
  public void testSubscription()
  {
    INetworkApi mockApi = mock(INetworkApi.class);
    doReturn(Observable.just(new ArrayList())).when(mockApi).GetDataUsage(0, 56);

    INetworkService service = new MobileNetworkService(mockApi);
    ((MobileNetworkService) service).onCreate();

    TestObserver<List<Consumption>> testObserver = service.syncConsumption(0, 56).test();

    testObserver.assertNoErrors();
    testObserver.assertSubscribed();
    testObserver.assertNotComplete();

    testObserver.dispose();
  }

  @Test
  public void testEmpty()
  {
    List<Record> records = new ArrayList<>();

    TestScheduler testScheduler = new TestScheduler();


    INetworkApi mockApi = mock(INetworkApi.class);
    doReturn(Observable.just(records)).when(mockApi).GetDataUsage(0, 56);

    INetworkService service = new MobileNetworkService(mockApi);
    ((MobileNetworkService) service).onCreate();

    Observable<List<Consumption>> observable = service.syncConsumption(0, 56);

    observable.subscribeOn(testScheduler);
    observable.observeOn(testScheduler);

    TestObserver<List<Consumption>> testObserver = new TestObserver<>();

    observable.subscribe(testObserver);

    testObserver.assertValue(List::isEmpty);

    testObserver.dispose();
  }

  @Test
  public void testRecord()
  {
    TestScheduler testScheduler = new TestScheduler();

    List<Record> records = new ArrayList<>();
    records.add(new Record(0, "2006-Q4", 0.5));
    records.add(new Record(1, "2007-Q1", 0.5));
    records.add(new Record(2, "2008-Q2", 0.5));
    records.add(new Record(3, "2008-Q3", 0.5));
    records.add(new Record(4, "2009-Q1", 0.5));

    INetworkApi mockApi = mock(INetworkApi.class);
    doReturn(Observable.just(records)).when(mockApi).GetDataUsage(0, 56);

    INetworkService service = new MobileNetworkService(mockApi);
    ((MobileNetworkService) service).onCreate();

    Observable<List<Consumption>> observable = service.syncConsumption(2008, 2018);

    observable.subscribeOn(testScheduler);

    TestObserver<List<Consumption>> testObserver = new TestObserver<>();
    observable.subscribe(testObserver);

    testObserver.assertNoErrors();
    testObserver.assertSubscribed();

    testObserver.assertValue(args -> !args.isEmpty());
    testObserver.assertValue(args -> args.size() == 2);

    testObserver.assertValue(args -> args.get(0).getBreakdowns().size() == 2);
    testObserver.assertValue(args -> args.get(0).getYear() == 2008);
    testObserver.assertValue(args -> args.get(0).getTotalVolume() == 1.0);

    testObserver.assertValue(args -> args.get(1).getYear() == 2009);
    testObserver.assertValue(args -> args.get(1).getTotalVolume() == 0.5);

    testObserver.dispose();
  }
}
