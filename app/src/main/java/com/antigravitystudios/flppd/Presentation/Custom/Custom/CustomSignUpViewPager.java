package com.antigravitystudios.flppd.Presentation.Custom.Custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Manuel Salas on 11/3/2017.
 */

public class CustomSignUpViewPager extends ViewPager {

        private boolean isPagingEnabled = true;

        public CustomSignUpViewPager(Context context) {
            super(context);
        }

        public CustomSignUpViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            return false;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            return false;
        }

        public void setPagingEnabled(boolean b) {
            this.isPagingEnabled = b;
        }

}
