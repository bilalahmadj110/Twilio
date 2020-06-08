package com.bilalahmad.twilio.mvp;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bilalahmad.twilio.Globals;
import com.bilalahmad.twilio.MLog;
import com.bilalahmad.twilio.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class NetworkingRouter {

    private boolean fromUI = true;
    private CallbackNetwork callbackNetwork;
    private String url;
    private LinearLayout loadView;
    private LinearLayout errorView;
    private View container;

    public NetworkingRouter(String url, LinearLayout loadView,
                            LinearLayout errorView, View container) {
        this.url = url;
        this.loadView = loadView;
        this.errorView = errorView;
        this.container = container;
    }

    public NetworkingRouter(String url) {
        fromUI = false;
        this.url = url;
    }

    public void startNetworking() {
        if (callbackNetwork == null) {
            return;
        }
        MLog.log(url + " <> ");

        callbackNetwork.startLoading();

        NetworkClass mainThread = new NetworkClass();
        mainThread.execute();
    }

    public void setErrorInfo(String errorTitle, String errorText) {
        if (!fromUI)
            return;
        if (container.getVisibility() == View.VISIBLE || loadView.getVisibility() == View.INVISIBLE)
            container.setVisibility(View.GONE);
        if (loadView.getVisibility() == View.VISIBLE || loadView.getVisibility() == View.INVISIBLE)
            loadView.setVisibility(View.GONE);
        if (errorView.getVisibility() == View.GONE || loadView.getVisibility() == View.INVISIBLE)
            errorView.setVisibility(View.VISIBLE);
        if (errorText.equals(Globals.INTERNET_ERROR))
            ((ImageView) this.errorView.getChildAt(0)).setImageResource(R.drawable.ic_computer);
        else if (errorText.equals(Globals.EMPTY_ERROR))
            ((ImageView) this.errorView.getChildAt(0)).setImageResource(R.drawable.ic_empty);
        else
            ((ImageView) this.errorView.getChildAt(0)).setImageResource(R.drawable.ic_error_outline_black_24dp);
        ((TextView) this.errorView.getChildAt(1)).setText(errorTitle);
        ((TextView) this.errorView.getChildAt(2)).setText(errorText);
    }

    public void setLoading(String loadingText) {
        if (!fromUI)
            return;
        if (container.getVisibility() == View.VISIBLE || loadView.getVisibility() == View.INVISIBLE)
            container.setVisibility(View.GONE);
        if (errorView.getVisibility() == View.VISIBLE || loadView.getVisibility() == View.INVISIBLE)
            errorView.setVisibility(View.GONE);
        if (loadView.getVisibility() == View.GONE || loadView.getVisibility() == View.INVISIBLE)
            loadView.setVisibility(View.VISIBLE);
        ((TextView) this.loadView.getChildAt(1)).setText(loadingText);
    }

    public void setSuccess() {
        if (!fromUI)
            return;
        if (loadView.getVisibility() == View.VISIBLE || loadView.getVisibility() == View.INVISIBLE)
            loadView.setVisibility(View.GONE);
        if (errorView.getVisibility() == View.VISIBLE || loadView.getVisibility() == View.INVISIBLE)
            errorView.setVisibility(View.GONE);
        if (container.getVisibility() == View.GONE || loadView.getVisibility() == View.INVISIBLE)
            container.setVisibility(View.VISIBLE);
    }

    public void registerCallbackNetwork(CallbackNetwork callbackNetwork) {
        this.callbackNetwork = callbackNetwork;
    }


    @SuppressLint("StaticFieldLeak")
    private class NetworkClass extends AsyncTask<Void, Integer, String> {

        String performGetCall(String requestURL) {
            URL url;
            StringBuilder response = new StringBuilder();
            try {
                url = new URL(requestURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(6000);
//                conn.setDoOutput(true);
                conn.setRequestProperty("Accept", "application/json");
                conn.connect();
                int responseCode = conn.getResponseCode();
                int length = conn.getContentLength();
                int downloaded = 0;
                MLog.log("Response Code:: " + responseCode + ", ResponseMessage:: " + conn.getResponseMessage());
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                        if (length > 0) { // Only if total length is known
                            downloaded += line.length();
                            publishProgress(length, downloaded);
                        }
                    }
                    MLog.log("Response:: " + response);
                } else {
                    response.append(responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (response.toString().equals(""))
                return null;
            return response.toString();
        }

        @Override
        protected String doInBackground(Void... aVoid) {
            int retryCount = 0;
            String response = null;
            while (retryCount < Globals.RETRY_COUNT) {
                try {
                    retryCount++;
                    if (retryCount > 1)
                        MLog.log("Trying Count = " + (retryCount - 1));
                    response = performGetCall(url);
                    if (response == null)
                        throw new Exception();
                    break;
                } catch (Exception e) {
                    response = null;
                    e.printStackTrace();
                    try {
                        // Waiting 400ms before retrying request...
                        Thread.sleep(Globals.TIME_WAIT_REQUEST_FAILED);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            MLog.log("response received: " + response);
            String title = Globals.INTERNET_ERROR_TITLE;
            String msg = Globals.INTERNET_ERROR;

            if (response != null) {
                if (response.contains("code") && response.contains("response")) { // If these keys available, then means ok/readable error
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt("code") == 200) {
                            if (callbackNetwork != null) {
                                callbackNetwork.onResponse(jsonObject);
                                return;
                            }
                        } else {
                            String[] codeNMsg = find(response);
                            title = codeNMsg[0] + " (" + codeNMsg[1].split("\n")[0] + ")";
                            msg = codeNMsg[1].split("\n")[1];
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        title = "Request failed";
                        msg = e.getMessage();
                    }
                } else if (find(response) != null) {
                    String[] codeNMsg = find(response);
                    title = codeNMsg[0] + " (" + codeNMsg[1].split("\n")[0] + ")";
                    msg = codeNMsg[1].split("\n")[1];
                }

            }
            if (callbackNetwork != null)
                callbackNetwork.onError(title, msg);
            super.onPostExecute(response);
        }

        String[] find(String code) {
            for (String[] codeNMsg : Globals.HTTP_CODES) {
                if (code.equals(codeNMsg[0]))
                    return codeNMsg;
            }
            return null;
        }
    }
}
