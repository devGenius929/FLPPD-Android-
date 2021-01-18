package com.antigravitystudios.flppd.ui.Evaluate.NewFlip;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.antigravitystudios.flppd.R;
import com.antigravitystudios.flppd.models.realm.RealmProperty;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondStepNewFlipFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondStepNewFlipFragment extends Fragment {
    private SecondStepNewFlipFragment.OnListPropertyListener mListener;
    private RealmProperty property;

    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.et_price)
    EditText et_price;

    @BindView(R.id.et_arv)
    EditText et_arv;

    @BindView(R.id.et_rehab_cost)
    EditText et_rehab_cost;

    @BindView(R.id.bt_send)
    Button bt_send;

    @OnClick(R.id.bt_send)
    public void send(){
        if (mListener!=null){
            if (validateFields()){
                property = new RealmProperty();
               // property.setPrice(Float.parseFloat(et_price.getText().toString()));
                property.setProperty_type_id(spinner.getSelectedItemPosition()+1);
               // property.setArv(Float.parseFloat(et_arv.getText().toString()));
                mListener.onListPropertyClick(property);
            }

        }
    }

    public SecondStepNewFlipFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SecondStepNewFlipFragment newInstance() {
        SecondStepNewFlipFragment fragment = new SecondStepNewFlipFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_second_step_new_flip, container, false);

        ButterKnife.bind(this,v);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.deal_types, R.layout.spinner_layout);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        return v;
    }

    private boolean validateFields(){

        boolean validated = true;



        if (TextUtils.isEmpty(et_price.getText())){
            et_price.setError("Can't be empty");
            et_price.requestFocus();
            validated = false;
        }

        if (TextUtils.isEmpty(et_arv.getText())){
            et_arv.setError("Can't be empty");
            et_arv.requestFocus();
            validated = false;
        }

        if (TextUtils.isEmpty(et_rehab_cost.getText())){
            et_rehab_cost.setError("Can't be empty");
            et_rehab_cost.requestFocus();
            validated = false;
        }

        return validated;

    }

    private void toast(String msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SecondStepNewFlipFragment.OnListPropertyListener) {
            mListener = (SecondStepNewFlipFragment.OnListPropertyListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNextButtonCLickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnListPropertyListener {
        void onListPropertyClick(RealmProperty property);
    }

}
