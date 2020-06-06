package com.bilalahmad.twilio.mvp;

import org.json.JSONObject;

public interface CallbackNetwork {

    void startLoading();

    void onProgress(final int total, final int downloaded);

    void onError(final String title, final String error);

    void onResponse(final JSONObject response);

}

