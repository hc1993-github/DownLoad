package com.example.download;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String path;
    Button button;
    Button cancel;
    ProgressBar progressBar;
    TextView textView;
    DownLoader downLoader;
    boolean pause = false;
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        initViews();
        path = editText.getText().toString();
        if(path.equals("")){
            path ="https://pm.myapp.com/invc/xfspeed/qqpcmgr/download/SougoPinyin_PCDownload1100112706.exe";
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(downLoader==null){
                    downLoader = new DownLoader();
                }
                if(pause){
                    downLoader.pause();
                    button.setText("开始下载");
                    pause = false;
                }else {
                    downLoader.download(path, new DownLoader.Listener() {
                        @Override
                        public void start(int totalsize) {
                            progressBar.setMax(totalsize);
                            button.setText("暂停下载");
                            pause = true;
                        }

                        @Override
                        public void progress(int progress) {
                            progressBar.setProgress(progress);
                            float ple = (float)progress/(float) progressBar.getMax();
                            textView.setText(String.format("%.0f", ple * 100) + "%");
                        }

                        @Override
                        public void pause(int progress) {
                            progressBar.setProgress(progress);
                            float ple = (float)progress/(float) progressBar.getMax();
                            textView.setText(String.format("%.0f", ple * 100) + "%");
                        }

                        @Override
                        public void cancel() {
                            progressBar.setProgress(0);
                            textView.setText("0%");
                            button.setText("开始下载");
                            downLoader = null;
                            pause = false;
                        }

                        @Override
                        public void finish() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,"下载完毕",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    });
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downLoader.cancel();
            }
        });
    }
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

        }
        return false;
    }
    private void initViews() {
        button = findViewById(R.id.button);
        cancel = findViewById(R.id.cancel);
        progressBar = findViewById(R.id.progressbar);
        textView = findViewById(R.id.textview);
        editText = findViewById(R.id.edittext);
    }
}