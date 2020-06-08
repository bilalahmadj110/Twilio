package com.bilalahmad.twilio.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bilalahmad.twilio.Globals;
import com.bilalahmad.twilio.R;
import com.bilalahmad.twilio.mvp.CallbackNetwork;
import com.bilalahmad.twilio.mvp.NetworkingRouter;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bilalahmad.twilio.Globals.RESPONSE_ERROR;
import static com.bilalahmad.twilio.Globals.RESPONSE_ERROR_TITLE;

public class CallFragment extends Fragment {
    private View view;
    private ScrollView scroll;
    private LinearLayout numberContainer;
    private ImageButton send;
    private EditText speechMsg;

    private ProgressDialog progress = null;
    private ArrayList<View> viewArray = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_call, container, false);

        init();
        addPhoneRow();

        return view;
    }

    private void init() {
        scroll = view.findViewById(R.id.scroller_call);
        numberContainer = view.findViewById(R.id.number_container);
        speechMsg = view.findViewById(R.id.sms);
        send = view.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMS();
            }
        });
    }

    public void showSnack(final String text) {
        if (getActivity() == null)
            return;
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content),
                text, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void addPhoneRow() {
        if (viewArray.size() >= Globals.MAX_ROWS) {
            showSnack(getString(R.string.max_phone_error, Globals.MAX_ROWS));
            return;
        }
        final View child = LayoutInflater.from(getContext()).inflate(R.layout.call_row, null);
        EditText phoneTxt = child.findViewById(R.id.editTextTextPersonName);
        final ImageButton addBtn = child.findViewById(R.id.imageButton);
        final ImageButton removeBtn = child.findViewById(R.id.imageButton2);
        ((TextView) child.findViewById(R.id.row_no)).setText(String.valueOf(viewArray.size() + 1));

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhoneRow();
                addBtn.setVisibility(View.GONE);
                removeBtn.setVisibility(View.VISIBLE);
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewArray.size() > 1) { // At-least one phone number should be there...
                    numberContainer.removeView(child);
                    viewArray.remove(child);
                    // In-case removed view is the last view, then we need to show add button for second last view
                    viewArray.get(viewArray.size() - 1).findViewById(R.id.imageButton2).setVisibility(View.GONE);
                    viewArray.get(viewArray.size() - 1).findViewById(R.id.imageButton).setVisibility(View.VISIBLE);

                    // Resseting row number for all phone numbers
                    for (int i = 0; i < viewArray.size(); i++) {
                        ((TextView) viewArray.get(i).findViewById(R.id.row_no)).setText(String.valueOf(i + 1));
                    }
                }
            }
        });

        viewArray.add(child);
        numberContainer.addView(child);

        scroll.postDelayed(new Runnable() {
            @Override
            public void run() {
                scroll.smoothScrollTo(0, scroll.getHeight());
            }
        }, 1000);
    }

    private void sendSMS() {
        if (!validatePhoneNumbers()) {
            return;
        }
        String url = "";
        try {
            url = String.format(Locale.getDefault(),
                    Globals.SEND_DATA,
                    URLEncoder.encode(Globals.FROM_NUMBER, "UTF-8"), // from
                    getPhones(), // to numbers list
                    URLEncoder.encode(speechMsg.getText().toString(), "UTF-8") // msg
            );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        NetworkingRouter networkingRouter = new NetworkingRouter(url);
        networkingRouter.registerCallbackNetwork(new CallbackNetwork() {
            @Override
            public void startLoading() {
                showProgress("Logging numbers to server for calling, please wait...");
            }

            @Override
            public void onError(String title, String error) {
                cancelProgress();
                showDialog(title, error);
            }

            @Override
            public void onResponse(JSONObject response) {
                cancelProgress();
                try {
                    JSONArray array = response.getJSONArray("ids");
                    int failed = 0;
                    int success = 0;
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject = array.getJSONObject(i);
                        if (!jsonObject.getString("sid").equals("FAILED")) success++;
                        else failed++;
                    }
                    showDialog("Logged successfully",
                            "Congralutions, total " + array.length() + " number(s) have been added for calling." +
                                    "\nSuccess: " + success +
                                    "\nFailed: " + failed);
                } catch (JSONException e) {
                    showDialog(RESPONSE_ERROR_TITLE, RESPONSE_ERROR);
                }
            }
        });
        networkingRouter.startNetworking();
    }

    private boolean validatePhoneNumbers() {
        boolean validate = true;
        for (View row : viewArray) {
            EditText phoneTxt = row.findViewById(R.id.editTextTextPersonName);
            String p = phoneTxt.getText().toString();
            if (!(p.contains("+") && p.length() > 12)) {
                validate = false;
                phoneTxt.setError("Invalid phone #");
                break;
            }

        }
        String speech = speechMsg.getText().toString();
        if (speech.trim().length() == 0) {
            speechMsg.setError("Speech message required");
        }
        return validate && speech.trim().length() != 0;
    }

    private String getPhones() {
        List<String> list = new ArrayList<>();
        for (View row : viewArray) {
            EditText phoneTxt = row.findViewById(R.id.editTextTextPersonName);
            String p = phoneTxt.getText().toString();
            try {
                list.add(URLEncoder.encode(p, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        JSONArray array = new JSONArray(list);
        return array.toString();
    }

    private void showProgress(String display) {
        progress = new ProgressDialog(getContext());
        progress.setTitle(null);
        progress.setMessage(display);
        progress.setCancelable(false);
        progress.show();
    }

    private void cancelProgress() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
            progress.cancel();
        }
    }

    private void showDialog(String title, String given) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title)
                .setMessage(given)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
}