package com.antigravitystudios.flppd.ui.network;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.antigravitystudios.flppd.SPreferences;
import com.antigravitystudios.flppd.databinding.ListItemConnectionBinding;
import com.antigravitystudios.flppd.databinding.ListItemConnectionPendingCardBinding;
import com.antigravitystudios.flppd.databinding.ListItemConnectionSearchBinding;
import com.antigravitystudios.flppd.events.ConnectionAcceptedEvent;
import com.antigravitystudios.flppd.events.ConnectionAddedEvent;
import com.antigravitystudios.flppd.events.ConnectionDeletedEvent;
import com.antigravitystudios.flppd.events.ConnectionRejectedEvent;
import com.antigravitystudios.flppd.events.ConnectionsListReceivedEvent;
import com.antigravitystudios.flppd.events.ConnectionsPendingListReceivedEvent;
import com.antigravitystudios.flppd.events.ConnectionsWaitingListReceivedEvent;
import com.antigravitystudios.flppd.events.SearchUserResultEvent;
import com.antigravitystudios.flppd.models.Connection;
import com.antigravitystudios.flppd.models.User;
import com.antigravitystudios.flppd.networking.NetworkModule;
import com.antigravitystudios.flppd.ui.base.BaseFragmentViewModel;
import com.antigravitystudios.flppd.ui.profile.ProfileActivity;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkViewModel extends BaseFragmentViewModel<NetworkFragment> implements MaterialSearchView.OnQueryTextListener, MaterialSearchView.SearchViewListener {

    public ObservableBoolean loading = new ObservableBoolean();
    public ObservableBoolean searching = new ObservableBoolean();
    public ConnectionsAdapter connectionsAdapter;
    public ConnectionsAdapter connectionsWaitingAdapter;
    public ConnectionsPendingAdapter connectionsPendingAdapter;
    public ConnectionsSearchingAdapter connectionsSearchingAdapter;
    public MaterialSearchView searchView;

    private User user;
    private ArrayList<Connection> connections = new ArrayList<>();
    private ArrayList<Connection> connectionsPending = new ArrayList<>();
    private ArrayList<Connection> connectionsWaiting = new ArrayList<>();
    private ArrayList<User> connectionsSearching = new ArrayList<>();
    private int loadCount;

    public NetworkViewModel(NetworkFragment fragment) {
        super(fragment);

        user = SPreferences.getUser();
        connectionsAdapter = new ConnectionsAdapter(connections);
        connectionsWaitingAdapter = new ConnectionsAdapter(connectionsWaiting);
        connectionsSearchingAdapter = new ConnectionsSearchingAdapter();
        connectionsPendingAdapter = new ConnectionsPendingAdapter();

        loadData();
    }

    private void loadData() {
        loading.set(true);
        loadCount = 0;
        NetworkModule.getInteractor().getUserConnections(SPreferences.getToken(), user.getUserId());
        NetworkModule.getInteractor().getWaitingConnections(SPreferences.getToken());
        NetworkModule.getInteractor().getPendingConnections(SPreferences.getToken());
    }

    private void searchUsers(String text) {
        if(text.trim().isEmpty()) return;

        loading.set(true);
        text = text.trim();
        NetworkModule.getInteractor().searchUsers(SPreferences.getToken(), text);
    }

    private void checkLoadCount() {
        if(++loadCount == 3) loading.set(false);
    }

    @Override
    protected void onFragmentDetach() {
        super.onFragmentDetach();

        if (searchView != null) {
            searchView.setOnQueryTextListener(null);
            searchView.setOnSearchViewListener(null);
        }
    }

    @Override
    protected void registerBus(EventBus bus) {
        bus.register(this);
    }

    @Override
    protected void unregisterBus(EventBus bus) {
        bus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionsListReceivedResult(ConnectionsListReceivedEvent event) {
        if (!user.getUserId().equals(event.getUserId())) return;

        if (event.isSuccessful()) {
            for (Connection connection : event.getResult()) {
                connection.getUser().setConnect_state(1);
            }
            connections.clear();
            connections.addAll(event.getResult());
            connectionsAdapter.notifyDataSetChanged();
        } else {
            activity.showToast(event.getErrorMessage());
        }

        checkLoadCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionsPendingListReceivedResult(ConnectionsPendingListReceivedEvent event) {

        if (event.isSuccessful()) {
            for (Connection connection : event.getResult()) {
                connection.getUser().setConnect_state(0);
            }
            connectionsPending.clear();
            connectionsPending.addAll(event.getResult());
            connectionsPendingAdapter.notifyDataSetChanged();
        } else {
            activity.showToast(event.getErrorMessage());
        }

        checkLoadCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionsWaitingListReceivedResult(ConnectionsWaitingListReceivedEvent event) {

        if (event.isSuccessful()) {
            for (Connection connection : event.getResult()) {
                connection.getUser().setConnect_state(2);
            }
            connectionsWaiting.clear();
            connectionsWaiting.addAll(event.getResult());
            connectionsWaitingAdapter.notifyDataSetChanged();
        } else {
            activity.showToast(event.getErrorMessage());
        }

        checkLoadCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSearchUserResultReceived(SearchUserResultEvent event) {

        if (event.isSuccessful()) {
            for (User user : event.getResult()) {
                if(connections.contains(user)) {
                    user.setConnect_state(1);
                } else if (connectionsPending.contains(user)) {
                    user.setConnect_state(2);
                } else {
                    user.setConnect_state(0);
                }
            }
            connectionsSearching.clear();
            connectionsSearching.addAll(event.getResult());
            connectionsSearchingAdapter.notifyDataSetChanged();
        } else {
            activity.showToast(event.getErrorMessage());
        }

        loading.set(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionAcceptedResult(ConnectionAcceptedEvent event) {

        if (event.isSuccessful()) {
            Toast.makeText(activity, event.getResult().getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            activity.showToast(event.getErrorMessage());
        }

        checkLoadCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionAddedResult(ConnectionAddedEvent event) {

        if (event.isSuccessful()) {
            Toast.makeText(activity, event.getResult().getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            activity.showToast(event.getErrorMessage());
        }

        checkLoadCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionDeletedResult(ConnectionDeletedEvent event) {

        if (event.isSuccessful()) {
            Toast.makeText(activity, event.getResult().getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            activity.showToast(event.getErrorMessage());
        }

        checkLoadCount();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionRejectedResult(ConnectionRejectedEvent event) {

        if (event.isSuccessful()) {
            Toast.makeText(activity, event.getResult().getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            activity.showToast(event.getErrorMessage());
        }

        checkLoadCount();
    }

    public void setSearchView(MaterialSearchView searchView, MenuItem item) {
        this.searchView = searchView;
        searchView.setMenuItem(item);
        searchView.setOnQueryTextListener(this);
        searchView.setOnSearchViewListener(this);
    }

    private Timer timer = new Timer();
    private final long DELAY = 1000;

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String newText) {
        timer.cancel();
        timer = new Timer();
        timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        searchUsers(newText);
                    }
                },
                DELAY
        );
        return true;
    }

    @Override
    public void onSearchViewShown() {
        searching.set(true);
    }

    @Override
    public void onSearchViewClosed() {
        searching.set(false);
        loadData();
        connectionsSearching.clear();
        connectionsSearchingAdapter.notifyDataSetChanged();
    }

    private void onConnectClicked(User user) {
        user.setConnect_state(2);
        user.notifyChange();
        NetworkModule.getInteractor().addConnection(SPreferences.getToken(), user.getUserId());
    }

    private void onConnectedClicked(final User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Delete")
                .setMessage("Do you want to remove this user from your network?")
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        user.setConnect_state(0);
                        user.notifyChange();
                        NetworkModule.getInteractor().deleteConnection(SPreferences.getToken(), user.getUserId());
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void onPendingClicked(User user) {
        user.setConnect_state(0);
        user.notifyChange();
        NetworkModule.getInteractor().deleteConnection(SPreferences.getToken(), user.getUserId());
    }

    private void onAcceptClicked(Connection connection, int position) {
        connection.getUser().setConnect_state(1);
        connections.add(0, connection);
        connectionsPending.remove(connection);
        connectionsPendingAdapter.notifyItemRemoved(position);
        connectionsAdapter.notifyItemInserted(0);
        NetworkModule.getInteractor().acceptConnection(SPreferences.getToken(), connection.getUser().getUserId());
    }

    private void onRejectClicked(Connection connection, int position) {
        connectionsPending.remove(connection);
        connectionsPendingAdapter.notifyItemRemoved(position);
        NetworkModule.getInteractor().rejectConnection(SPreferences.getToken(), connection.getUser().getUserId());
    }

    private void onAvatarClick(int userId) {
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra("userId", userId);
        activity.startActivity(intent);
    }

    public class ConnectionsPendingAdapter extends RecyclerView.Adapter<ConnectionsPendingAdapter.ItemViewHolder> {

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ItemViewHolder(ListItemConnectionPendingCardBinding.inflate(inflater, parent, false));
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            holder.bind(connectionsPending.get((position)));
        }

        @Override
        public int getItemCount() {
            return connectionsPending.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            ListItemConnectionPendingCardBinding binding;

            ItemViewHolder(final ListItemConnectionPendingCardBinding binding) {
                super(binding.getRoot());
                this.binding = binding;

                binding.acceptBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        onAcceptClicked(connectionsPending.get(position), position);
                    }
                });
                binding.rejectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        onRejectClicked(connectionsPending.get(position), position);
                    }
                });
                binding.avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        onAvatarClick(connectionsPending.get(position).getUser().getUserId());
                    }
                });
            }

            public void bind(Connection item) {
                binding.setItem(item);
                binding.executePendingBindings();
            }
        }
    }

    public class ConnectionsAdapter extends RecyclerView.Adapter<ConnectionsAdapter.ItemViewHolder> {

        private ArrayList<Connection> connections;

        public ConnectionsAdapter(ArrayList<Connection> connections) {
            this.connections = connections;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ItemViewHolder(ListItemConnectionBinding.inflate(inflater, parent, false));
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            holder.bind(connections.get((position)));
        }

        @Override
        public int getItemCount() {
            return connections.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            ListItemConnectionBinding binding;

            ItemViewHolder(final ListItemConnectionBinding binding) {
                super(binding.getRoot());
                this.binding = binding;

                binding.connectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        onConnectClicked(connections.get(position).getUser());
                    }
                });
                binding.connectedBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        onConnectedClicked(connections.get(position).getUser());
                    }
                });
                binding.pendingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        onPendingClicked(connections.get(position).getUser());
                    }
                });
                binding.avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        onAvatarClick(connections.get(position).getUser().getUserId());
                    }
                });
            }

            public void bind(Connection item) {
                binding.setItem(item);
                binding.executePendingBindings();
            }
        }
    }

    public class ConnectionsSearchingAdapter extends RecyclerView.Adapter<ConnectionsSearchingAdapter.ItemViewHolder> {

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ItemViewHolder(ListItemConnectionSearchBinding.inflate(inflater, parent, false));
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            holder.bind(connectionsSearching.get((position)));
        }

        @Override
        public int getItemCount() {
            return connectionsSearching.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            ListItemConnectionSearchBinding binding;

            ItemViewHolder(final ListItemConnectionSearchBinding binding) {
                super(binding.getRoot());
                this.binding = binding;

                binding.connectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        onConnectClicked(connectionsSearching.get(position));
                    }
                });
                binding.connectedBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        onConnectedClicked(connectionsSearching.get(position));
                    }
                });
                binding.pendingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        onPendingClicked(connectionsSearching.get(position));
                    }
                });
                binding.avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        onAvatarClick(connectionsSearching.get(position).getUserId());
                    }
                });
            }

            public void bind(User item) {
                binding.setItem(item);
                binding.executePendingBindings();
            }
        }
    }

}
