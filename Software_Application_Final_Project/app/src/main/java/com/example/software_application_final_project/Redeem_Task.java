package com.example.software_application_final_project;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Redeem_Task extends AsyncTask<String, Void, Integer> {

    private String DB_NAME = "MY_RECEIPT";
    private String interval;
    private SQLiteDatabase db;
    private int str_money_amount;
    private Context context;


    public Redeem_Task(Context context, String interval){
        this.interval = interval;
        this.context = context;
        db = new myDBHelper(context).getWritableDatabase();
        str_money_amount = 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        TextView money_amount = (TextView) ((Activity) context).findViewById(R.id.money_amount);
        money_amount.setText("中獎金額\n" + str_money_amount);
    }

    @Override
    protected Integer doInBackground(String... strings) {

        // 用爬蟲抓取發票開獎號碼
        String target_interval = interval; // 獲取目前發票之時段
        String split_interval[] = target_interval.split("~"); // 提取年分及奇數月份
        String processed_interval[] = split_interval[0].split("年"); // 分開年分及奇數月份
        String year = Integer.toString(Integer.parseInt(processed_interval[0]) - 1911); // 西元年轉為民國年
        String month = processed_interval[1].length() == 1 ? "0" + processed_interval[1] : processed_interval[1]; // 月份補0
        String target_url = "https://www.etax.nat.gov.tw/etw-main/ETW183W2_" + year + month + "/"; // 財政部提供之開獎網站

        try {
            Document document = Jsoup.connect(target_url).get();// 抓取所有號碼
            Elements elements = document.select("div[class='col-12 mb-3']");
            // 進行兌獎
            String redeem_num[] = elements.text().split(" ");
            str_money_amount = Redeem(redeem_num);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return str_money_amount;
    }

    private int Redeem(String[] redeem_num) {
        //獎金
        int money = 0;

        //頭獎金額
        Map<Integer, Integer> jackpotMap = new HashMap<>();
        jackpotMap.put(8, 200000);
        jackpotMap.put(7, 40000);
        jackpotMap.put(6, 10000);
        jackpotMap.put(5, 4000);
        jackpotMap.put(4, 1000);
        jackpotMap.put(3, 200);

        //取出該開獎區間所有發票
        Cursor c = db.rawQuery("SELECT Receipt_Number FROM " + DB_NAME + " WHERE Receipt_Interval='" + interval + "'", null);
        c.moveToFirst();

        for (int i = 0; i < c.getCount(); i++) {
            if (i != 0) c.moveToNext();
            //當前號碼
            String receipt = c.getString(0).substring(3);

            //特別獎中獎號碼
            String pstr = redeem_num[0];

            if (receipt.equals(pstr)) {
                money += 10000000;
                continue;
            }

            //特獎中獎號碼
            pstr = redeem_num[1];
            if (receipt.equals(pstr)) {
                money += 2000000;
                continue;
            }

            //增開六獎中獎號碼
            pstr = redeem_num[5];
            if (receipt.substring(5).equals(pstr)) {
                money += 200;
                continue;
            }

            //頭獎
            for (int j = 2; j < 5; j++) {
                int samebits = 0;
                //計算當前號碼與中獎號碼有幾個數字相同
                for (int k = 7; k > 0; k--) {
                    // 計算重複位數
                    if (receipt.substring(k).equals(redeem_num[j].substring(k))) {
                        samebits = samebits + 1;
                    }
                    //若發生不相同的數字就直接跳出for迴圈進入獎金判斷
                    else {
                        if (samebits < 3) break;
                    }
                }
                //頭獎
                if (jackpotMap.containsKey(samebits)) {
                    money += jackpotMap.get(samebits);
                    continue;
                }
            }
        }

        return money;
    }
}
