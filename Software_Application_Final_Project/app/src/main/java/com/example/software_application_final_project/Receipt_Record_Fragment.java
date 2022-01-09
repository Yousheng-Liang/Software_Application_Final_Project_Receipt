package com.example.software_application_final_project;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Receipt_Record_Fragment extends Fragment {

    // 物件及介面相關變數
    View view;
    Spinner interval_selector;
    private RecyclerView recyclerView;
    private LinearLayout lay1;
    TextView receipt_amount, money_amount;
    myAdapter adapter;

    // 資料庫相關變數
    private final String DB_NAME = "MY_RECEIPT";
    private SQLiteDatabase db;

    // 宣告要讓spinner顯示選項的物件
    ArrayList<String> selector_list;
    ArrayAdapter selector_adapter;

    // 發票資料相關變數
    ArrayList<String> receipt_list;
    Integer str_money_amount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_receipt__record, container, false);

        findViewByIds();
        myInit();

        try {
            loadDataFromDB(); // 讀取發票資料
            draw_or_not(); // 判斷是否開獎
        } catch (Exception e) {
            Toast.makeText(getActivity(), "載入發票時發生了問題", Toast.LENGTH_SHORT).show();
        }

        interval_selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 將選擇到的區間之所有發票表列出來
                Cursor c = db.rawQuery("SELECT * FROM " + DB_NAME + " WHERE Receipt_Interval = '" + parent.getSelectedItem().toString() + "' ORDER BY Receipt_Month, Cast(strftime('%d', Receipt_Day) as Integer)", null);
                c.moveToFirst();
                adapter = new myAdapter(c);
                recyclerView.setAdapter(adapter);
                receipt_amount.setText("發票張數\n" + c.getCount());
                lay1.setVisibility(View.VISIBLE);
                draw_or_not(); // 判斷是否開獎
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 自動兌獎功能
        money_amount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(money_amount.getText().toString() == "點此兌獎")
                    new Redeem_Task(getActivity(), interval_selector.getSelectedItem().toString()).execute();
            }
        });

        return view;
    }


    private void draw_or_not() {

        // 抓取目前日期，判斷是否超過兌獎期限以及是否開獎
        Calendar calendar = Calendar.getInstance();
        String date_format = "yyyy/MM/dd-HH:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(date_format, Locale.TAIWAN);

        String target_interval = interval_selector.getSelectedItem().toString(); // 獲取目前發票之時段
        String split_interval[] = target_interval.split("~"); // 提取年分及奇數月份
        String processed_interval[] = split_interval[0].split("年"); // 分開年分及奇數月份


        // 判斷是否開獎(開獎時間：每單月25日下午1:30~1:50，為求保險，判斷時段為下午2:00)
        try {
            // 取得目前時間
            Date current_date = format.parse(format.format(calendar.getTime()));
            // 開獎時間
            Calendar draw_calendar = Calendar.getInstance();
            draw_calendar.set(Integer.parseInt(processed_interval[0]), Integer.parseInt(processed_interval[1]), 25);
            draw_calendar.add(Calendar.MONTH, 1); // 開獎時間為奇數月後的下兩個月
            Date draw_date = draw_calendar.getTime();

            Long DayDiff = draw_date.getTime() - current_date.getTime();
            Integer daydiff = (int) (DayDiff / (24*60*60*1000));

            if (current_date.before(draw_date)) {
                money_amount.setText("開獎倒數\n" + daydiff + "天");
                return;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        money_amount.setText("點此兌獎");
    }


    private void loadDataFromDB() {
        db = new myDBHelper(getActivity()).getWritableDatabase();
        Cursor c = db.rawQuery("SELECT DISTINCT Receipt_Interval FROM " + DB_NAME + " ORDER BY DATE(Receipt_Year), Date(Receipt_Month)", null);
        c.moveToFirst();

        // 將目前儲存的發票兌獎區間儲存為array並塞給spinner
        for (int i = 0; i < c.getCount(); i++) {
            selector_list.add(c.getString(0));
            c.moveToNext();
        }
        selector_adapter.notifyDataSetChanged();
        interval_selector.setAdapter(selector_adapter);
        // 將預設選取的項目設定為最後一個(最新)
        interval_selector.setSelection(selector_list.size() - 1);
    }

    private void findViewByIds() {
        recyclerView = view.findViewById(R.id.myRecyclerView);
        interval_selector = view.findViewById(R.id.interval_selector);
        lay1 = view.findViewById(R.id.lay1);
        receipt_amount = view.findViewById(R.id.receipt_amount);
        money_amount = view.findViewById(R.id.money_amount);
    }

    private void myInit() {
        // 初始化中獎金額及發票張數
        receipt_amount.setText("發票張數\n0");
        money_amount.setText("中獎金額\n0");
        // 初始化ArrayList
        receipt_list = new ArrayList<>();
        // 設定recyclerview為列表型態
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        // 初始化資料庫
        db = new myDBHelper(getActivity()).getWritableDatabase();
        // 初始化Spinner相關變數
        selector_list = new ArrayList<>();
        selector_adapter = new ArrayAdapter(getActivity(), R.layout.spinner_items_layout, selector_list);
    }
}
