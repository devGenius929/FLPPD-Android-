package com.antigravitystudios.flppd;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antigravitystudios.flppd.databinding.ListItemConnectionCardBinding;
import com.antigravitystudios.flppd.models.Connection;

import java.util.List;

public class ConnectionsListAdapter extends RecyclerView.Adapter<ConnectionsListAdapter.ItemViewHolder> {

    private List<Connection> items;
    private ConnectionItemListener listener;

    public ConnectionsListAdapter(List<Connection> items, ConnectionItemListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ItemViewHolder(ListItemConnectionCardBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.bind(items.get((position)));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        ListItemConnectionCardBinding binding;

        ItemViewHolder(ListItemConnectionCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    listener.onItemClicked(items.get(position));
                }
            });
        }

        public void bind(Connection item) {
            binding.setItem(item);
            binding.executePendingBindings();
        }
    }

}
