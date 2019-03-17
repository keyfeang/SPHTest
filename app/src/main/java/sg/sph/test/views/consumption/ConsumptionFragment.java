package sg.sph.test.views.consumption;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.keyfe.ang.foundation.view.navigation.NavigationItemFragment;
import com.keyfe.ang.foundation.widgets.DividerItemDecoration;

import sg.sph.test.R;
import sg.sph.test.core.Services;
import sg.sph.test.databinding.ConsumptionFragmentBinding;

public class ConsumptionFragment extends NavigationItemFragment
{
  private ConsumptionViewModel mViewModel;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState)
  {
    ConsumptionFragmentBinding binding =
      DataBindingUtil.inflate(inflater, R.layout.consumption_fragment, container, false);

    mViewModel =  new ConsumptionViewModel(getContext());
    binding.setModel(mViewModel);

    binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                                                                  LinearLayoutManager.VERTICAL,
                                                                  false));
    binding.recyclerView.addItemDecoration(new DividerItemDecoration());



    return binding.getRoot();
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState)
  {
    super.onViewCreated(view, savedInstanceState);
    mViewModel.init();
  }

  @Override
  public void onDestroyView()
  {
    mViewModel.destroy();
    super.onDestroyView();
  }
}
