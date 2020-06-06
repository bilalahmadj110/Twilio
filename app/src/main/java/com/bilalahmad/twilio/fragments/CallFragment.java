package com.bilalahmad.twilio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.bilalahmad.twilio.R;

public class CallFragment extends Fragment {
    private View view;
    private LinearLayout numberContainer;
    private ImageButton send;
    private EditText speechMsg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_call, container, false);

        return view;
    }

    private void init() {
        numberContainer = view.findViewById(R.id.number_container);
        speechMsg = view.findViewById(R.id.sms);
        send = view.findViewById(R.id.send);
    }

    private void sendSMS() {
    }
}