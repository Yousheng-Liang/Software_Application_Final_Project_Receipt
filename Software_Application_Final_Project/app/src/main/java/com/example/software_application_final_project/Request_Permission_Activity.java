package com.example.software_application_final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Request_Permission_Activity extends AppCompatActivity {

    // 變數宣告
    private final int CAMERA_PERMISSION = 13;

    Button btnCameraPermission;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_PERMISSION:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "已順利取得相機權限", Toast.LENGTH_SHORT).show();
                    btnCameraPermission.setEnabled(false);
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permission);

        btnCameraPermission = findViewById(R.id.btnRequestCameraPermission);

        Bundle bundle = getIntent().getExtras();

        // 若無按鈕對應的權限，則啟用該按鈕
        try {
            btnCameraPermission.setEnabled(bundle.getBoolean("camera"));
            btnCameraPermission.setText(bundle.getBoolean("camera")?"取得相機權限":"已取得相機權限");
        }catch (Exception e){
            e.printStackTrace();
        }

        btnCameraPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCameraPermission(Request_Permission_Activity.this);
            }
        });


    }

    private void checkCameraPermission(Activity activity){
        // 如果沒有相機權限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            // 要求使用相機權限
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        }
    }
}