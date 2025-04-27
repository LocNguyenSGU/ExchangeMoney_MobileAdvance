package com.example.exchangemoney;

public class CurrencyModel {
    private final String code; // Mã tiền tệ
    private final String displayName; // Tên đầy đủ của tiền tệ

    public CurrencyModel(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return code + " - " + displayName; // Hiển thị mã tiền tệ và tên đầy đủ
    }
}
