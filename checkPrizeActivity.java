package com.example.receipt_test;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class checkPrizeActivity extends AppCompatActivity {

    Button btnCheckPrize;
    EditText boxYear;
    Spinner boxMonth;
    ListView mylist;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    /*
        boxSpecial1: 特別獎
        boxSpecial2: 特獎
        boxJackpot1: 頭獎1
        boxJackpot2: 頭獎2
        boxJackpot3: 頭獎3
        box6Award: 增開六獎
     */
    EditText boxSpecial1, boxSpecial2, boxJackpot1, boxJackpot2, boxJackpot3, box6Award;

    // 資料庫相關變數
    private final String DB_NAME = "MY_RECEIPT";
    private SQLiteDatabase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_prize);
        findViewByIds();
        Init();



        // 一鍵兌獎(你要做這ㄍ)
        btnCheckPrize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(boxYear.getText().toString().replace(" ", "").length() <= 0){
                    Toast.makeText(getApplication(), "欄位請勿留空", Toast.LENGTH_SHORT).show();
                }else{
                    // 依據月份列表選擇月份篩選範圍
                    String tmp = boxMonth.getSelectedItem().toString().substring(0, boxMonth.getSelectedItem().toString().length()-1);
                    String[] month= tmp.split("~");  // 把月份的兩個數字拆出來

                    // 依照選擇年份及日期從資料庫撈資料出來
                    Cursor c = db.rawQuery("SELECT * FROM " + DB_NAME + " WHERE Receipt_Year='" + boxYear.getText().toString() +
                            "' AND (Receipt_Month='" + month[0] + "' OR Receipt_Month= '" + month[1] + "')", null);

                    Toast.makeText(getApplication(), "共有" + c.getCount() + "筆資料", Toast.LENGTH_SHORT).show();

                    c.moveToFirst(); // 移動到第一筆資料

                    for(int i=0; i<c.getCount(); i++){

                        String receipt = c.getString(3);  // 取出所有符合搜尋條件的發票號碼
                        Toast.makeText(getApplication(), receipt, Toast.LENGTH_SHORT).show();

                        int money=0;
                        money=Redeem(receipt);
                        Toast.makeText(getApplication(), "本張發票獲得"+money+"元", Toast.LENGTH_SHORT).show();
                    /*
                      邏輯參考：
                        第一種(可能較慢但較簡單)：
                            1. 先用字串分割抓後三碼 str.substring(8, str.length()) 或 str = str.substring(8); 兩個寫法結果一樣
                               因為發票號碼格式：AB-12345678 總共有11字元，第8個index開始是末三碼
                            2. 看末三碼有沒有符合，有的話繼續看末四碼、末五碼...，不然就換下一張發票
                            3. 依照相同的字數決定該張發票的中獎金額，規則去網路上查一下

                        第二種(可能較快)：
                            1. 寫一個迴圈，範圍從發票號碼的最後一位str.length()開始往回到0
                            2. 如果發票號的第i個字元str.charAt(i)和中獎號的一樣就繼續往下；否則直接換下一組號碼
                            3. str.length()-i的值就是相同的位數，依其決定該張發票的中獎金額，規則去網路上查一下

                      變數命名要有意義喔 不要什麼a阿b之類的，覺得需要註解的部分盡量寫

                    */

                        c.moveToNext();  // 換下一筆資料
                    }

                    c.close();



                }
            }
        });

    }

    private void Init() {
        // 資料庫初始化
        db = new myDBHelper(getApplication()).getWritableDatabase();
        // 清單元件初始化
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, list);
        mylist.setAdapter(adapter);
    }

    private void findViewByIds() {
        btnCheckPrize = findViewById(R.id.btnCheckPrize);
        boxYear = findViewById(R.id.boxPrizeYear);
        boxMonth = findViewById(R.id.boxPrizeMonth);
        boxSpecial1 = findViewById(R.id.boxSpecialReward);
        boxSpecial2 = findViewById(R.id.boxSpecialReward2);
        boxJackpot1 = findViewById(R.id.boxJackpot1);
        boxJackpot2 = findViewById(R.id.boxJackpot2);
        boxJackpot3 = findViewById(R.id.boxJackpot3);
        mylist = findViewById(R.id.listview);
        box6Award = findViewById(R.id.box6Award);
    }

    private  int Redeem(String receipt){
        //獎金
        int money=0;

        //假設中獎號碼
        String price[]={"AB-14872301","AB-37250799","AB-71086085","AB-53645821","AB-46626911","916"};

        //頭獎金額
        Map<Integer,Integer> jackpotMap = new HashMap<>();
        jackpotMap.put(8,200000);
        jackpotMap.put(7,40000);
        jackpotMap.put(6,10000);
        jackpotMap.put(5,4000);
        jackpotMap.put(4,1000);
        jackpotMap.put(3,200);

        //當前號碼
        String str = receipt.substring(3);
        //特別獎中獎號碼
        String pstr = price[0].substring(3);
        if (str.equals(pstr)){
            money=10000000;
            return money;
        }


        //特獎中獎號碼
        pstr = price[1].substring(3);
        if (str.equals(pstr)){
            money=2000000;
            return money;
        }


        //當前號碼
        str = receipt.substring(8);
        //增開六獎中獎號碼
        pstr = price[5].substring(0);
        if (str.equals(pstr)){
            money=200;
            return money;
        }

        //頭獎
        if (money==0){
            for(int j=2; j<5; j++){
                int samebits=0;
                //計算當前號碼與中獎號碼有幾個數字相同
                for (int k=8; k>2; k--){
                    //當前號碼
                    str = receipt.substring(k);
                    //String str = boxYear.getText().toString().substring(k);
                    //中獎號碼
                    pstr = price[j].substring(k);
                    if (str.equals(pstr)){
                        samebits = samebits+1;
                    }
                    //若發生不相同的數字就直接跳出for迴圈進入獎金判斷
                    else break;
                }
                //頭獎
                if (jackpotMap.containsKey(samebits+2)){
                    money=jackpotMap.get(samebits+2);
                    break;
                }
            }
        }
        return money;
    }
}