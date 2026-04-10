package com.example.realestate.dto;

public class MonthlyRegistrationDto {
    private int month;
    private long count;
    public MonthlyRegistrationDto(int month, long count) { this.month = month; this.count = count; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}