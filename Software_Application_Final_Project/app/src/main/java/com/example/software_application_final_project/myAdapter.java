package com.example.software_application_final_project;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class myAdapter extends RecyclerView.Adapter<myAdapter.ViewHolder> {

    private ArrayList<String> list;
    private Cursor c;


    public myAdapter(Cursor c){
        this.c = c;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvReceiptDate, tvReceiptNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            tvReceiptDate = itemView.findViewById(R.id.tvReceiptDate);
            tvReceiptNumber = itemView.findViewById(R.id.tvReceiptNumber);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // 連結recyclerView布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, parent ,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // 設定各物件要顯示的內容
        try{
            holder.tvReceiptDate.setText(c.getString(0) + "/" + c.getString(1) + "/" +  c.getString(2));
            holder.tvReceiptNumber.setText(c.getString(3));
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
