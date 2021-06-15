package com.example.download;

import android.os.Environment;
import android.util.Log;

import com.tencent.mmkv.MMKV;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownLoader {
    private boolean ispause;
    private boolean iscancel;
    private Listener mlistener;
    File file;
    File destfile;
    public void download(final String fileurl, final Listener listener){
        try {
            ispause= false;
            mlistener = listener;
            file = new File(Environment.getExternalStorageDirectory(), "MMKV_cache");
            destfile = new File(Environment.getExternalStorageDirectory(),fileurl.substring(fileurl.lastIndexOf("/")+1));
            MMKV.initialize(file.getAbsolutePath());
            final MMKV mmkv = MMKV.defaultMMKV();
            final RandomAccessFile randomAccessFile = new RandomAccessFile(destfile,"rwd");
            final int currentlength = mmkv.getInt("current",0);
            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().header("RANGE","bytes="+currentlength+"-").url(fileurl).build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    long filetotalsize = response.body().contentLength()+currentlength;
                    mlistener.start((int) filetotalsize);
                    mlistener.progress(currentlength);
                    randomAccessFile.setLength(filetotalsize);
                    randomAccessFile.seek(currentlength);
                    InputStream is = response.body().byteStream();
                    byte[] buf = new byte[1024];
                    int readlength=0;
                    int currentreadlength=currentlength;
                    while ((readlength=is.read(buf))!=-1){
                        if(iscancel){
                            mlistener.cancel();
                            closeResources(mmkv,randomAccessFile,is,response.body());
                            return;
                        }
                        if(ispause){
                            mmkv.encode("current",currentreadlength);
                            mlistener.pause(currentreadlength);
                            closeResources(mmkv,randomAccessFile,is,response.body());
                            return;
                        }
                        randomAccessFile.write(buf,0,readlength);
                        currentreadlength+=readlength;
                        listener.progress(currentreadlength);
                        randomAccessFile.seek(currentreadlength);

                    }
                    closeResources(mmkv,randomAccessFile,is,response.body());
                    closeFiles(file);
                    mlistener.finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeResources(MMKV mmkv,Closeable...closeables) {
        try {
            mmkv.close();
            int length = closeables.length;
            for(int i=0;i<length;i++){
                closeables[i].close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeFiles(File... files) {
        try {
            int length = files.length;
            for(int i=0;i<length;i++){
                if(files[i].isDirectory()){
                    for(File f:files[i].listFiles()){
                        f.delete();
                    }
                    files[i].delete();
                }else {
                    files[i].delete();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void pause(){
        ispause = true;
    }
    public void cancel(){
        iscancel =true;
        mlistener.cancel();
        closeFiles(file,destfile);
    }
    public interface Listener{
        void start(int totalsize);
        void progress(int progress);
        void pause(int progress);
        void cancel();
        void finish();
    }
}
