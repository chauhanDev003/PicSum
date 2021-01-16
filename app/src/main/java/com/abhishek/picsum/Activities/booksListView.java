package com.abhishek.picsum.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abhishek.picsum.Adapters.RecyclerViewAdapter;
import com.abhishek.picsum.Models.RecyclerViewModel;
import com.abhishek.picsum.R;
import com.abhishek.picsum.Utils.CustomTypefaceSpan;
import com.abhishek.picsum.Utils.IsLoading;
import com.abhishek.picsum.Utils.RequestHandler;
import com.abhishek.picsum.Utils.Url;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class booksListView extends AppCompatActivity {

    private static final String TAG = "bookListView";
    final IsLoading isLoading = new IsLoading();
    boolean doubleBackToExitPressedOnce = false;
    private Context context = booksListView.this;
    //widgets
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private ProgressBar isLoadingPB;
    //List
    private List<RecyclerViewModel> recyclerViewModelList = new ArrayList<>();
    //var
    private int pageNumber = 1;
    private int offset = 10;
    private boolean hasMore = false;
    private boolean firstTime = true;
    private int itemCount;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list_view);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            itemCount = 3;
        } else {
            itemCount = 2;
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark

        //ProgressDialog while loading from server
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        //text font set
        Typeface font = ResourcesCompat.getFont(context, R.font.montserrat_bold);
        SpannableStringBuilder spannableSB = new SpannableStringBuilder(getString(R.string.text_please_wait));
        spannableSB.setSpan(new CustomTypefaceSpan("font/montserrat_bold.ttf", font), 0, spannableSB.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        progressDialog.setMessage(spannableSB);
        progressDialog.setCancelable(false);
        progressDialog.show();

        booksListFetch();

    }

    private void booksListFetch() {
        recyclerView = findViewById(R.id.recyclerView);
        isLoadingPB = findViewById(R.id.isLoadingPB);

        isLoading.setListener(new IsLoading.OnLoadingListener() {
            @Override
            public void onChange() {
                if (isLoading.isLoading()) {
                    Log.d(TAG, "onChange: is loading");
                    ViewGroup.MarginLayoutParams marginLayoutParams =
                            (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
                    marginLayoutParams.setMargins(0, 0, 0, dpToPx(100));
                    recyclerView.setLayoutParams(marginLayoutParams);
                    isLoadingPB.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "onChange: not loading");
                    ViewGroup.MarginLayoutParams marginLayoutParams =
                            (ViewGroup.MarginLayoutParams) recyclerView.getLayoutParams();
                    marginLayoutParams.setMargins(0, 0, 0, dpToPx(0));
                    isLoadingPB.setVisibility(View.GONE);
                    recyclerView.setLayoutParams(marginLayoutParams);
                }
            }
        });
        getList(pageNumber, offset);
    }

    private void getList(final int pageNumber, final int offset) {

        //API call
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Url.baseURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                        if (isLoading.isLoading()) isLoading.setLoading(false);
                        Log.d(TAG, "onResponse: response = " + response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                String id = object.getString("id");
                                String author = object.getString("author");

                                RecyclerViewModel recyclerViewModel = new RecyclerViewModel(id, author);
                                recyclerViewModelList.add(recyclerViewModel);
                            }

                            if (firstTime) setUpRecyclerView();
                        } catch (JSONException e) {
                            Log.e(TAG, "onResponse: json error", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (progressDialog.isShowing()) progressDialog.dismiss();
                        if (isLoading.isLoading()) isLoading.setLoading(false);
                        Log.d(TAG, "onErrorResponse: Volley Error = " + error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("page_num", String.valueOf(pageNumber));
                params.put("offset", String.valueOf(offset));
                return params;
            }
        };
        RequestHandler.getInstance(context).addToRequestQueue(stringRequest);
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(context, itemCount));

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(recyclerViewModelList, context);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    Log.d(TAG, "onScrollStateChanged: last");
                    if (hasMore) {
                        isLoading.setLoading(true);
                        pageNumber += 1;
                        getList(pageNumber, offset);
                    } else {
                        if (isLoading.isLoading()) isLoading.setLoading(false);
                    }
                }
            }
        });

        firstTime = false;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        //according to recycleView is re arranged
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            itemCount = 3;
            setUpRecyclerView();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            itemCount = 2;
            setUpRecyclerView();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}