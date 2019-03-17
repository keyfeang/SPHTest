package sg.sph.test;

import android.app.Application;

import io.realm.Realm;
import sg.sph.test.core.Services;

public class SPHApplication extends Application
{
  @Override
  public void onCreate()
  {
    super.onCreate();
    Realm.init(this);
    Services.init(this);
  }

  @Override
  public void onLowMemory()
  {
    super.onLowMemory();
    Services.getInstance().onLowMemory();
  }
}
