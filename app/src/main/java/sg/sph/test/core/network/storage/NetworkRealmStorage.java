package sg.sph.test.core.network.storage;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.annotations.RealmModule;
import sg.sph.test.core.network.Breakdown;
import sg.sph.test.core.network.Consumption;

public class NetworkRealmStorage implements INetworkStorage
{
  private Realm Realm()
  {
    RealmConfiguration config = new RealmConfiguration.Builder()
        .name("network.realm")
        .schemaVersion(1)
        .modules(new Module())
        .deleteRealmIfMigrationNeeded()
        .build();
    return Realm.getInstance(config);
  }

  public NetworkRealmStorage()
  {
  }

  @Override
  public List<Consumption> listAll()
  {
    Realm realm = Realm();
    RealmQuery<Consumption> query = realm.where(Consumption.class);
    List<Consumption> rs = query.findAll();
    return realm.copyFromRealm(rs);
  }

  @Override
  public void bulkInsertOrReplace(List<Consumption> entities)
  {
    Realm().executeTransaction(realm -> realm.copyToRealmOrUpdate(entities));
  }

  @Override
  public void clearAll()
  {
    Realm().executeTransaction(realm -> realm.where(Consumption.class)
                                            .findAll()
                                            .deleteAllFromRealm());
  }

  @RealmModule(classes = {Consumption.class, Breakdown.class})
  private class Module
  {
  }
}
