package com.example.exchangemoney;

import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<CurrencyModel> {
    private final Context context;
    private List<CurrencyModel> currencyModelList;
    private List<CurrencyModel> filteredCurrencyList;

    public CustomAdapter(Context context, List<CurrencyModel> currencyModelList) {
        super(context, android.R.layout.simple_dropdown_item_1line, currencyModelList);
        this.context = context;
        this.currencyModelList = currencyModelList;
        this.filteredCurrencyList = currencyModelList;
    }

    @Override
    public int getCount() {
        return filteredCurrencyList.size();
    }

    @Override
    public CurrencyModel getItem(int position) {
        return filteredCurrencyList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Hiển thị mã và tên tiền tệ khi chọn
        TextView textView = (TextView) LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false);
        textView.setText(filteredCurrencyList.get(position).toString());

        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // Hiển thị dropdown khi mở ra
        TextView textView = (TextView) LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        textView.setText(filteredCurrencyList.get(position).toString());

        return textView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0 || constraint.toString().trim().isEmpty()) {
                    results.values = currencyModelList;
                    results.count = currencyModelList.size();
                } else {
                    List<CurrencyModel> filteredList = new ArrayList<>();
                    String filterPattern = constraint.toString().toLowerCase();

                    // Tìm kiếm theo mã tiền tệ hoặc tên tiền tệ
                    for (CurrencyModel currency : currencyModelList) {
                        if (currency.getCode().toLowerCase().contains(filterPattern) ||
                                currency.getDisplayName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(currency);
                        }
                    }
                    results.values = filteredList;
                    results.count = filteredList.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredCurrencyList = (List<CurrencyModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
