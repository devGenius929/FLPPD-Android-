package com.antigravitystudios.flppd;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antigravitystudios.flppd.databinding.ListItemPropertyBinding;
import com.antigravitystudios.flppd.models.Property;

import java.util.List;

public class PropertyListAdapter extends RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder> {

    private List<Property> properties;
    private PropertyItemListener listener;

    public PropertyListAdapter(List<Property> properties, PropertyItemListener listener) {
        this.properties = properties;
        this.listener = listener;
    }

    @Override
    public PropertyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new PropertyViewHolder(ListItemPropertyBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(PropertyViewHolder holder, int position) {
        holder.bind(properties.get(position));
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    class PropertyViewHolder extends RecyclerView.ViewHolder {

        ListItemPropertyBinding binding;

        PropertyViewHolder(ListItemPropertyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    listener.onItemClicked(properties.get(position));
                }
            });
        }

        public void bind(Property item) {
            binding.setItem(item);
            binding.executePendingBindings();
        }
    }

}
