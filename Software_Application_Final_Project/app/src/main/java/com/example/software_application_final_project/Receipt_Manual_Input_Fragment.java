package com.example.software_application_final_project;

import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Receipt_Manual_Input_Fragment extends Fragment {

    private View view;

    // 物件變數宣告
    EditText boxDate, boxNumber1, boxNumber2;
    Button btnWrite;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_receipt__manual__input_, container, false);

        findViewByIds();  // 連結物件變數
        Init();  // 初始化

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 檢查使用者輸入之發票格式是否有誤
                if (check_receipt_format()) {
                    try {
                        String[] date_tmp = boxDate.getText().toString().split("/");
                        receipt_year = date_tmp[0];
                        receipt_number = boxNumber1.getText().toString().toUpperCase(Locale.ROOT) + "-" + boxNumber2.getText().toString();


                        // 把發票資料寫入資料庫
                        db.execSQL("INSERT INTO " + DB_NAME + "(Receipt_Year, Receipt_Month, Receipt_Day, Receipt_Number, Receipt_Interval) VALUES(?,?,?,?,?)",
                                new String[]{receipt_year, date_tmp[1], date_tmp[2], receipt_number, receipt_year + "年" + receipt_interval[Integer.parseInt(date_tmp[1])]});
                        Toast.makeText(getActivity(), "發票資料已寫入", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Toast.makeText(getActivity(), "新增資料時發生了錯誤", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(), "發票格式有誤", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 設定日期dialog
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                // 判斷使用者是不是穿越時空，輸入了未來的發票
                Calendar set_calendar = Calendar.getInstance();
                set_calendar.set(year, month, dayOfMonth);

                Calendar today_calendat = Calendar.getInstance();
                if(today_calendat.before(set_calendar)){
                    Toast.makeText(getActivity(), "請勿加入未來發票，買彩券比較好賺", Toast.LENGTH_SHORT).show();
                }else{
                    // 初始化Calendar
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);
                    String date_format = "yyyy/M/d";
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(date_format, Locale.TAIWAN);
                    boxDate.setText(simpleDateFormat.format(calendar.getTime()));
                }
            }
        };

        boxDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 初始化Calendar
                Calendar calendar = Calendar.getInstance();
                new DatePickerDialog(getActivity(), dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        return view;
    }

    private boolean check_receipt_format() {

        // 檢查長度是否正確
        if (boxNumber1.getText().toString().replace(" ", "").length() != 2) return false;
        if (boxNumber2.getText().toString().replace(" ", "").length() != 8) return false;

        // 檢查英文部分
        try {
            String tmp = boxNumber1.getText().toString().toUpperCase();
            for (int i = 0; i < 2; i++) {
                if (tmp.charAt(i) < 65 || tmp.charAt(i) > 90) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        // 檢查數字部分
        try {
            String tmp = boxNumber2.getText().toString();
            for (int i = 0; i < 8; i++) {
                if (tmp.charAt(i) < 48 || tmp.charAt(i) > 57) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private void Init() {
        // 變數初始化
        list = new ArrayList<>();
        // 設定adapter及listview
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        // 資料庫初始化
        db = new myDBHelper(getActivity()).getWritableDatabase();
    }

    private void findViewByIds() {
        boxDate = view.findViewById(R.id.boxDate);
        boxNumber1 = view.findViewById(R.id.boxNumber1);
        boxNumber2 = view.findViewById(R.id.boxNumber2);
        btnWrite = view.findViewById(R.id.btnWrite);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();  // 關閉資料庫
    }
}