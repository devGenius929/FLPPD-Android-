package com.antigravitystudios.flppd.ui.base;


/**
 * Interface representing a Presenter in a model view presenter (MVP) pattern.
 */

public interface BasePresenter {

    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onResume() method.
     */

    void onResume();

    /**
     * Method that control the lifecycle of the view. It should be called in the view's
     * (Activity or Fragment) onPause() method.
     */
    void onPause();


}