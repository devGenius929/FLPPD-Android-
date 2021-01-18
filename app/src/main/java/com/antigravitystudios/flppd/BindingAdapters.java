package com.antigravitystudios.flppd;

import android.databinding.BindingAdapter;
import android.databinding.InverseBindingAdapter;
import android.databinding.InverseBindingListener;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

public class BindingAdapters {

    @BindingAdapter(value = {"selectedValueIndex", "selectedValueIndexAttrChanged"}, requireAll = false)
    public static void bindSpinnerDataIndex(Spinner spinner, Integer selectedValueIndex, final InverseBindingListener selectedValueIndexAttrChanged) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedValueIndexAttrChanged.onChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (selectedValueIndex != null) {
            spinner.setSelection(selectedValueIndex, true);
        } else {
            spinner.setSelection(0, true);
        }
    }

    @InverseBindingAdapter(attribute = "selectedValueIndex")
    public static Integer captureSelectedValueIndex(Spinner spinner) {
        return spinner.getSelectedItemPosition();
    }

    @BindingAdapter(value = {"selectedValue", "selectedValueAttrChanged"}, requireAll = false)
    public static void bindSpinnerData(Spinner spinner, String selectedValue, final InverseBindingListener selectedValueAttrChanged) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedValueAttrChanged.onChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (selectedValue != null) {
            int pos = ((ArrayAdapter<String>) spinner.getAdapter()).getPosition(selectedValue);
            spinner.setSelection(pos, true);
        } else {
            spinner.setSelection(0, true);
        }
    }

    @InverseBindingAdapter(attribute = "selectedValue")
    public static String captureSelectedValue(Spinner spinner) {
        return (String) spinner.getSelectedItem();
    }

    @BindingAdapter("error")
    public static void bindError(TextInputLayout view, String message) {
        view.setError(message);
    }

    @BindingAdapter("error")
    public static void bindSpinnerError(Spinner view, String message) {
        TextView errorText = (TextView) view.getSelectedView();
        if (message != null && message.isEmpty())
            errorText.setError(null);
        else
            errorText.setError(message);
    }

    @BindingAdapter("pagerAdapter")
    public static void bindPagerAdapter(ViewPager view, PagerAdapter adapter) {
        view.setAdapter(adapter);
    }

    @BindingAdapter("adapter")
    public static void bindRecyclerViewAdapter(RecyclerView view, RecyclerView.Adapter adapter) {
        view.setAdapter(adapter);
    }

    @BindingAdapter("pagerListener")
    public static void bindPagerListener(ViewPager view, ViewPager.OnPageChangeListener listener) {
        view.addOnPageChangeListener(listener);
    }

    @BindingAdapter("keyListener")
    public static void bindKeyListener(EditText view, View.OnKeyListener listener) {
        view.setOnKeyListener(listener);
    }

    @BindingAdapter(value = {"imageUrl", "imageScale", "imageTransform"}, requireAll = false)
    public static void bindImage(ImageView view, String imageUrl, String imageScale, String imageTransform) {

        if (imageUrl == null || imageUrl.trim().isEmpty()) return;

        RequestCreator requestCreator = Picasso.with(view.getContext()).load(imageUrl).fit();

        if (imageScale != null) {
            switch (imageScale) {
                case "centerCrop":
                    requestCreator.centerCrop();
                    break;
                case "centerInside":
                    requestCreator.centerInside();
                    break;
            }
        }

        if (imageTransform != null) {
            switch (imageTransform) {
                case "circle":
                    requestCreator.transform(new CircleTransform());
                    break;
            }
        }

        requestCreator.into(view);
    }

}
