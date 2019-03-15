package sg.sph.test.core;

import android.content.Context;

import sg.sph.test.core.network.INetworkService;
import sg.sph.test.core.network.MobileNetworkService;
import sg.sph.test.core.network.api.INetworkApi;
import sg.sph.test.core.network.api.MobileNetworkApi;
import sg.sph.test.core.providers.request.IRequestProvider;
import sg.sph.test.core.providers.request.RequestProvider;

public class Services
{
  /* Properties */

  private static Services smInstance;

  private MobileNetworkService mNetworkService;

  /* Initializations */

  public static Services getInstance ()
  {
    return smInstance;
  }

  public static void init (Context context)
  {
    smInstance = new Services(context);
  }

  private Services (Context context)
  {
    // TODO Add Dependency inversion implementation here.

    IRequestProvider requestProvider = new RequestProvider(context);

    INetworkApi networkApi = new MobileNetworkApi(requestProvider);

    mNetworkService = new MobileNetworkService(networkApi);
  }

  /* Methods */

  public void onLowMemory ()
  {
    mNetworkService.onLowMemory();
  }

  public INetworkService getNetworkService()
  {
    return mNetworkService;
  }
}
