package com.antigravitystudios.flppd;

import android.app.Activity;
import android.content.DialogInterface;
import android.databinding.ObservableBoolean;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponse;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchasesResult;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.antigravitystudios.flppd.events.PurchaseCancelEvent;
import com.antigravitystudios.flppd.events.PurchaseUpdateEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static com.android.billingclient.api.BillingClient.SkuType.INAPP;
import static com.android.billingclient.api.BillingClient.SkuType.SUBS;

public class BillingManager implements PurchasesUpdatedListener {

    private static BillingManager instance;

    private BillingClient client;
    private EventBus bus = EventBus.getDefault();

    public static ObservableBoolean isSubscriptionActive = new ObservableBoolean(false);
    public static ObservableBoolean isPlatinumListingPurchased = new ObservableBoolean(false);

    private String platinumToken;

    public static void init() {
        instance = new BillingManager();
    }

    private BillingManager() {
        bus.register(this);
        client = BillingClient.newBuilder(MyApplication.getInstance()).setListener(this).build();
        client.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingResponse.OK) {
                    PurchasesResult result = client.queryPurchases(SUBS);
                    if (result.getPurchasesList().isEmpty()) {
                        isSubscriptionActive.set(false);
                    } else {
                        for (Purchase purchase : result.getPurchasesList()) {
                            if (purchase.getSku().equals("investor_pro9.99_per_month"))
                                isSubscriptionActive.set(true);
                        }
                    }
                    result = client.queryPurchases(INAPP);
                    if (result.getPurchasesList().isEmpty()) {
                        isPlatinumListingPurchased.set(false);
                    } else {
                        for (Purchase purchase : result.getPurchasesList()) {
                            if (purchase.getSku().equals("platinum_25")) {
                                isPlatinumListingPurchased.set(true);
                                platinumToken = purchase.getPurchaseToken();
                            }
                        }
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    public static BillingManager getInstance() {
        return instance;
    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        if (responseCode == BillingResponse.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                bus.post(new PurchaseUpdateEvent(purchase));
            }
        } else {
            bus.post(new PurchaseCancelEvent());
        }
    }

    public static void launchSubFlow(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Investor Pro Membership")
                .setMessage("Full Access to Property Details and ability to generate reports and projections")
                .setPositiveButton("Subscribe", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        BillingFlowParams.Builder builder = BillingFlowParams.newBuilder().setSku("investor_pro9.99_per_month").setType(SUBS);
                        instance.client.launchBillingFlow(activity, builder.build());
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    public static void launchPlatinumFlow(Activity activity) {
        BillingFlowParams.Builder builder = BillingFlowParams.newBuilder().setSku("platinum_25").setType(INAPP);
        instance.client.launchBillingFlow(activity, builder.build());
    }

    public static void setPlatinumToken(String token) {
        instance.platinumToken = token;
    }

    public static void consumePlatinum() {
        instance.client.consumeAsync(instance.platinumToken, new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(int responseCode, String purchaseToken) {
                if (responseCode == BillingResponse.OK) {
                    instance.platinumToken = null;
                    BillingManager.getInstance().isPlatinumListingPurchased.set(false);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPurchaseUpdateResult(PurchaseUpdateEvent event) {
        if (event.getPurchase().getSku().equals("investor_pro9.99_per_month")) {
            isSubscriptionActive.set(true);
        }
    }
}
