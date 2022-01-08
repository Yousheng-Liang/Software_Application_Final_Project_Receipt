package com.example.software_application_final_project;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class Receipt_Record_Fragment extends Fragment {

    // 物件及介面相關變數
    View view;
    Spinner interval_selector;
    private RecyclerView recyclerView;
    private LinearLayout lay1;
    private TextView receipt_amount, money_amount;
    myAdapter adapter;

    // 資料庫相關變數
    private final String DB_NAME = "MY_RECEIPT";
    private SQLiteDatabase db;

    // 宣告要讓spinner顯示選項的物件
    ArrayList<String> selector_list;
    ArrayAdapter selector_adapter;

    // 發票資料相關變數
    ArrayList<String> receipt_list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_receipt__record, container, false);

        findViewByIds();
        myInit();
        try {
            loadDataFromDB();
        }catch (Exception e){
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private void loadDataFromDB() {
        db = new myDBHelper(getActivity()).getWritableDatabase();
        Cursor c = db.rawQuery("SELECT DISTINCT Receipt_Interval FROM " + DB_NAME + " ORDER BY Receipt_Year, CAST(strftime('%m', Receipt_Month) AS INTEGER)", null);
        c.moveToFirst();

        // 將目前儲存的發票兌獎區間儲存為array並塞給spinner
        for(int i=0; i<c.getCount(); i++){
            selector_list.add(c.getString(0));
            c.moveToNext();
        }
        selector_adapter.notifyDataSetChanged();
        interval_selector.setAdapter(selector_adapter);
        // 將預設選取的項目設定為最後一個(最新)
        interval_selector.setSelection(selector_list.size()-1);
    }

    private void findViewByIds(){
        recyclerView = view.findViewById(R.id.myRecyclerView);
        interval_selector = view.findViewById(R.id.interval_selector);
        lay1 = view.findViewById(R.id.lay1);
        receipt_amount = view.findViewById(R.id.receipt_amount);
        money_amount = view.findViewById(R.id.money_amount);
    }

    private void myInit(){
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