package com.connester.job.activity.businesspage;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.connester.job.R;

public class ManageMyPageActivity extends AppCompatActivity {
    String business_page_id = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_my_page);

        if (getIntent() != null) {
            business_page_id = getIntent().getStringExtra("business_page_id");
        } else {
            Toast.makeText(this, "Id not found! Please go back and try again", Toast.LENGTH_LONG).show();
        }
    }
}