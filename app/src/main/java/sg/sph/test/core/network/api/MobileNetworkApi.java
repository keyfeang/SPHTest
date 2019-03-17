package sg.sph.test.core.network.api;

import com.keyfe.ang.foundation.tools.http.RequestMethod;

import java.util.List;

import io.reactivex.Observable;
import sg.sph.test.core.network.data.Record;
import sg.sph.test.core.network.parser.RecordParser;
import sg.sph.test.core.providers.request.IRequestProvider;

public class MobileNetworkApi implements INetworkApi
{
  private final IRequestProvider mRequestProvider;

  public MobileNetworkApi(IRequestProvider requestProvider)
  {
    mRequestProvider = requestProvider;
  }

  @Override
  public Observable<List<Record>> GetDataUsage(int offset, int limit)
  {
    return mRequestProvider.buildUpon("/action/datastore_search")
        .map(conn ->
        {
          conn.setRequestMethod(RequestMethod.METHOD_GET);

          conn.addRequestParameter("offset", offset);
          conn.addRequestParameter("limit", limit);

          // TODO Temporarily hard code resource ID. Change this to configuration
          conn.addRequestParameter("resource_id", "a807b7ab-6cad-4aa6-87d0-e283a7353a0f");

          String data = conn.getResponse();
          return new RecordParser().parse(data);
        });
  }
}
