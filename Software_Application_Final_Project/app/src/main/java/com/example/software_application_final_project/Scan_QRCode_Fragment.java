package com.example.software_application_final_project;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

public class Scan_QRCode_Fragment extends Fragment {

    /**
     * 在Fragment中，以往在Activity內寫成 this 的部分，皆須使用 getActivity() 取代
     **/

    // 物件變數
    View view;
    SurfaceView surfaceView;
    TextView tvRead;

    // 儲存發票資料之相關變數
    private SQLiteDatabase db;
    private ArrayList<String> list;
    private final String DB_NAME = "MY_RECEIPT";
    private String[] receipt_interval = {"", "01~02月", "01~02月", "03~04月", "03~04月", "05~06月", "05~06月", "07~08月", "07~08月", "09~10月", "09~10月", "11~12月", "11~12月"};

    // 宣告Google相機所需變數
    CameraSource cameraSource;
    BarcodeDetector barcodeDetector;


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        /** Fragment中，須將這個view先抓出來才能使用 view.findViewById **/
        view = inflater.inflate(R.layout.fragment_scan__q_r_code_, container, false);

        surfaceView = view.findViewById(R.id.surfaceView);
        tvRead = view.findViewById(R.id.tvRead);

        myInit();

        // 建立相機物件之builder
        barcodeDetector = new BarcodeDetector.Builder(getActivity()).setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(getActivity(), barcodeDetector).setAutoFocusEnabled(true).build();

        // 將相機放入SurfaceView
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                try {
                    // 檢查權限(cameraSource.start()方法需要，但其實我們已經檢查完了)
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start(surfaceHolder); // 將相機抓到的畫面放入SurfaceView
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                // 關閉相機
                cameraSource.stop();
            }
        });


        // 讀取QR Code
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                SparseArray<Barcode> QRCodes = detections.getDetectedItems();

                if (QRCodes.size() != 0) {
                    tvRead.post(new Runnable() {
                        @Override
                        public void run() {
                            // 取得抓到的完整字串
                            String strGet = QRCodes.valueAt(0).displayValue;
                            if (strGet.charAt(0) != '*' && strGet.length() > 2) {

                                // 分離出發票號碼(第0~9個字元)
                                String receipt_num = strGet.substring(0, 2) + "-" + strGet.substring(2, 10);
                                // 分離出發票年度(第10~12個字元)
                                String receipt_year = strGet.substring(10, 13);
                                // 分離出發票日期(第13~16個字元)
                                String receipt_month = strGet.substring(13, 15);
                                String recipt_day = strGet.substring(15, 17);


                                tvRead.setText("發票號碼: " + receipt_num + "\n" + "發票日期: " + receipt_month + "/" + recipt_day);

                                // 將資料寫入資料庫中
                                try {
                                    db.execSQL("INSERT INTO " + DB_NAME + " VALUES(?,?,?,?,?)",
                                            new String[]{receipt_year, receipt_month, recipt_day, receipt_num, receipt_year+"年"+receipt_interval[Integer.parseInt(receipt_month)]});
                                }catch (Exception e){

                                }

                            }

                        }
                    });
                }
            }
        });

        return view;
    }

    private void myInit() {
        // 初始化db
        db = new myDBHelper(getActivity()).getWritableDatabase();
    }
}