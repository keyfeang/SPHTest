package sg.sph.test.views.consumption;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.ObservableBoolean;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import sg.sph.test.core.Services;
import sg.sph.test.core.network.Consumption;
import sg.sph.test.core.network.INetworkService;
import sg.sph.test.misc.AppCommonViewDelegate;
import sg.sph.test.misc.AppCommonViewDelegateImp;

public class ConsumptionViewModel implements OnArrowClickEventHandler
{
  private final CompositeDisposable mDisposables = new CompositeDisposable();

  public final ConsumptionAdapter adapter = new ConsumptionAdapter(this);

  private Context mContext;
  private INetworkService mNetworkService;

  public final ObservableBoolean isBusy = new ObservableBoolean();

  private AppCommonViewDelegate mViewDelegate;

  public ConsumptionViewModel(Context context)
  {
    mContext = context;
    mViewDelegate = new AppCommonViewDelegateImp(context);
    mNetworkService = Services.getInstance().getNetworkService();
  }

  public void init()
  {
    mDisposables.clear();

    mDisposables.add(mNetworkService.getConsumptions()
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(args ->
        {
          adapter.setDataSet(args);

          sync();
        }));
  }

  void sync()
  {
    mDisposables.clear();

    isBusy.set(true);

    // TODO Fixed filter data consumption for specified year range
    mDisposables.add(mNetworkService.syncConsumption(2008, 2018)
        // Add a temporary delay to simulate progress
        .delay(3, TimeUnit.SECONDS)
        .subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(args ->
        {
          isBusy.set(false);
          adapter.setDataSet(args);
        }, ex ->
        {
          isBusy.set(false);

          mViewDelegate.handleException(ex);
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
