package com.example.exchangemoney;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<CurrencyModel> {
    private final Context context;
    private final List<CurrencyModel> currencyModelList;

    public CustomAdapter(Context context, List<CurrencyModel> currencyModelList) {
        super(context, android.R.layout.simple_spinner_item, currencyModelList);
        this.context = context;
        this.currencyModelList = currencyModelList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Lúc này sẽ hiển thị mã tiền tệ khi chưa chọn
        TextView textView = (TextView) LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false);
        textView.setText(currencyModelList.get(position).getCode()); // Chỉ hiển thị mã tiền tệ
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Khi người dùng mở dropdown, hiển thị mã và tên đầy đủ
        TextView textView = (TextView) LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        textView.setText(currencyModelList.get(position).toString()); // Hiển thị cả mã và tên đầy đủ
        return textView;
    }
}
