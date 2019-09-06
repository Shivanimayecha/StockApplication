package com.hd.nature.stockapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.hd.nature.stockapplication.MainActivity;
import com.hd.nature.stockapplication.R;
import com.hd.nature.stockapplication.comman.NetworkConnection;
import com.hd.nature.stockapplication.data_model.Details;
import com.hd.nature.stockapplication.retrofit.ApiService;
import com.hd.nature.stockapplication.retrofit.RetroClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PastPerformanceActivity extends AppCompatActivity {

    Activity activity;
    private String TAG = "TAG";
    ArrayList<Details> arrayList = new ArrayList<>();
    RecyclerView recyclerView;
    Detailsadapter detailsadapter;
    //DrawerLayout drawerLayout;
    Toolbar toolbar2;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_performance);
        activity = PastPerformanceActivity.this;
        findViews();
        initViews();

    }

    private void findViews() {
        recyclerView = findViewById(R.id.recyclerview_pp);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        toolbar2 = findViewById(R.id.toolbar);
        back = findViewById(R.id.img_back);
    }

    private void initViews() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (NetworkConnection.isNetworkAvailable(activity)) {

            try {
                ApiService api = RetroClient.getApiService();
                Call<String> call = api.getDetails();
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        Log.e(TAG, "onResponse: " + response.body());

                        if (response.body() != null) {

                            parseResponse(response.body(), "");

                        } else {
                            //showSomethingwentWrong();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        //hideProgressbar();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(activity, "Please check your network connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void parseResponse(String body, String s) {

        try {

            JSONArray jsonArray = new JSONArray(body);
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonobject = (JSONObject) jsonArray.get(i);
                Details details = new Details();
                details.setId(jsonobject.optString("id"));
                details.setTitle(jsonobject.optString("title"));
                details.setBuysell(jsonobject.optString("buysell"));
                details.setPrice(jsonobject.optString("exe_price"));
                details.setT1(jsonobject.optString("t1"));
                details.setT2(jsonobject.optString("t2"));
                details.setT3(jsonobject.optString("t3"));
                details.setSl(jsonobject.optString("sl"));
                details.setTarget1(jsonobject.optString("target_1"));
                details.setTarget2(jsonobject.optString("target_2"));
                details.setTarget3(jsonobject.optString("target_3"));
                details.setStoploss(jsonobject.optString("stoploss"));
                details.setStatusMsg(jsonobject.getString("status_message"));
                details.setCreatedAt(jsonobject.getString("created_at").substring(0, 10));

                String date1 = jsonobject.getString("created_at").substring(0, 10);

                String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                if (!date1.equals(date)) {

                    Log.e(TAG, "parseResponse: " + date1 + "----" + date);
                    arrayList.add(details);

                }

            }
            detailsadapter = new Detailsadapter(PastPerformanceActivity.this, arrayList);
            recyclerView.setAdapter(detailsadapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    class Detailsadapter extends RecyclerView.Adapter<Detailsadapter.ViewHolder> {

        private Context context;
        ArrayList<Details> detailsArrayList = new ArrayList<>();

        public Detailsadapter(Context context, ArrayList<Details> detailsArrayList) {
            this.context = context;
            this.detailsArrayList = detailsArrayList;
        }

        @Override
        public Detailsadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.details_item, parent, false);
            return new Detailsadapter.ViewHolder(view);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView title, buysell, price, t1, t2, t3, stoploss, status, abovebellow, dateD;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.txt_title);
                buysell = itemView.findViewById(R.id.txt_buy_sell);
                abovebellow = itemView.findViewById(R.id.txt_above_below);
                price = itemView.findViewById(R.id.txt_price);
                t1 = itemView.findViewById(R.id.txt_t1);
                t2 = itemView.findViewById(R.id.txt_t2);
                t3 = itemView.findViewById(R.id.txt_t3);
                stoploss = itemView.findViewById(R.id.txt_stoploss);
                status = itemView.findViewById(R.id.txt_status);
                dateD = itemView.findViewById(R.id.txt_date);
            }
        }


        @Override
        public void onBindViewHolder(Detailsadapter.ViewHolder holder, int position) {

            holder.title.setText(detailsArrayList.get(position).getTitle());

            String bs = detailsArrayList.get(position).getBuysell();
            if (bs.equals("1")) {
                holder.buysell.setText("BUY");
                holder.abovebellow.setText("Above");
                holder.buysell.setTextColor(Color.parseColor("#47B604"));
            } else if (bs.equals("-1")) {
                holder.buysell.setText("Sell");
                holder.abovebellow.setText("Below");
                holder.buysell.setTextColor(Color.parseColor("#FB1222"));

            }

            holder.price.setText(detailsArrayList.get(position).getPrice());

            String trgt1 = detailsArrayList.get(position).getT1();
            String trgt2 = detailsArrayList.get(position).getT2();
            String trgt3 = detailsArrayList.get(position).getT3();

            Log.e(TAG, "onBindViewHolder: " + trgt1);
            Log.e(TAG, "onBindViewHolder: " + trgt2);
            Log.e(TAG, "onBindViewHolder: " + trgt3);


            holder.t1.setText(detailsArrayList.get(position).getTarget1());
            holder.t2.setText(detailsArrayList.get(position).getTarget2());
            holder.t3.setText(detailsArrayList.get(position).getTarget3());

            if (trgt1.equals("1")) {
                holder.t1.setBackgroundColor(Color.parseColor("#4DA201"));
                holder.t1.setTextColor(Color.parseColor("#FEFFFF"));
            } else if (trgt1.equals("0")) {
                holder.t1.setBackgroundColor(Color.parseColor("#FEFFFF"));
                holder.t1.setTextColor(Color.parseColor("#010000"));
            }
            if (trgt2.equals("1")) {
                holder.t2.setBackgroundColor(Color.parseColor("#4DA201"));
                holder.t2.setTextColor(Color.parseColor("#FEFFFF"));
            } else if (trgt2.equals("0")) {
                holder.t2.setBackgroundColor(Color.parseColor("#FEFFFF"));
                holder.t2.setTextColor(Color.parseColor("#010000"));
            }
            if (trgt3.equals("1")) {

                holder.t3.setBackgroundColor(Color.parseColor("#4DA201"));
                holder.t3.setTextColor(Color.parseColor("#FEFFFF"));
            } else if (trgt3.equals("0")) {
                holder.t3.setBackgroundColor(Color.parseColor("#FEFFFF"));
                holder.t3.setTextColor(Color.parseColor("#010000"));
            }

         /*   if(trgt1.equals("1"))
            {
                Log.e(TAG, "if: " );
                holder.t1.setBackgroundColor(Color.parseColor("#4DA201"));
                holder.t1.setTextColor(Color.parseColor("#FEFFFF"));
            }
            else if(trgt2.equals("1") || trgt3.equals("1"))
            {
                Log.e(TAG, " else if 1: " );
                holder.t2.setBackgroundColor(Color.parseColor("#4DA201"));
                holder.t2.setTextColor(Color.parseColor("#FEFFFF"));
                holder.t3.setBackgroundColor(Color.parseColor("#4DA201"));
                holder.t3.setTextColor(Color.parseColor("#FEFFFF"));
            }*/
           /* else if(trgt3.equals("1"))
            {
                Log.e(TAG, "else if 2: " );
                holder.t3.setBackgroundColor(Color.parseColor("#4DA201"));
                holder.t3.setTextColor(Color.parseColor("#FEFFFF"));

            }*/

           /* else {
                holder.t1.setBackgroundColor(Color.parseColor("#FEFFFF"));
                holder.t2.setBackgroundColor(Color.parseColor("#FEFFFF"));
                holder.t3.setBackgroundColor(Color.parseColor("#FEFFFF"));
            }*/
         /*   if(trgt1.equals("1") || trgt2.equals("1") ||trgt3.equals("1"))
            {
                holder.t1.setBackgroundColor(Color.parseColor("#4DA201"));
                holder.t2.setBackgroundColor(Color.parseColor("#4DA201"));
                holder.t3.setBackgroundColor(Color.parseColor("#4DA201"));

                holder.t1.setTextColor(Color.parseColor("#FEFFFF"));
                holder.t2.setTextColor(Color.parseColor("#FEFFFF"));
                holder.t3.setTextColor(Color.parseColor("#FEFFFF"));

            }
            else if(trgt1.equals("0") || trgt2.equals("0") || trgt3.equals("0"))
            {

                holder.t1.setBackgroundColor(Color.parseColor("#FEFFFF"));
                holder.t2.setBackgroundColor(Color.parseColor("#FEFFFF"));
                holder.t3.setBackgroundColor(Color.parseColor("#FEFFFF"));

                holder.t1.setTextColor(Color.parseColor("#010000"));
                holder.t2.setTextColor(Color.parseColor("#010000"));
                holder.t3.setTextColor(Color.parseColor("#010000"));
            }*/

            String sl = detailsArrayList.get(position).getSl();
            holder.stoploss.setText(detailsArrayList.get(position).getStoploss());

            if (sl.equals("1")) {

                holder.stoploss.setBackgroundColor(Color.parseColor("#FF0E02"));
                holder.stoploss.setTextColor(Color.parseColor("#FEFFFF"));
            } else if (sl.equals("0")) {

                holder.stoploss.setBackgroundColor(Color.parseColor("#FEFFFF"));
                holder.stoploss.setTextColor(Color.parseColor("#010000"));
            }

            holder.status.setText(detailsArrayList.get(position).getStatusMsg());
            holder.dateD.setText(detailsArrayList.get(position).getCreatedAt());

        }

        @Override
        public int getItemCount() {
            return detailsArrayList.size();
        }

    }

}
