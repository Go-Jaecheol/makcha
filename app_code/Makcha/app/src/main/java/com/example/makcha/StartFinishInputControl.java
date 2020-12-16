package com.example.makcha;

import android.text.Editable;
import android.widget.AutoCompleteTextView;

public class StartFinishInputControl {
    public void swap_starting_and_ending(AutoCompleteTextView startingPointView, AutoCompleteTextView finishPointView)
    {
        Editable temp;
        temp = startingPointView.getText();
        startingPointView.setText(finishPointView.getText());
        finishPointView.setText(temp);
    }
}
