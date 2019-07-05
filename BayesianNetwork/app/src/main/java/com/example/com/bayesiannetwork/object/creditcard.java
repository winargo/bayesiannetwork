package com.example.com.bayesiannetwork.object;

public class creditcard {
    public String card_type,card_number,card_name,card_billing,card_id;
    public int cvv,month,year,limit;

    public int getCvv() {
        return cvv;
    }

    public String getCard_id() {
        return card_id;
    }

    public int getLimit() {
        return limit;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public String getCard_billing() {
        return card_billing;
    }
    public String getCard_number() {
        return card_number;
    }

    public void setCard_id(String card_id) {
        this.card_id = card_id;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_billing(String card_billing) {
        this.card_billing = card_billing;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
