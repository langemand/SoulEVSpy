package com.evranger.soulevspy.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.evranger.soulevspy.R;

public class BackButtonDialog extends DialogFragment {
    private int choice = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        choice = 0;
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_back_button_message)
                .setPositiveButton(R.string.dialog_back_button_terminate, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Shutdown app
                        choice = 1;
                        ((AppCompatActivity)getActivity()).onBackPressed();
                    }
                })
                .setNegativeButton(R.string.dialog_back_button_background, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Continue in background
                        choice = 2;
                        ((AppCompatActivity)getActivity()).onBackPressed();
                    }
                })
//                .setNeutralButton(R.string.dialog_back_button_cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // Continue as we were
//                        choice = 3;
//                        ((AppCompatActivity)getActivity()).onBackPressed();
//                    }
//                })
        ;
        // Create the AlertDialog object and return it
        return builder.create();
    }

    public int getChoice() {
        return choice;
    }
}

