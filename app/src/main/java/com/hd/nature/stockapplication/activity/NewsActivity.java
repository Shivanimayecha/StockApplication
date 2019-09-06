package com.hd.nature.stockapplication.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hd.nature.stockapplication.R;
import com.hd.nature.stockapplication.comman.NetworkConnection;
import com.hd.nature.stockapplication.data_model.News;
import com.hd.nature.stockapplication.retrofit.ApiService;
import com.hd.nature.stockapplication.retrofit.RetroClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Activity activity;
    ArrayList<News> newsArrayList = new ArrayList<>();
    NewsAdapter newsAdapter;
    private String TAG = "TAG";
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        activity = NewsActivity.this;

        findViews();
        initViews();
    }

    private void findViews() {

        recyclerView = findViewById(R.id.recyclerview_news);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        imageView = findViewById(R.id.img_back_news);
    }

    private void initViews() {

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });

        if (NetworkConnection.isNetworkAvailable(activity)) {

            try {

                ApiService api = RetroClient.getApiService();
                Call<String> call = api.getNews();
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if (response.body() != null) {

                            Log.e(TAG, "onResponse: " + response.body());

                            callNewsApi(response.body(), "");
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                    }
                });

            } catch (Exception e) {

            }
        } else {
            Toast.makeText(activity, "Please check your network connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void callNewsApi(String body, String s) {

        try {
            JSONArray jsonArray = new JSONArray(body);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                News news = new News();
                news.setNews_id(jsonObject.optString("news_id"));
                news.setDate(jsonObject.optString("date"));
                news.setTitle(jsonObject.optString("title"));
                news.setDescription(jsonObject.optString("description"));
                newsArrayList.add(news);
            }

            newsAdapter = new NewsAdapter(NewsActivity.this, newsArrayList);
            recyclerView.setAdapter(newsAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

        private Context context;
        ArrayList<News> newsArrayList = new ArrayList<>();

        public NewsAdapter(Context context, ArrayList<News> newsArrayList) {
            this.context = context;
            this.newsArrayList = newsArrayList;
        }

        @Override
        public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.news_item, parent, false);
            return new NewsAdapter.ViewHolder(view);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView textView;
            RelativeLayout relativeLayout;

            public ViewHolder(View itemView) {
                super(itemView);

                textView = itemView.findViewById(R.id.txt_news_title);
                relativeLayout = itemView.findViewById(R.id.rl);
            }
        }

        @Override
        public void onBindViewHolder(NewsAdapter.ViewHolder holder, final int position) {

            holder.textView.setText(newsArrayList.get(position).getTitle());

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, NewsDescription_Activity.class);
                    intent.putExtra("title", newsArrayList.get(position).getTitle());
                    intent.putExtra("desc", newsArrayList.get(position).getDescription());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return newsArrayList.size();
        }
    }
}
