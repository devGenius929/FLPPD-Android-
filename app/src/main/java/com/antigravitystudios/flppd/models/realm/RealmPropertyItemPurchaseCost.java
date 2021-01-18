package com.antigravitystudios.flppd.models.realm;

import java.util.UUID;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Manuel on 6/8/2017.
 */

public class RealmPropertyItemPurchaseCost extends RealmObject{

    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    private String name = "";
    private String hint = "";
    private Float value = Float.valueOf(0);
    private Float total;

    private boolean set_amount = true; //False  for percent
    private boolean percent_of_price = true; //False  for percent of loan
    private boolean pay_upfront = true; //False for Wrap into Loan

    @LinkingObjects("items_purchase_costs")
    private final RealmResults<RealmWorksheetFlip> realmWorksheetFlips;

    public RealmPropertyItemPurchaseCost() {
        realmWorksheetFlips=null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public boolean isSet_amount() {
        return set_amount;
    }

    public void setSet_amount(boolean set_amount) {
        this.set_amount = set_amount;
    }

    public boolean isPercent_of_price() {
        return percent_of_price;
    }

    public void setPercent_of_price(boolean percent_of_price) {
        this.percent_of_price = percent_of_price;
    }

    public boolean isPay_upfront() {
        return pay_upfront;
    }

    public void setPay_upfront(boolean pay_upfront) {
        this.pay_upfront = pay_upfront;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RealmResults<RealmWorksheetFlip> getRealmWorksheetFlips() {
        return realmWorksheetFlips;
    }

    public Float getTotal() {
        return total;
    }

    public void updateTotal() {

        if (set_amount){
            this.total = value;
        } else {
            if (percent_of_price){
                this.total = (value / 100) * (getRealmWorksheetFlips().get(0).getPurchase_price());
            } else {
               //Set percent of loan
                this.total = Float.valueOf(0);
            }
        }

    }


}
