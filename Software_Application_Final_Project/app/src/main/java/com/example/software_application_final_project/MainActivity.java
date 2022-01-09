package com.example.software_application_final_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    // 變數宣告
    private final int CAMERA_PERMISSION = 13;
    Button btnCameraPermission;
    BottomNavigationView navigationView;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CAMERA_PERMISSION:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                }else{   // 如果權限被拒絕
                    Intent intent = new Intent(MainActivity.this, Request_Permission_Activity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("camera", true);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 檢查相機權限，若無則會一直觸發
        checkCameraPermission(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.myFragmentContainer, new Receipt_Record_Fragment()).commit();

        navigationView = findViewById(R.id.myBottomNavigator);

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selected_Fragement = null;
                switch (item.getItemId()){
                    case R.id.navigation_receipt:
                        selected_Fragement = new Receipt_Record_Fragment();
                        break;
                    case R.id.navigation_scan:
                        selected_Fragement = new Scan_QRCode_Fragment();
                        break;
                    case R.id.navigation_manual_input:
                        selected_Fragement = new Receipt_Manual_Input_Fragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.myFragmentContainer, selected_Fragement).commit();
                return true;
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