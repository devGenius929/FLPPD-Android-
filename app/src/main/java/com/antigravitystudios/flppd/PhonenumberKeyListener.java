package com.antigravitystudios.flppd;

import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class PhonenumberKeyListener implements View.OnKeyListener {
    @Override
    public boolean onKey(View view, int i, KeyEvent event) {
        EditText editText = (EditText) view;

        boolean t = false;
        if (editText.getText().length() > 1) {
            if ((editText.getText().length() == 3 || editText.getText().length() == 7) && (event.getKeyCode() != KeyEvent.KEYCODE_DEL)) {
                editText.setText(String.format("%s ", editText.getText()));
                editText.setSelection(editText.getText().length());
            } else if (editText.getText().length() == 13) {
                editText.setText(editText.getText().toString().substring(0, editText.getText().length() - 1));
                editText.setSelection(editText.getText().length());
            }
            t = true;
        }
        if (editText.getText().toString().length() < 12) t = false;
        return t;
    }
}
