package com.example.download;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SecActivity extends AppCompatActivity {
    DownLoadProgressDialog dialog;
    DownLoader downLoader;
    String path = "https://pm.myapp.com/invc/xfspeed/qqpcmgr/download/SougoPinyin_PCDownload1100112706.exe";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sec);
        dialog = new DownLoadProgressDialog(this);
        downLoader = new DownLoader();
        TextView textView = findViewById(R.id.tv);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                downLoader.download(path, new DownLoader.Listener() {
                    @Override
                    public void start(int totalsize) {
                        dialog.setMax(totalsize);
                    }

                    @Override
                    public void progress(int progress) {
                        dialog.setProgress(progress);
                    }

                    @Override
                    public void pause(int progress) {

                    }

                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void finish() {
                        dialog.dismiss();
                    }
                });
            }
        });
    }
}