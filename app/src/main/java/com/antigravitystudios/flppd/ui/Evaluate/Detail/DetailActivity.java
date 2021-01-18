package com.antigravitystudios.flppd.ui.Evaluate.Detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.antigravitystudios.flppd.Presentation.Utils.Convertions;
import com.antigravitystudios.flppd.ui.Evaluate.EditProperty.EditFlipActivity;
import com.antigravitystudios.flppd.ui.Evaluate.Itemize.PurchaseCost.ItemizePurchaseCostActivity;
import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.models.realm.RealmProperty;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.iv_image)
    ImageView iv_image;

    @BindView(R.id.tv_zip_street)
    TextView tv_zip_street;

    @BindView(R.id.tv_price)
    TextView tv_price;

    @BindView(R.id.tv_state_city)
    TextView tv_state_city;

    @BindView(R.id.layout_edit)
    RelativeLayout layout_edit;

    @BindView(R.id.layout_worksheet)
    LinearLayout layout_worksheet;

    @OnClick(R.id.layout_worksheet)
    public void onClickWorkSheet(View view){
        Intent intent = new Intent(DetailActivity.this, ItemizePurchaseCostActivity.class);
        intent.putExtra("id", property.getId());
        startActivity(intent);
    }

    @OnClick(R.id.layout_edit)
    public void click (View view){
        Intent intent = new Intent(DetailActivity.this, EditFlipActivity.class);
        intent.putExtra("id", property.getId());
        startActivity(intent);
    }


    private Realm realm;
    private  RealmProperty property;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_evaluate);
        realm = Realm.getDefaultInstance();
        property =  realm.where(RealmProperty.class).equalTo("id",getIntent().getStringExtra("id")).findFirst();

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle((property.getProperty_type_id()==1)?"Rental":"Flip");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String city = property.getCity();
        String state =  property.getState();
        String zip = property.getZip_code();
        String street = property.getStreet();

        //if (property.get()!=null) tv_price.setText("$"+String.valueOf(property.getPrice()));
        tv_state_city.setText(city+", "+state);
        tv_zip_street.setText(zip+" "+street);

        Log.d("imag", String.valueOf(Uri.parse(property.getPhotos().first().getImage())));

        int px = (int) Convertions.convertDpToPixel(250,this);
        Picasso.with(this).setIndicatorsEnabled(true);
        Picasso.with(this)
                .load( Uri.parse(property.getPhotos().first().getImage()))
                .resize(px,px)
                .centerCrop()
                .into(iv_image);

    }

    private void delete(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                property.deleteFromRealm();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.menu_delete:
                delete();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.flip_detail_menu, menu);
        return true;
    }
}
