package com.antigravitystudios.flppd.ui.signup;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antigravitystudios.flppd.PhonenumberKeyListener;
import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.SPreferences;
import com.antigravitystudios.flppd.databinding.PagerItemSignup1Binding;
import com.antigravitystudios.flppd.databinding.PagerItemSignup2Binding;
import com.antigravitystudios.flppd.events.SignUpEvent;
import com.antigravitystudios.flppd.models.User;
import com.antigravitystudios.flppd.networking.NetworkModule;
import com.antigravitystudios.flppd.ui.base.BaseActivityViewModel;
import com.antigravitystudios.flppd.ui.main.MainActivity;
import com.antigravitystudios.flppd.ui.WebviewActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SignUpViewModel extends BaseActivityViewModel<SignUpActivity> {

    public ObservableBoolean loading = new ObservableBoolean();
    public ObservableBoolean lastPage = new ObservableBoolean();

    public ObservableField<String> firstName = new ObservableField<>();
    public ObservableField<String> lastName = new ObservableField<>();
    public ObservableField<String> email = new ObservableField<>();
    public ObservableField<String> password = new ObservableField<>();
    public ObservableField<String> password2 = new ObservableField<>();
    public ObservableField<String> phonenumber = new ObservableField<>();

    public ObservableField<String> firstNameError = new ObservableField<>();
    public ObservableField<String> lastNameError = new ObservableField<>();
    public ObservableField<String> emailError = new ObservableField<>();
    public ObservableField<String> passwordError = new ObservableField<>();
    public ObservableField<String> password2Error = new ObservableField<>();
    public ObservableField<String> phonenumberError = new ObservableField<>();

    public PagerAdapter pagerAdapter;
    public ViewPager.OnPageChangeListener pagerListener;
    public View.OnKeyListener phoneKeyListener;

    public ViewPager pager;
    public TabLayout tabLayout;

    public SignUpViewModel(SignUpActivity activity) {
        super(activity);

        pagerAdapter = new SignUpPagerAdapter(activity);
        pagerListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                lastPage.set(position == pagerAdapter.getCount() - 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        phoneKeyListener = new PhonenumberKeyListener();
    }

    @Override
    protected void registerBus(EventBus bus) {
        bus.register(this);
    }

    @Override
    protected void unregisterBus(EventBus bus) {
        bus.unregister(this);
    }

    @Override
    protected boolean onActivityBackPressed() {
        if (pager.getCurrentItem() == 0) {
            return false;
        } else {
            changePage(-1);
            return true;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignUpResult(SignUpEvent event) {
        if (event.isSuccessful()) {
            onSignUpSuccess(event.getResult());
        } else {
            loading.set(false);
            activity.showToast(event.getErrorMessage());
        }
    }

    private void attemptSignUp(String email, String password, String first_name, String last_name, String phone_number) {
        loading.set(true);
        NetworkModule.getInteractor().signup(email, password, first_name, last_name, phone_number);
    }

    public void onNextClick() {

        passwordError.set("");
        password2Error.set("");
        phonenumberError.set("");
        emailError.set("");
        lastNameError.set("");
        firstNameError.set("");

        boolean cancel = false;

        switch (pager.getCurrentItem()) {
            case 0:
                if (!isFirstNameValid(firstName.get())) {
                    firstNameError.set(activity.getString(R.string.invalid_first_name));
                    cancel = true;
                }

                if (!isLastNameValid(lastName.get())) {
                    lastNameError.set(activity.getString(R.string.invalid_last_name));
                    cancel = true;
                }
                break;
            case 1:
                if (!isEmailValid(email.get())) {
                    emailError.set(activity.getString(R.string.invalid_email));
                    cancel = true;
                }

                if (!isPasswordValid(password.get())) {
                    passwordError.set(activity.getString(R.string.invalid_password));
                    cancel = true;
                } else if (!password.get().equals(password2.get())) {
                    password2Error.set(activity.getString(R.string.passwords_are_not_equal));
                    cancel = true;
                }

                if (!isPhonenumberValid(phonenumber.get())) {
                    phonenumberError.set(activity.getString(R.string.invalid_phonenumber));
                    cancel = true;
                }
                break;
        }

        if(cancel) return;

        if (pager.getCurrentItem() == pagerAdapter.getCount() - 1) {
            attemptSignUp(email.get(), password.get(), firstName.get(), lastName.get(), phonenumber.get());
        } else {
            changePage(1);
        }
    }

    private void changePage(int offset) {
        tabLayout.getTabAt(pager.getCurrentItem() + offset).select();
    }

    public void onTermsClick() {
        Intent intent = new Intent(activity, WebviewActivity.class);
        intent.putExtra("title", activity.getString(R.string.terms_and_conditions));
        intent.putExtra("href", activity.getString(R.string.terms_and_conditions_url));
        activity.startActivity(intent);
    }

    public void onSignUpSuccess(User user) {
        SPreferences.saveSession(user.getAuthToken(), user, true);

        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    private boolean isEmailValid(String text) {
        return !TextUtils.isEmpty(text);
    }

    private boolean isPasswordValid(String text) {
        return !TextUtils.isEmpty(text);
    }

    private boolean isFirstNameValid(String text) {
        return !TextUtils.isEmpty(text);
    }

    private boolean isLastNameValid(String text) {
        return !TextUtils.isEmpty(text);
    }

    private boolean isPhonenumberValid(String text) {
        return !TextUtils.isEmpty(text);
    }

    private class SignUpPagerAdapter extends PagerAdapter {

        LayoutInflater inflater;

        SignUpPagerAdapter(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;
            switch (position) {
                case 0:
                    PagerItemSignup1Binding binding1 = PagerItemSignup1Binding.inflate(inflater);
                    binding1.setViewModel(SignUpViewModel.this);
                    view = binding1.getRoot();
                    container.addView(view);
                    break;
                case 1:
                    PagerItemSignup2Binding binding2 = PagerItemSignup2Binding.inflate(inflater);
                    binding2.setViewModel(SignUpViewModel.this);
                    view = binding2.getRoot();
                    container.addView(view);
                    break;
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
