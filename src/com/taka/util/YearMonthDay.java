package com.taka.util;

public class YearMonthDay {
    private long year;
    private long month;
    private long day;

    public YearMonthDay(long year, long month, long day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public long getYear() {
        return year;
    }

    public long getMonth() {
        return month;
    }

    public long getDay() {
        return day;
    }

    public void setYear(long year) {
        this.year = year;
    }

    public void setMonth(long month) {
        this.month = month;
    }

    public void setDay(long day) {
        this.day = day;
    }
}
