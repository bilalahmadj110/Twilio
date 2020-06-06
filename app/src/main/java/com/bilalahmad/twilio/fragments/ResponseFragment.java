package com.bilalahmad.twilio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bilalahmad.twilio.R;

public class ResponseFragment extends Fragment {
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout infoLayout, loadingLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_response, container, false);

        return view;
    }

    private void init() {
        swipeRefreshLayout = view.findViewById(R.id.swiper);
        recyclerView = view.findViewById(R.id.response_holder);
        infoLayout = view.findViewById(R.id.info_layout);
        loadingLayout = view.findViewById(R.id.loading_layout);
    }

    private synchronized void getResponse() {
        // Request to Server to get response
    }
}