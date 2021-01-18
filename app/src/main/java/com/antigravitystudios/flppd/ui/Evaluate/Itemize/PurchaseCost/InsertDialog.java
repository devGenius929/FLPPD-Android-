package com.antigravitystudios.flppd.ui.Evaluate.Itemize.PurchaseCost;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.antigravitystudios.flppd.models.realm.RealmProperty;
import com.antigravitystudios.flppd.models.realm.RealmPropertyItemPurchaseCost;
import com.antigravitystudios.flppd.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by Manuel on 10/8/2017.
 */

public class InsertDialog extends DialogFragment {

    private boolean[] mat = new boolean[]  {true,true,true};

    private  Realm realm;

    private String id;

    @BindView(R.id.et_name)
    TextInputEditText et_name;

    @BindView(R.id.et_value)
    TextInputEditText et_value;

    @BindView(R.id.bt_set_amount)
    Button bt_set_amount;

    @BindView(R.id.bt_percent)
    Button bt_percent;

    @BindView(R.id.bt_percent_of_price)
    Button bt_percent_of_price;

    @BindView(R.id.bt_percent_of_loan)
    Button bt_percent_of_loan;

    @BindView(R.id.bt_pay_upfront)
    Button bt_pay_upfront;

    @BindView(R.id.bt_wrap_into_loan)
    Button bt_wrap_into_loan;



    @BindView(R.id.layout_percent)
    LinearLayout layout_percent;

    @OnClick(R.id.bt_set_amount)
    public void clickAmount(){
        mat[0] = true;
        selectButton(bt_set_amount);
        unselectButton(bt_percent);
        layout_percent.setVisibility(View.GONE);
    }

    @OnClick(R.id.bt_percent)
    public void clickPercent(){
        mat[0] = false;
        selectButton(bt_percent);
        unselectButton(bt_set_amount);
        layout_percent.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.bt_percent_of_price)
    public void clickPercentOfPrice(){
        mat[1] = true;
        selectButton(bt_percent_of_price);
        unselectButton(bt_percent_of_loan);
    }

    @OnClick(R.id.bt_percent_of_loan)
    public void clickPercentOfLoan(){
        mat[1] = false;
        selectButton(bt_percent_of_loan);
        unselectButton(bt_percent_of_price);
    }

    @OnClick(R.id.bt_pay_upfront)
    public void clickPayUpfront(){
        mat[2] = true;
        selectButton(bt_pay_upfront);
        unselectButton(bt_wrap_into_loan);
    }

    @OnClick(R.id.bt_wrap_into_loan)
    public void clickWrapIntoLoan(){
        mat[2] = false;
        selectButton(bt_wrap_into_loan);
        unselectButton(bt_pay_upfront);
    }



    public InsertDialog(){

    }

    public static InsertDialog newInstance(String id){
        InsertDialog frag = new InsertDialog();
        Bundle args = new Bundle();
        args.putString("id", id);
        frag.setArguments(args);
        return frag;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater i = getActivity().getLayoutInflater();
        View v = i.inflate(R.layout.add_purchase_cost,null);

        id = getArguments().getString("id");

        ButterKnife.bind(this,v);

        realm = Realm.getDefaultInstance();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle("Add Purchase Cost");
        alertDialogBuilder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                save();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }

        });

        alertDialogBuilder.setView(v);

        return alertDialogBuilder.create();

    }

    private void selectButton(Button button){
        button.setBackgroundColor(Color.parseColor("#b8b08d"));
        button.setTextColor(Color.parseColor("#FFFFFF"));
    }

    private void unselectButton(Button button){
        button.setBackgroundColor(Color.parseColor("#00ffffff"));
        button.setTextColor(Color.parseColor("#7f7f7f"));
    }

    private void save(){

        realm.beginTransaction();

        RealmProperty property = realm.where(RealmProperty.class).contains("id",id).findFirst();
        RealmPropertyItemPurchaseCost item = new RealmPropertyItemPurchaseCost();
        item.setName(et_name.getText().toString());
        item.setValue(Float.valueOf(et_value.getText().toString()));
        item.setSet_amount(mat[0]);
        item.setPercent_of_price(mat[1]);
        item.setPay_upfront(mat[2]);
        property.getRealmWorksheetFlip().getItems_purchase_costs().add(item);
        realm.commitTransaction();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }
}