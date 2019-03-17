package sg.sph.test;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.keyfe.ang.foundation.view.BaseActivity;

import sg.sph.test.views.consumption.ConsumptionFragment;

public class MainActivity extends BaseActivity
{
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    FragmentManager fm = getSupportFragmentManager();

    if (fm.findFragmentByTag("consumptionFragment") == null)
    {
      FragmentTransaction trx = fm.beginTransaction();
      trx.replace(R.id.content, new ConsumptionFragment(), "consumptionFragment");
      trx.commit();
    }
  }
}
