package com.bilalahmad.twilio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bilalahmad.twilio.Globals;
import com.bilalahmad.twilio.MLog;
import com.bilalahmad.twilio.Model;
import com.bilalahmad.twilio.R;
import com.bilalahmad.twilio.adapters.ResponseAdapter;
import com.bilalahmad.twilio.mvp.ActionType;
import com.bilalahmad.twilio.mvp.CallbackNetwork;
import com.bilalahmad.twilio.mvp.CallbackRetry;
import com.bilalahmad.twilio.mvp.NetworkingRouter;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class ResponseFragment extends Fragment {
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout infoLayout, loadingLayout;
    private int totalAvailable = 0;
    private boolean LOADING_FLAG = false;

    // Recycler View
    private RecyclerView recyclerView;
    private ArrayList<Model> models = new ArrayList<>();
    private ResponseAdapter responseAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_response, container, false);

        init();

        getResponse(ActionType.RETRY);

        return view;
    }

    private void init() {
        swipeRefreshLayout = view.findViewById(R.id.swiper);
        recyclerView = view.findViewById(R.id.response_holder);
        infoLayout = view.findViewById(R.id.info_layout);
        loadingLayout = view.findViewById(R.id.loading_layout);

        responseAdapter = new ResponseAdapter(getContext(), models);
        recyclerView.setAdapter(responseAdapter);

        responseAdapter.setCallback(new CallbackRetry() {
            @Override
            public void click() {
                getResponse(ActionType.LOAD_MORE);
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getResponse(ActionType.REFRESH);
            }
        });

        infoLayout.getChildAt(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getResponse(ActionType.RETRY);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (!LOADING_FLAG) {
                    if (linearLayoutManager != null && (linearLayoutManager.findLastCompletelyVisibleItemPosition() ==
                            countSize() - 2 || linearLayoutManager.findLastCompletelyVisibleItemPosition() == countSize() - 1)) {
                        if (countSize() < totalAvailable)
                            getResponse(ActionType.LOAD_MORE);
                    }
                }
            }
        });
    }

    public void showSnack(final String text, final CallbackRetry callbackRetry) {
        if (getActivity() == null)
            return;
        Snackbar.make(getActivity().findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG)
                .setAction(getString(R.string.retry_button), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (callbackRetry != null)
                            callbackRetry.click();
                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_green_light))
                .show();
    }

    private int countSize() {
        int size = 0;
        for (Model m : models) {
            if (m.getId() > 0) { // means that its not loading layout/error layout
                size++;
            }
        }
        return size;
    }

    private void addLoading() {
        if (models.size() == 0)
            return;
        Model m = models.get(models.size() - 1);
        if (m.getId() < 1) { // Some layout is added already --  editing existing layout
            models.remove(models.size() - 1);
            m.setId(0);
            models.add(m);
            recyclerView.post(new Runnable() {
                public void run() {
                    responseAdapter.notifyItemChanged(models.size() - 1);
                }
            });
        } else { // adding loading layout
            m = new Model();
            m.setId(0);
            models.add(m);
            recyclerView.post(new Runnable() {
                public void run() {
                    responseAdapter.notifyItemInserted(models.size() - 1);
                }
            });
        }
    }

    private void removeLoading() {
        if (models.size() == 0)
            return;
        Model m = models.get(models.size() - 1);
        MLog.log("Removing loading... " + m.getId());
        if (m.getId() < 1) { // Some loading layout is added already -- DELETE existing layout
            models.remove(models.size() - 1);
            recyclerView.post(new Runnable() {
                public void run() {
                    responseAdapter.notifyItemRemoved(models.size() - 1);
                }
            });
        }
    }

    private void addError(int errorType) {
        if (models.size() == 0)
            return;
        Model m = models.get(models.size() - 1);
        if (m.getId() < 1) { // Some layout is added already -- editing existing layout
            models.remove(models.size() - 1);
            m.setId(errorType);
            models.add(m);
            recyclerView.post(new Runnable() {
                public void run() {
                    responseAdapter.notifyItemChanged(models.size() - 1);
                }
            });
        } else { // adding error layout
            m = new Model();
            m.setId(errorType);
            models.add(m);
            recyclerView.post(new Runnable() {
                public void run() {
                    responseAdapter.notifyItemInserted(models.size() - 1);
                }
            });
        }
    }

    private synchronized void getResponse(final ActionType actionType) {
        // Request to Server to get response
        // This interface would automatically hide/show loading/error/data, would download only few data, would load more on demand like in
        // facebook, as we scrolled download its loads more data ad keep loading util data reaches it limit
        int AvailableSize = countSize();
        final NetworkingRouter networkingRouter = new NetworkingRouter(
                String.format(Locale.getDefault(),
                        Globals.GET_RESPONSE,
                        actionType == ActionType.REFRESH || actionType == ActionType.RETRY ? 0 : AvailableSize,
                        actionType == ActionType.REFRESH ? AvailableSize :
                                (actionType == ActionType.RETRY ? Globals.ITEMS_PER_PAGE :
                                        (AvailableSize + Globals.ITEMS_PER_PAGE))),
                loadingLayout,
                infoLayout,
                swipeRefreshLayout
        );
        networkingRouter.registerCallbackNetwork(new CallbackNetwork() {

            @Override
            public void startLoading() {
                LOADING_FLAG = true;
                if (actionType == ActionType.RETRY)
                    networkingRouter.setLoading(getString(R.string.loading_msg));
                else if (actionType == ActionType.REFRESH) {
                    if (!swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(true);
                } else if (actionType == ActionType.LOAD_MORE)
                    addLoading();
            }

            @Override
            public void onError(String title, String error) {
                if (actionType == ActionType.RETRY) {
                    networkingRouter.setErrorInfo(title, error);
                    LOADING_FLAG = false;
                } else if (actionType == ActionType.REFRESH) {
                    if (swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);
                    showSnack(title, new CallbackRetry() {
                        @Override
                        public void click() {
                            getResponse(actionType);
                        }
                    });
                    LOADING_FLAG = false;
                } else if (actionType == ActionType.LOAD_MORE) {
                    LOADING_FLAG = false;
                    addError(title.equals(Globals.INTERNET_ERROR_TITLE) ? -1 : -3);
                }
            }

            @Override
            public void onResponse(JSONObject response) {
                if (actionType == ActionType.RETRY)
                    networkingRouter.setSuccess();
                else if (actionType == ActionType.REFRESH) {
                    if (swipeRefreshLayout.isRefreshing())
                        swipeRefreshLayout.setRefreshing(false);
                } else if (actionType == ActionType.LOAD_MORE) {
                    removeLoading();
                }
                try {
                    totalAvailable = response.getInt("total");
                    JSONArray responseList = response.getJSONArray("results");

                    if (actionType == ActionType.RETRY || actionType == ActionType.REFRESH) {
                        models.clear();
                        if (totalAvailable == 0) {
                            networkingRouter.setErrorInfo(Globals.EMPTY_ERROR_TITLE, Globals.EMPTY_ERROR);
                            return;
                        }
                    } else if (responseList.length() == 0 && actionType == ActionType.LOAD_MORE) {
                        if (totalAvailable == 0) {
                            models.clear();
                            networkingRouter.setErrorInfo(Globals.EMPTY_ERROR_TITLE, Globals.EMPTY_ERROR);
                            return;
                        }
                        addError(-2);
                    }

                    for (int i = 0; i < responseList.length(); i++) {
                        Model model = new Model(responseList.getJSONObject(i));
                        models.add(model);
                    }

                    recyclerView.post(new Runnable() {
                        public void run() {
                            responseAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (NullPointerException | JSONException e) {
                    e.printStackTrace();
                    if (actionType == ActionType.RETRY)
                        networkingRouter.setErrorInfo(Globals.RESPONSE_ERROR_TITLE, Globals.RESPONSE_ERROR);
                    else if (actionType == ActionType.REFRESH) {
                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                        showSnack(Globals.RESPONSE_ERROR_TITLE, new CallbackRetry() {
                            @Override
                            public void click() {
                                getResponse(actionType);
                            }
                        });
                    } else if (actionType == ActionType.LOAD_MORE) {
                        addError(-3);
                    }
                }
                LOADING_FLAG = false;

            }
        });
        networkingRouter.startNetworking();
    }


}