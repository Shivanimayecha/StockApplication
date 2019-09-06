package com.hd.nature.stockapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.hd.nature.stockapplication.R;

public class NewsDescription_Activity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_description_);

        textView = findViewById(R.id.txt_desc);

        String description = getIntent().getStringExtra("desc");
        textView.setText(description);
    }
}
