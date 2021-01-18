package com.antigravitystudios.flppd.ui.Evaluate.Itemize.PurchaseCost;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.antigravitystudios.flppd.models.realm.RealmProperty;
import com.antigravitystudios.flppd.models.realm.RealmPropertyItemPurchaseCost;
import com.antigravitystudios.flppd.Presentation.Interfaces.ViewHolderClickInterface;
import com.antigravitystudios.flppd.R;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;

public class ItemizePurchaseCostActivity extends AppCompatActivity implements ViewHolderClickInterface {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.iv_add)
    ImageView iv_add;

    @OnClick(R.id.iv_add)
    public void addClick(){
        FragmentManager fm = getSupportFragmentManager();
        InsertDialog dialog = InsertDialog.newInstance(property.getId());

        dialog.show(fm,"add");
    }

    private RealmProperty property;
    private static Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemize_purchase_costs);
        ButterKnife.bind(this);
        realm = Realm.getDefaultInstance();

        property = realm.where(RealmProperty.class).equalTo("id",getIntent().getStringExtra("id")).findFirst();

        Log.d("item ",property.getRealmWorksheetFlip().getItems_purchase_costs().get(0).getName());

        RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();
        dragMgr.setInitiateOnMove(true);
        //dragMgr.setInitiateOnLongPress(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dragMgr.createWrappedAdapter(new MyAdapter(property.getRealmWorksheetFlip().getItems_purchase_costs(),this)));

        dragMgr.attachRecyclerView(recyclerView);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void itemClicked(Object data) {

    }

    @Override
    public void itemClicked2(Object data) {

    }

    @Override
    public void itemClicked3(Object data) {

    }

    static class ViewHolder extends AbstractDraggableItemViewHolder {
        @BindView(R.id.text)
        TextView textView;
        @BindView(R.id.et_value)
        TextInputEditText et_value;

        private ViewHolderClickInterface<String> callback;

        public ViewHolder(View itemView, ViewHolderClickInterface<String> callback) {
            super(itemView);
            this.callback = callback;
            ButterKnife.bind(this,itemView);
            et_value.setInputType(InputType.TYPE_NULL);
            et_value.clearFocus();
        }
    }

    static class MyAdapter extends RealmRecyclerViewAdapter<RealmPropertyItemPurchaseCost,ViewHolder>implements DraggableItemAdapter<ViewHolder> {

        private ViewHolderClickInterface<String> itemClick;

        public MyAdapter(OrderedRealmCollection<RealmPropertyItemPurchaseCost> items, ViewHolderClickInterface callback) {
            super(items, true);
            setHasStableIds(true); // this is required for D&D feature.
            this.itemClick = callback;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            ButterKnife.bind(this, v);
            return new ViewHolder(v, itemClick);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textView.setText(getItem(position).getName());
        }

        @Override
        public int getItemCount() {
            return  getData().size();
        }

        @Override
        public void onMoveItem(int fromPosition, int toPosition) {
            realm.beginTransaction();
            RealmPropertyItemPurchaseCost movedItem = getData().remove(fromPosition);
            getData().add(toPosition, movedItem);
            realm.commitTransaction();
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public boolean onCheckCanStartDrag(ViewHolder holder, int position, int x, int y) {
            return true;
        }

        @Override
        public ItemDraggableRange onGetItemDraggableRange(ViewHolder holder, int position) {
            return null;
        }

        @Override
        public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
            return true;
        }

        @Override
        public void onItemDragStarted(int position) {

        }

        @Override
        public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {

        }
    }


    private AlertDialog insertDialog(String title, final AddListener addListener){

        LayoutInflater inflater = getLayoutInflater();
        View layout=inflater.inflate(R.layout.add_purchase_cost,null);

        final EditText data= layout.findViewById(R.id.et_data);

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setView(layout);
        builder.setTitle(title);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if (!data.getText().toString().isEmpty())
                    //itemClickListener.onClick(0,data.getText().toString()+" sqft");
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        return dialog;
    }

    public interface AddListener {
        void onAdd(int id, RealmPropertyItemPurchaseCost item);
    }




}
