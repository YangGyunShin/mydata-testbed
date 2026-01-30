package com.mydata.mydatatestbed.util;

public class FileSizeFormatter {

    // ✅ private 생성자로 인스턴스화 방지
    private FileSizeFormatter() {
    }

    public static String format(Long size) {
        if (size == null) return "";
        if (size < 1024) return size + " B";
        else if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        else return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }
}