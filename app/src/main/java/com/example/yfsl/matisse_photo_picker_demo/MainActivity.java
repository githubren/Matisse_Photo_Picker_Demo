package com.example.yfsl.matisse_photo_picker_demo;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private TextView text;
    private ImageView imageView;
    private RxPermissions rxPermissions;
    private List<Uri> uris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btn);
        text = findViewById(R.id.textview);
        imageView = findViewById(R.id.imageView);

        rxPermissions = new RxPermissions(this);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //动态申请权限
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA)
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(Boolean aBoolean) {
                                if (aBoolean) {
                                    Matisse.from(MainActivity.this)
                                            .choose(MimeType.allOf())
                                            .countable(false)
                                            .maxSelectable(9)
                                            .capture(true)
                                            .captureStrategy(new CaptureStrategy(true,"com.example.yfsl.matisse_photo_picker_demo.fileprovider"))
                                            .imageEngine(new GlideEngine())
                                            .forResult(111);
                                }else {
                                    Toast.makeText(MainActivity.this,"没有权限！",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 111) {
            List<Uri> uriList = Matisse.obtainResult(data);
            uris.addAll(uriList);
            text.setText(uris.toString());
            for (Uri uri : uris){
                Glide.with(this)
                        .load(uri)
                        .override(500,500)
                        .into(imageView);
            }
        }
    }
}
