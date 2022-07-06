package com.example.download;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class DownLoadProgressDialog extends Dialog {
    ProgressBar progressBar;
    TextView textView;
    int max;
    public DownLoadProgressDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_download);
        initViews();
    }

    private void initViews() {
        progressBar = findViewById(R.id.download_progress);
        textView = findViewById(R.id.textview);
    }

    public void setProgress(int progress){
        progressBar.setProgress(progress);
        float ple = (float)progress/(float)max;
        textView.setText(String.format("%.0f", ple * 100) + "%");
    }
    public void setMax(int max){
        this.max = max;
        progressBar.setMax(max);
    }
}
