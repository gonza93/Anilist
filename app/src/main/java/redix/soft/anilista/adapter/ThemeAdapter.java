package redix.soft.anilista.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import redix.soft.anilista.R;
import redix.soft.anilista.databinding.ListThemeBinding;
import redix.soft.anilista.listener.ItemClickListener;
import redix.soft.anilista.model.Theme;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ViewHolder> {

    private List<Theme> themes;
    private Context context;

    public ThemeAdapter(List<Theme> themes, Context context) {
        this.themes = themes;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ListThemeBinding mBinding;
        private ItemClickListener listener;

        public ViewHolder(ListThemeBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public void bind(Theme theme) {
            mBinding.setTheme(theme);
            mBinding.executePendingBindings();
        }

        public void setItemClickListener(ItemClickListener listener) {
            mBinding.getRoot().setOnClickListener(this);
            this.listener = listener;
        }

        @Override
        public void onClick(View view) { }
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListThemeBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_theme, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(themes.get(position));
    }

    @Override
    public int getItemCount() {
        return themes.size();
    }

}