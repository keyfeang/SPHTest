package sg.sph.test.misc;

import android.app.AlertDialog;
import android.content.Context;

import com.keyfe.ang.foundation.tools.http.HttpException;
import com.keyfe.ang.foundation.tools.http.StatusCode;

import sg.sph.test.R;

public class AppCommonViewDelegateImp implements AppCommonViewDelegate
{
  /* Properties */

  private Context mContext;

  public AppCommonViewDelegateImp (Context context)
  {
    mContext = context;
  }

  /* Methods */

  @Override
  public boolean handleException (Throwable throwable)
  {
    String message = null;
    if (throwable instanceof HttpException)
    {
      message = throwable.getMessage();

      if (message == null)
      {
        switch (((HttpException) throwable).statusCode)
        {
          case StatusCode.CODE_NETWORK_UNAVAILABLE:
            message = mContext.getString(R.string.error_no_connection);
            break;
          case StatusCode.CODE_UNKNOWN_HOST:
          case StatusCode.CODE_TIMEOUT:
          case StatusCode.CODE_SERVER_ERROR:
            message = mContext.getString(R.string.error_server_unreachable);
            break;
        }
      }
    }

    boolean handled = false;
    if (    message != null
      && !message.isEmpty())
    {
      handled = true;

      new AlertDialog.Builder(mContext)
          .setTitle(null)
          .setMessage(message)
          .create()
          .show();
    }
    return handled;
  }
}
