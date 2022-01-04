package com.example.receipt;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView textView3;
    private EditText ed_year, ed_month;
    private Button btn_query;
    String date, year, month, target_url;
    String nm;
    int years, months;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView3 = findViewById(R.id.textView3);
        ed_year = findViewById(R.id.ed_year);
        ed_month = findViewById(R.id.ed_month);
        btn_query = findViewById(R.id.btn_query);

        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ed_year.length()<1 || ed_month.length()<1)
                    Toast.makeText(MainActivity.this, "欄位請勿留空", Toast.LENGTH_SHORT).show();
                else {
                    try {
                        year = ed_year.getText().toString();
                        month = ed_month.getText().toString();
                        years = Integer.parseInt(year);
                        months = Integer.parseInt(month);
                        date = ed_year.getText().toString()+"0"+ed_month.getText().toString();
                    }catch (Exception e) {
                        Toast.makeText(MainActivity.this, "查詢失敗:"+e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
                target_url = "https://www.etax.nat.gov.tw/etw-main/ETW183W2_"+date+"/";
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            Document document = Jsoup.connect(target_url).get();
                            Elements elements = document.select("div[class='col-12 mb-3']");

                            nm = elements.text();

                            if (nm.length() > 48)
                                textView3.setText(String.format(year+"年"+months+"~"+(months+1)+"月中獎發票\n\n"+
                                        "特別獎:\n"+nm.substring(0, 8)+"\n\n特獎:\n"+nm.substring(9, 17)+"\n\n頭獎:\n"+nm.substring(18, 26)
                                        +"\n"+nm.substring(27, 35)+"\n"+nm.substring(36, 44)+"\n\n增開六獎:\n"+nm.substring(45, 48)+","+nm.substring(49, 52)));
                            else
                                textView3.setText(String.format(year+"年"+months+"~"+(months+1)+"月中獎發票\n\n"+
                                        "特別獎:\n"+nm.substring(0, 8)+"\n\n特獎:\n"+nm.substring(9, 17)+"\n\n頭獎:\n"+nm.substring(18, 26)
                                        +"\n"+nm.substring(27, 35)+"\n"+nm.substring(36, 44)+"\n\n增開六獎:\n"+nm.substring(45, 48)));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("LYS", e.toString());
                        }
                    }
                }).start();
                textView3.setText(String.format(year+"年"+months+"~"+(months+1)+"月中獎發票\n\n"+
                        "特別獎:\n\n\n特獎:\n\n\n頭獎:\n\n\n\n\n增開六獎:\n"));
            }
        });
    }
}
