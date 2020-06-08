package com.bilalahmad.twilio.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bilalahmad.twilio.Globals;
import com.bilalahmad.twilio.Model;
import com.bilalahmad.twilio.R;
import com.bilalahmad.twilio.mvp.CallbackRetry;

import java.util.List;

public class ResponseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater inflater;
    private List<Model> model;
    private CallbackRetry callbackRetry;

    public ResponseAdapter(Context context, List<Model> model) {
        mContext = context;
        this.model = model;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType > 0) {
            View view = inflater.inflate(R.layout.response_item, parent, false);
            return new ResponseViewHolder(view);
        } else if (viewType == 0) { // loading layout
            View view = inflater.inflate(R.layout.load_more, parent, false);
            return new ResponseAdapter.LoadingHolder(view);
        } else { // info layout -- usually -1
            View view = inflater.inflate(R.layout.load_more, parent, false);
            return new ResponseAdapter.InfoHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ResponseViewHolder) {
            populateItems((ResponseViewHolder) holder, position);
        } else if (holder instanceof ResponseAdapter.LoadingHolder) {
            showLoading((ResponseAdapter.LoadingHolder) holder);
        } else if (holder instanceof ResponseAdapter.InfoHolder) {
            showError((ResponseAdapter.InfoHolder) holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return model.get(position).getId();
    }

    private void showLoading(ResponseAdapter.LoadingHolder viewHolder) {
        ((TextView) viewHolder.loadingLayout.getChildAt(1)).setText(mContext.getString(R.string.loading_more));
    }

    private void showError(ResponseAdapter.InfoHolder holder, int position) {
        String msg;
        switch (model.get(position).getId()) {
            case -1: //  no internet
                msg = Globals.INTERNET_ERROR_TITLE;
                break;
            case -2: // no content available
                msg = "No more responses available!";
                break;
            default:
                msg = "Unknown error occur, try again";
                break;
        }

        ((TextView) holder.infoLayout.getChildAt(0)).setText(msg);
        holder.infoLayout.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callbackRetry != null)
                    callbackRetry.click();
            }
        });
    }

    private void populateItems(final ResponseViewHolder holder, final int position) {
        holder.fromNum.setText(model.get(position).getSender());
        holder.toNum.setText(model.get(position).getReceiver());

        if (model.get(position).getStatus().toUpperCase().equals("PENDING")) {
            holder.status.setTextColor(mContext.getResources().getColor(android.R.color.holo_blue_dark));
        } else if (model.get(position).getStatus().toUpperCase().equals("FAILED")) {
            holder.status.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
        } else if (model.get(position).getStatus().toUpperCase().equals("COMPLETED")) {
            holder.status.setTextColor(mContext.getResources().getColor(android.R.color.holo_green_dark));
        }
        holder.status.setText(model.get(position).getStatus());
        holder.responseMsg.setText(model.get(position).getResponseMsg().trim().equals("") ? "n/a" : model.get(position).getResponseMsg().trim());
    }

    public void setCallback(CallbackRetry retry) {
        this.callbackRetry = retry;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return model.size();
    }

    public static class ResponseViewHolder extends RecyclerView.ViewHolder {
        private TextView fromNum, toNum, responseMsg, status;

        ResponseViewHolder(@NonNull View view) {
            super(view);
            fromNum = view.findViewById(R.id.textView6);
            toNum = view.findViewById(R.id.textView7);
            responseMsg = view.findViewById(R.id.textView8);
            status = view.findViewById(R.id.textView);
        }
    }

    public static class LoadingHolder extends RecyclerView.ViewHolder {
        public LinearLayout loadingLayout;

        public LoadingHolder(@NonNull View view) {
            super(view);
            loadingLayout = view.findViewById(R.id.load_load_layout);
            view.findViewById(R.id.load_info_layout).setVisibility(View.GONE);
        }
    }

    public static class InfoHolder extends RecyclerView.ViewHolder {
        public LinearLayout infoLayout;

        public InfoHolder(@NonNull View view) {
            super(view);
            infoLayout = view.findViewById(R.id.load_info_layout);
            view.findViewById(R.id.load_load_layout).setVisibility(View.GONE);
        }
    }

}

