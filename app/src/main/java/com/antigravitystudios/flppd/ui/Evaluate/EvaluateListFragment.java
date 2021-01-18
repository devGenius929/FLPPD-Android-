package com.antigravitystudios.flppd.ui.Evaluate;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.antigravitystudios.flppd.Presentation.Interfaces.ViewHolderClickInterface;
import com.antigravitystudios.flppd.Presentation.Utils.Convertions;
import com.antigravitystudios.flppd.ui.Evaluate.Detail.DetailActivity;
import com.antigravitystudios.flppd.ui.main.MainActivity;
import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.models.realm.RealmProperty;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;


public class EvaluateListFragment extends Fragment implements ViewHolderClickInterface {

    //PropertiesContract.Presenter mPresenter;
    MainActivity activity;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.properties_progress)
    View properties_progress;

    private Realm realm;
    private PropertyRecyclerViewAdapter propertyRecyclerViewAdapter;
    private Boolean rental;


    public EvaluateListFragment() {
        // Required empty public constructor
    }

    public static EvaluateListFragment newInstance(boolean rental) {
        EvaluateListFragment fragment = new EvaluateListFragment();
        Bundle args = new Bundle();
        args.putBoolean("rental",rental);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity()!=null)
            activity = (MainActivity) getActivity();
        realm = Realm.getDefaultInstance();
        if (getArguments()!=null){
            rental = getArguments().getBoolean("rental");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_rental_list, container, false);

        if (getActivity()!=null)    activity = (MainActivity) getActivity();

        ButterKnife.bind(this, v);

        setUpRecyclerView();

        return v;
    }


    private void setUpRecyclerView(){

        RealmResults query =  ((rental)?realm.where(RealmProperty.class).equalTo("property_type_id",1):realm.where(RealmProperty.class).equalTo("property_type_id",2)).findAll();

        propertyRecyclerViewAdapter =
                new PropertyRecyclerViewAdapter(query , this);
        recyclerView.setAdapter(propertyRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        recyclerView.setAdapter(null);
        realm.close();
    }

    @Override
    public void itemClicked(Object data) {
        if (getActivity()!=null){
        Intent i = new Intent(getActivity(), DetailActivity.class);
        i.putExtra("id",(String )data);
        startActivity(i);
        }
    }

    @Override
    public void itemClicked2(Object data) {

    }

    @Override
    public void itemClicked3(Object data) {

    }

    public class PropertyRecyclerViewAdapter extends RealmRecyclerViewAdapter<RealmProperty,PropertyRecyclerViewAdapter.PropertyViewHolder> {


        private ViewHolderClickInterface<String> itemClick;

        public PropertyRecyclerViewAdapter(OrderedRealmCollection<RealmProperty> properties, ViewHolderClickInterface callback) {
            super(properties,true);

            this.itemClick = callback;
            setHasStableIds(true);
        }

        @Override
        public PropertyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_property_evaluate, parent, false);
            ButterKnife.bind(this, v);
            PropertyViewHolder holder = new PropertyViewHolder(v, itemClick);


            return holder;
        }

        @Override
        public void onBindViewHolder(final PropertyViewHolder holder, int position) {

            RealmProperty item = getItem(position);

            String city = item.getCity();
            String state =  item.getState();
            String zip = item.getZip_code();
            String street = item.getStreet();

            int px = (int) Convertions.convertDpToPixel(75, getActivity());

            holder.setProperty(item);
            //if (item.getPrice()!=null) holder.getTv_price().setText("$"+String.valueOf(item.getPrice()));
            holder.getTv_state_city().setText(city+", "+state);
            holder.getTv_zip_street().setText(zip+" "+street);

            Log.d("img: ",item.getPhotos().first().getImage());

            Picasso.with(getContext()).setIndicatorsEnabled(true);
            Picasso.with(getContext())
                    .load( Uri.parse(item.getPhotos().first().getImage()))
                    .resize(px,px)
                    .centerCrop()
                    .into(holder.getIv_property_image());


        }


        public class PropertyViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.cardView) CardView cv;
            @BindView(R.id.tv_price)TextView tv_price;
            @BindView(R.id.tv_zip_street)TextView tv_zip_street;
            @BindView(R.id.tv_state_city)TextView tv_state_city;
            @BindView(R.id.iv_property_image)ImageView iv_property_image;

            private ViewHolderClickInterface<String> callback;
            private RealmProperty property;

            @OnClick(R.id.cardView)
            public void click(View view){
                callback.itemClicked(property.getId());
            }

            public PropertyViewHolder(View itemView, ViewHolderClickInterface callback) {
                super(itemView);
                this.callback = callback;
                ButterKnife.bind(this, itemView);
            }

            public TextView getTv_price() {
                return tv_price;
            }

            public void setTv_price(TextView tv_price) {
                this.tv_price = tv_price;
            }

            public ImageView getIv_property_image() {
                return iv_property_image;
            }

            public void setIv_property_image(ImageView iv_property_image) {
                this.iv_property_image = iv_property_image;
            }

            public RealmProperty getProperty() {
                return property;
            }

            public void setProperty(RealmProperty property) {
                this.property = property;
            }

            public TextView getTv_zip_street() {
                return tv_zip_street;
            }

            public void setTv_zip_street(TextView tv_zip_street) {
                this.tv_zip_street = tv_zip_street;
            }

            public TextView getTv_state_city() {
                return tv_state_city;
            }

            public void setTv_state_city(TextView tv_state_city) {
                this.tv_state_city = tv_state_city;
            }
        }




    }




}
