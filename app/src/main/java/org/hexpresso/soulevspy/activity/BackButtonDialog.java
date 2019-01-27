package org.hexpresso.soulevspy.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.hexpresso.soulevspy.R;

public class BackButtonDialog extends DialogFragment {
    AppCompatActivity activity;
    public int choice = 0;

    public BackButtonDialog(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        choice = 0;
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);//getActivity());
        builder.setMessage(R.string.dialog_back_button_message)
                .setPositiveButton(R.string.dialog_back_button_terminate, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Shutdown app
                        choice = 1;
                        ((AppCompatActivity)activity).onBackPressed();
                    }
                })
                .setNegativeButton(R.string.dialog_back_button_background, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Continue in background
                        choice = 2;
                        ((AppCompatActivity)activity).onBackPressed();
                    }
                })
                .setNeutralButton(R.string.dialog_back_button_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Continue as we were
                        choice = 3;
                        ((AppCompatActivity)activity).onBackPressed();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

