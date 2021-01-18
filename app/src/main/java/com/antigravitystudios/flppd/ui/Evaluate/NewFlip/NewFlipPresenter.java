package com.antigravitystudios.flppd.ui.Evaluate.NewFlip;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.antigravitystudios.flppd.Presentation.Utils.ImageUtils;
import com.antigravitystudios.flppd.models.realm.RealmPhoto;
import com.antigravitystudios.flppd.models.realm.RealmProperty;

import java.io.IOException;

import io.realm.RealmList;

public class NewFlipPresenter implements NewFlipContract.Presenter {

    private NewFlipContract.View mView;
    private static Context context;

    public NewFlipPresenter( NewFlipContract.View mView, Context context) {
        this.mView = mView;
        NewFlipPresenter.context = context;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void storeProperty(RealmProperty property) {

        mView.showLoading();
        RealmList<RealmPhoto> photos = new RealmList<>();
        for (int i = 0; i < property.getPhotos().size(); i++) {
            try {
                String uri = ImageUtils.storeImageInternal(
                        MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(property.getPhotos().get(i).getImage())));
                photos.add(new RealmPhoto(uri));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        property.setPhotos(photos);

        for (int i = 0; i <property.getPhotos().size() ; i++) {
            Log.d("photo ", property.getPhotos().get(i).getImage());
        }

        //mInteractor.storeFlip(property);
    }

/*    @Override
    public void onCompleted(int code, String message) {
        mView.hideLoading();
    }*/
}
