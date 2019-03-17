package sg.sph.test.views.consumption;

import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.keyfe.ang.foundation.util.NumberUtil;

import sg.sph.test.core.network.Consumption;
import sg.sph.test.databinding.ConsumptionItemBinding;

public class ItemViewHolder extends RecyclerView.ViewHolder
{
  public final ObservableField<String>  year = new ObservableField<>();
  public final ObservableField<String> volume = new ObservableField<>();

  public ItemViewHolder(@NonNull ConsumptionItemBinding binding)
  {
    super(binding.getRoot());
    binding.setModel(this);
  }

  public void onBind(Consumption consumption)
  {
    year.set(String.valueOf(consumption.getYear()));
    volume.set(NumberUtil.format(consumption.getVolume(), 2));
  }
}
