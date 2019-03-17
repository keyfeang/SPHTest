package sg.sph.test.views.consumption;

import android.app.AlertDialog;
import android.content.Context;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import sg.sph.test.core.Services;
import sg.sph.test.core.network.Consumption;
import sg.sph.test.core.network.INetworkService;

public class ConsumptionViewModel implements OnArrowClickEventHandler
{
  private final CompositeDisposable mDisposables = new CompositeDisposable();

  public final ConsumptionAdapter adapter = new ConsumptionAdapter(this);

  private Context mContext;
  private INetworkService mNetworkService;

  public ConsumptionViewModel(Context context)
  {
    mContext = context;
    mNetworkService = Services.getInstance().getNetworkService();
  }

  public void init()
  {
    mDisposables.clear();

    // TODO Fixed filter data consumption for specified year range
    mDisposables.add(mNetworkService.GetConsumption(2008, 2018)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(args ->
        {
          adapter.setDataSet(args);
        }, ex ->
        {
          // TODO Handle exception
        }));
  }

  public void destroy()
  {
    mDisposables.clear();
  }

  @Override
  public void onArrowClicked(Consumption data)
  {
    new AlertDialog.Builder(mContext)
        .setTitle(String.valueOf(data.getYear()))
        .setMessage(String.valueOf(data.getTotalVolume()))
        .create()
        .show();
  }
}
