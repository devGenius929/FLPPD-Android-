package com.antigravitystudios.flppd;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class LandscapeRelativeLayout extends RelativeLayout {

    public LandscapeRelativeLayout(Context context) {
        super(context);
    }

    public LandscapeRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = Math.round(width /16 * 9);
        int finalWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        int finalHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(finalWidthMeasureSpec, finalHeightMeasureSpec);
    }

}