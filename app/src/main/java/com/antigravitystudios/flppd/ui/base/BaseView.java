package com.antigravitystudios.flppd.ui.base;

import android.content.Context;

/**
 * Created by Manuel Salas on 5/4/2017.
 */

public interface BaseView {

    /**
     * Show a view with a progress bar indicating a loading process.
     */
    void showLoading();

    /**
     * Hide a loading view.
     */
    void hideLoading();

    /**
     * Show an error message
     *
     * @param message A string representing an error.
     */
    void showError(String message);

    /**
     * Show an error message
     *
     * @param message A string representing an error.
     */
    void showToast(String message);


    /**
     * Get a {@link android.content.Context}.
     */
    Context getContext();

}
