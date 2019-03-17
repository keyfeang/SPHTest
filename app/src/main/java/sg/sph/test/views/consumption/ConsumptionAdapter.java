package sg.sph.test.views.consumption;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import sg.sph.test.R;
import sg.sph.test.core.network.Consumption;
import sg.sph.test.databinding.ConsumptionItemBinding;

public class ConsumptionAdapter extends RecyclerView.Adapter
{
  private List<Consumption> mDataSet = new ArrayList<>(0);

  public void setDataSet(List<Consumption> dataSet)
  {
    mDataSet.clear();
    if (dataSet != null)
    {
      mDataSet.addAll(dataSet);
    }
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount()
  {
    return mDataSet.size();
  }

  @NonNull
  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
  {
    Context context = viewGroup.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);

    ConsumptionItemBinding binding =
        DataBindingUtil.inflate(inflater, R.layout.consumption_item, viewGroup, false);

    return new ItemViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i)
  {
    Consumption data = mDataSet.get(i);
    ((ItemViewHolder)viewHolder).onBind(data);
  }
}
