package com.antigravitystudios.flppd.events;

import com.android.billingclient.api.Purchase;

public class PurchaseUpdateEvent {

    private Purchase purchase;

    public PurchaseUpdateEvent(Purchase purchase) {
        this.purchase = purchase;
    }

    public Purchase getPurchase() {
        return purchase;
    }
}
