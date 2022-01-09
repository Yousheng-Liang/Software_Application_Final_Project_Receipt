package com.example.software_application_final_project;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class myAdapter extends RecyclerView.Adapter<myAdapter.ViewHolder> {

    private ArrayList<String> list;
    private Cursor c;
    private View view;
    private TreeMap<String, Integer> redeem_receipts;


    public myAdapter(Cursor c){
        this.c = c;
        redeem_receipts = new TreeMap<>();
    }

    public myAdapter(Cursor c, TreeMap<String, Integer> redeem_receipts){
        this.c = c;
        this.redeem_receipts = redeem_receipts;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvReceiptDate, tvReceiptNumber, tv_redeem_money;
        private CardView receipt_container;


        public ViewHolder(View itemView) {
            super(itemView);
            tvReceiptDate = itemView.findViewById(R.id.tvReceiptDate);
            tvReceiptNumber = itemView.findViewById(R.id.tvReceiptNumber);
            tv_redeem_money = itemView.findViewById(R.id.tv_redeem_money);
            receipt_container = itemView.findViewById(R.id.receipt_container);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // 連結recyclerView布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent ,false);
        this.view = view;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // 設定各物件要顯示的內容
        try{
            holder.tvReceiptDate.setText(c.getString(0) + "/" + c.getString(1) + "/" +  c.getString(2));
            holder.tvReceiptNumber.setText(c.getString(3));
            if(redeem_receipts.size()>0){
                if(redeem_receipts.containsKey(c.getString(3).substring(3))){
                    Integer money = redeem_receipts.get(c.getString(3).substring(3));
                    holder.receipt_container.setCardBackgroundColor(0xffedb97e);
                    holder.tv_redeem_money.setText("中獎:" + money + "元");
                    holder.tv_redeem_money.setVisibility(View.VISIBLE);
                }
            }
            c.moveToNext();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return c.getCount();
    }
}
