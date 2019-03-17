package sg.sph.test.core.network.storage;

import java.util.List;

import sg.sph.test.core.network.Consumption;

public interface INetworkStorage
{
  /**
   * Return all consumption instances.
   */
  List<Consumption> listAll();

  /**
   * Bulk insert or replace consumption instances.
   */
  void bulkInsertOrReplace (List<Consumption> entities);

  /**
   * Deleta all stored instances.
   */
  void clearAll();
}
