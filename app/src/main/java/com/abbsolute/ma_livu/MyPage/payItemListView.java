package com.abbsolute.ma_livu.MyPage;

public class payItemListView {
    private String pay_date;
    private String pay_title;
    private String pay_time_deposit_withdrawal;
    private String amount;
    private String balance;
    private String pay_time;


    public payItemListView(String pay_date,String pay_title,String pay_time_deposit_withdrawal, String amount, String balance,String pay_time){
        this.pay_date = pay_date;
        this.pay_title =  pay_title;
        this.pay_time_deposit_withdrawal = pay_time_deposit_withdrawal;
        this.amount = amount;
        this.balance = balance;
        this.pay_time = pay_time;
    }

    public String getPay_date() {
        return pay_date;
    }

    public void setPay_date(String pay_date) {
        this.pay_date = pay_date;
    }

    public String getPay_title() {
        return pay_title;
    }

    public void setPay_title(String pay_title) {
        this.pay_title = pay_title;
    }

    public String getPay_time_deposit_withdrawal() {
        return pay_time_deposit_withdrawal;
    }

    public void setPay_time_deposit_withdrawal(String pay_time_deposit_withdrawal) {
        this.pay_time_deposit_withdrawal = pay_time_deposit_withdrawal;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getPay_time() {
        return pay_time;
    }

    public void setPay_time(String pay_time) {
        this.pay_time = pay_time;
    }
}
