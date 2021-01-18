package com.antigravitystudios.flppd.models.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Manuel on 6/8/2017.
 */

public class RealmWorksheetRental extends RealmObject {

    private Float purchase_price = Float.valueOf(0);

    private Float arv = Float.valueOf(0);

    private Float purchase_costs_percent = Float.valueOf(0);
    private Boolean itemized_purchase_costs = false;
    private RealmList<RealmPropertyItemPurchaseCost> items_purchase_costs;


}
