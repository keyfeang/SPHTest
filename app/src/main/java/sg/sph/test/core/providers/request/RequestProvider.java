package sg.sph.test.core.providers.request;

import android.content.Context;
import android.net.Uri;

import com.keyfe.ang.foundation.tools.http.HttpConnection;

import io.reactivex.Observable;
import sg.sph.test.configs.Configuration;

public class RequestProvider implements IRequestProvider
{
  private Context mContext;

  public RequestProvider(Context context)
  {
    mContext = context;
  }

  @Override
  public Observable<HttpConnection> buildUpon(String uri)
  {
    return Observable.fromCallable(() ->
    {
      Uri.Builder builder = Uri.parse(Configuration.NetworkBaseURL + uri).buildUpon();

      HttpConnection conn = new HttpConnection(mContext, builder.toString());
      appendDefaultProperties(conn);
      return conn;
    });
  }

  /**
   * Method that append default request properties. (e.g. Token).
   */
  private void appendDefaultProperties (HttpConnection conn)
  {
    conn.setRequestProperty("Content-Type", "application/json");
  }
}
