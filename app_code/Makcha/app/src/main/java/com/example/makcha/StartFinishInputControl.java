package com.example.makcha;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

public class StartFinishInputControl {
    protected void swap_starting_and_ending(AutoCompleteTextView startingPointView, AutoCompleteTextView finishPointView)
    {
        Editable temp;
        temp = startingPointView.getText();
        startingPointView.setText(finishPointView.getText());
        finishPointView.setText(temp);
    }
}
