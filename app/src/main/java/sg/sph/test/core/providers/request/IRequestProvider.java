package sg.sph.test.core.providers.request;

import com.keyfe.ang.foundation.tools.http.HttpConnection;

import io.reactivex.Observable;

public interface IRequestProvider
{
  /**
   * Builds http connection from specified uri
   *
   * @param uri the resource uri
   * @return the connection instance
   */
  Observable<HttpConnection> buildUpon(String uri);
}
