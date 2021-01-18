package com.antigravitystudios.flppd.models.realm;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Manuel on 6/8/2017.
 */

public class RealmWorksheetFlip extends RealmObject {

    @PrimaryKey
    private String id = UUID.randomUUID().toString();

    private Float purchase_price = Float.valueOf(1000);

    private Float arv = Float.valueOf(0);

    private Float purchase_costs_percent = Float.valueOf(0);

    private Boolean itemized_purchase_costs = false;

    private RealmList<RealmPropertyItemPurchaseCost> items_purchase_costs;

    private Float total_itemized_purchase_costs = Float.valueOf(0);

    @LinkingObjects("realmWorksheetFlip")
    private final RealmResults<RealmProperty> property;


    public RealmWorksheetFlip() {

        property=null;


    }

    public static RealmWorksheetFlip create(){

        RealmWorksheetFlip realmWorksheetFlip = new RealmWorksheetFlip();

        realmWorksheetFlip.items_purchase_costs = new RealmList<>();

        RealmPropertyItemPurchaseCost i1 = new RealmPropertyItemPurchaseCost();
        i1.setName("item1");
        realmWorksheetFlip.items_purchase_costs.add(i1);

        RealmPropertyItemPurchaseCost i2 = new RealmPropertyItemPurchaseCost();
        i2.setName("item2");
        realmWorksheetFlip.items_purchase_costs.add(i2);

        RealmPropertyItemPurchaseCost i3 = new RealmPropertyItemPurchaseCost();
        i3.setName("item3");
        realmWorksheetFlip.items_purchase_costs.add(i3);

        RealmPropertyItemPurchaseCost i4 = new RealmPropertyItemPurchaseCost();
        i4.setName("item4");
        realmWorksheetFlip.items_purchase_costs.add(i4);

        return realmWorksheetFlip;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getPurchase_price() {
        return purchase_price;
    }

    public void setPurchase_price(Float purchase_price) {
        this.purchase_price = purchase_price;
    }

    public Float getArv() {
        return arv;
    }

    public void setArv(Float arv) {
        this.arv = this.purchase_price + arv;
    }

    public Float getPurchase_costs_percent() {
        return purchase_costs_percent;
    }

    public void setPurchase_costs_percent(Float purchase_costs_percent) {
        this.purchase_costs_percent = purchase_costs_percent;
    }

    public Boolean getItemized_purchase_costs() {
        return itemized_purchase_costs;
    }

    public void setItemized_purchase_costs(Boolean itemized_purchase_costs) {
        this.itemized_purchase_costs = itemized_purchase_costs;
    }

    public RealmList<RealmPropertyItemPurchaseCost> getItems_purchase_costs() {
        return items_purchase_costs;
    }

    public void setItems_purchase_costs(RealmList<RealmPropertyItemPurchaseCost> items_purchase_costs) {
        this.items_purchase_costs = items_purchase_costs;
    }

    public Float getTotal_itemized_purchase_costs() {
        return total_itemized_purchase_costs;
    }

    public void setTotal_itemized_purchase_costs(Float total_itemized_purchase_costs) {
        this.total_itemized_purchase_costs = total_itemized_purchase_costs;
    }

    public RealmResults<RealmProperty> getProperty() {
        return property;
    }



}
