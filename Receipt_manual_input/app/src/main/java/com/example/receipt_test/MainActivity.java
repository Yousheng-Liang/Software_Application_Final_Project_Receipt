package com.example.receipt_test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    // 物件變數宣告
    EditText boxYear, boxDate, boxNumber;
    Button btnWrite, btnRead, btnClear, btnCheckPrize;
    ListView myList;

    // 資料庫相關變數
    private final String DB_NAME = "MY_RECEIPT";
    private SQLiteDatabase db;

    // 儲存發票相關變數
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    private String receipt_year;
    private String receipt_date;
    private String receipt_number;
    private String[] receipt_interval = {"", "1~2月", "1~2月", "3~4月", "3~4月", "5~6月", "5~6月", "7~8月", "7~8月", "9~10月", "9~10月", "11~12月", "11~12月"};

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();  // 關閉資料庫
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewByIds();  // 連結物件變數
        Init();  // 初始化

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 清除table所有資料，但保留table本身
                db.execSQL("DELETE FROM " + DB_NAME);
            }
        });

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    receipt_year = boxYear.getText().toString();
                    receipt_date = boxDate.getText().toString();
                    receipt_number = boxNumber.getText().toString();

                    String[] date = receipt_date.split("/");


                    // 把發票資料寫入資料庫
                    db.execSQL("INSERT INTO " + DB_NAME + "(Receipt_Year, Receipt_Month, Receipt_Day, Receipt_Number, Receipt_Interval) VALUES(?,?,?,?,?)",
                            new String[]{receipt_year, date[0], date[1], receipt_number, receipt_year+"年"+receipt_interval[Integer.parseInt(date[0])]});
                    Toast.makeText(getApplication(), "發票資料已寫入", Toast.LENGTH_SHORT).show();

                }catch (Exception e){
                    Toast.makeText(getApplication(), "新增資料時發生了錯誤", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 讀取資料庫
                Cursor c = db.rawQuery("SELECT * FROM " + DB_NAME, null);
                // 把指標移動到第一筆資料
                c.moveToFirst();
                // 清空arrayList
                list.clear();
                Toast.makeText(getApplication(), "共有" + c.getCount()+"筆資料", Toast.LENGTH_SHORT).show();
                // 逐筆資料讀取並放入arrayList中
                for(int i=0; i<c.getCount(); i++){
                    list.add("發票年度：" + c.getString(0) + "\t\t\t發票日期：" + c.getString(1) + "月" + c.getString(2) + "日" + "\n發票號碼：" + c.getString(3) + "\n開獎區間：" + c.getString(4));
                    c.moveToNext();
                }
                // 告知adapter內容更新
                adapter.notifyDataSetChanged();
                // 關閉Cursor
                c.close();

                Cursor c2 = db.rawQuery("SELECT Count(Distinct Receipt_Interval) FROM " + DB_NAME + " order by Receipt_Interval", null);
                c2.moveToFirst();
                for(int i=0; i<c2.getCount(); i++){
                    Toast.makeText(getApplication(), c2.getString(0), Toast.LENGTH_SHORT).show();
                    c2.moveToNext();
                }
            }
        });

        btnCheckPrize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), checkPrizeActivity.class);
                startActivity(intent);
            }
        });

    }


    private void Init(){
        // 變數初始化
        list = new ArrayList<>();
        // 設定adapter及listview
        adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, list);
        myList.setAdapter(adapter);
        // 資料庫初始化
        db = new myDBHelper(getApplication()).getWritableDatabase();
    }

    private void findViewByIds(){
        boxYear = findViewById(R.id.boxYear);
        boxDate = findViewById(R.id.boxDate);
        boxNumber = findViewById(R.id.boxNumber);
        btnClear = findViewById(R.id.btnClear);
        btnWrite = findViewById(R.id.btnWrite);
        btnRead = findViewById(R.id.btnRead);
        myList = findViewById(R.id.myList);
        btnCheckPrize = findViewById(R.id.btnCheckPrize);
    }
}