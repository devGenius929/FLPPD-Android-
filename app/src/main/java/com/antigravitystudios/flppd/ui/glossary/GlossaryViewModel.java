package com.antigravitystudios.flppd.ui.glossary;

import android.content.Intent;
import android.content.res.AssetManager;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.antigravitystudios.flppd.databinding.ListItemGlossaryBinding;
import com.antigravitystudios.flppd.ui.WebviewActivity;
import com.antigravitystudios.flppd.ui.base.BaseFragmentViewModel;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GlossaryViewModel extends BaseFragmentViewModel<GlossaryFragment> implements MaterialSearchView.OnQueryTextListener {

    public ObservableBoolean loading = new ObservableBoolean();
    public GlossaryListAdapter listAdapter;

    public MaterialSearchView searchView;

    private ArrayList<GlossaryItem> allItems = new ArrayList<>();
    private SortedList<GlossaryItem> items = new SortedList<GlossaryItem>(GlossaryItem.class, new SortedList.Callback<GlossaryItem>() {
        @Override
        public void onInserted(int position, int count) {
            listAdapter.notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            listAdapter.notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            listAdapter.notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            listAdapter.notifyItemRangeChanged(position, count);
        }

        @Override
        public int compare(GlossaryItem a, GlossaryItem b) {
            return a.compareTo(b);
        }

        @Override
        public boolean areContentsTheSame(GlossaryItem oldItem, GlossaryItem newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(GlossaryItem item1, GlossaryItem item2) {
            return item1.getTitle().equals(item2.getTitle());
        }
    });

    public GlossaryViewModel(GlossaryFragment fragment) {
        super(fragment);

        listAdapter = new GlossaryListAdapter();

        Thread thread = new Thread() {
            @Override
            public void run() {
                loading.set(true);
                init();
                loading.set(false);
            }
        };
        thread.start();
    }

    @Override
    protected void onFragmentDetach() {
        super.onFragmentDetach();

        if (searchView != null) {
            searchView.setOnQueryTextListener(null);
        }
    }

    @Override
    protected void registerBus(EventBus bus) {
    }

    @Override
    protected void unregisterBus(EventBus bus) {
    }

    private void init() {
        try {
            String dir = "glossary";
            AssetManager assetManager = activity.getAssets();
            String[] letters = assetManager.list(dir);
            for (String letter : letters) {
                String path = dir + File.separator + letter;
                String[] files = assetManager.list(path);
                for (String file : files) {
                    String title = file.substring(0, file.lastIndexOf('.'));
                    allItems.add(new GlossaryItem(title, path + File.separator + file));
                }
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    items.addAll(allItems);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onItemClicked(GlossaryItem item) {
        Intent intent = new Intent(activity, WebviewActivity.class);
        intent.putExtra("title", item.getTitle());
        intent.putExtra("assetPath", item.getPath());
        activity.startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<GlossaryItem> filteredModelList = filter(allItems, newText);

        items.beginBatchedUpdates();
        for (int i = items.size() - 1; i >= 0; i--) {
            final GlossaryItem model = items.get(i);
            if (!filteredModelList.contains(model)) {
                items.remove(model);
            }
        }
        items.addAll(filteredModelList);
        items.endBatchedUpdates();

        fragment.binding.recyclerView.scrollToPosition(0);
        return true;
    }

    private static List<GlossaryItem> filter(List<GlossaryItem> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();
        final List<GlossaryItem> filteredModelList = new ArrayList<>();
        for (GlossaryItem model : models) {
            final String text = model.getTitle().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public void setSearchView(MaterialSearchView searchView, MenuItem item) {
        this.searchView = searchView;
        searchView.setMenuItem(item);
        searchView.setOnQueryTextListener(this);
    }

    public class GlossaryItem implements Comparable<GlossaryItem> {

        private String title;
        private String path;

        public GlossaryItem(String title, String path) {
            this.title = title;
            this.path = path;
        }

        public String getTitle() {
            return title;
        }

        public String getPath() {
            return path;
        }

        @Override
        public int compareTo(@NonNull GlossaryItem item) {
            return getTitle().compareTo(item.getTitle());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof GlossaryItem)) return false;

            GlossaryItem that = (GlossaryItem) o;

            return title.equals(that.title);
        }

        @Override
        public int hashCode() {
            return title.hashCode();
        }
    }

    public class GlossaryListAdapter extends RecyclerView.Adapter<GlossaryListAdapter.ItemViewHolder> {

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ItemViewHolder(ListItemGlossaryBinding.inflate(inflater, parent, false));
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

            ListItemGlossaryBinding binding;

            ItemViewHolder(ListItemGlossaryBinding binding) {
                super(binding.getRoot());
                this.binding = binding;

                binding.getRoot().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        onItemClicked(items.get(position));
                    }
                });
            }

            public void bind(GlossaryItem item) {
                binding.setItem(item);
                binding.executePendingBindings();
            }
        }

    }

}
