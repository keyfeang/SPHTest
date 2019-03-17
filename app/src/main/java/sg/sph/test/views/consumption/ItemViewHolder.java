package sg.sph.test.views.consumption;

import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.keyfe.ang.foundation.util.NumberUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sg.sph.test.core.network.Consumption;
import sg.sph.test.core.network.Breakdown;
import sg.sph.test.databinding.ConsumptionItemBinding;

public class ItemViewHolder extends RecyclerView.ViewHolder
{
  public final ObservableField<String> year = new ObservableField<>();
  public final ObservableField<String> volume = new ObservableField<>();
  public final ObservableInt arrowVisibility = new ObservableInt();

  private static QuarterComparator smComparator = new QuarterComparator();

  public ItemViewHolder(@NonNull ConsumptionItemBinding binding)
  {
    super(binding.getRoot());
    binding.setModel(this);
  }

  public void onBind(Consumption consumption)
  {
    year.set(String.valueOf(consumption.getYear()));
    volume.set(NumberUtil.format(consumption.getTotalVolume(), 2));

    Collections.sort(consumption.getBreakdowns(), smComparator);

    boolean hasDecreased = checkIfHasDecreased(consumption);
    arrowVisibility.set(hasDecreased ? View.VISIBLE : View.INVISIBLE);
  }

  boolean checkIfHasDecreased(Consumption consumption)
  {
    boolean hasDecreased = false;
    List<Breakdown> data = consumption.getBreakdowns();
    for (int i = 0; i < data.size(); i++)
    {
      Breakdown d = data.get(i);
      if (hasDecreased)
      {
        break;
      }

      if (i == 0)
      {
        continue;
      }

      hasDecreased = d.getVolume() < data.get(i - 1).getVolume();
    }
    return hasDecreased;
  }

  private static class QuarterComparator implements Comparator<Breakdown>
  {
    @Override
    public int compare(Breakdown data, Breakdown t1)
    {
      return Integer.valueOf(data.getQuarter()).compareTo(t1.getQuarter());
    }
  }
}
