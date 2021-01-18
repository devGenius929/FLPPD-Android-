package com.antigravitystudios.flppd.ui.Evaluate.NewFlip;


import com.antigravitystudios.flppd.models.realm.RealmProperty;
import com.antigravitystudios.flppd.ui.base.BasePresenter;
import com.antigravitystudios.flppd.ui.base.BaseView;

public interface NewFlipContract {

    interface View extends BaseView {
        void onSuccess(int code, Object response);
        void onError(int code, String message);
    }

    interface Presenter extends BasePresenter {

        void storeProperty(RealmProperty property);

    }

}
