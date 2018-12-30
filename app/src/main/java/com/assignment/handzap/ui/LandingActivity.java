package com.assignment.handzap.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.assignment.handzap.R;
import com.assignment.handzap.databinding.ActivityLandingBinding;

public class LandingActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLandingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_landing);
        binding.btnAddNewPost.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddNewPost:
                startActivity(new Intent(getApplicationContext(), NewPostActivity.class));
                break;
        }
    }
}
