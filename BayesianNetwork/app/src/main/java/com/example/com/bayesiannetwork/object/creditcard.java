package com.example.com.bayesiannetwork.object;

public class creditcard {
    public String card_type,card_number,card_firstname,card_lastname,card_billing;
    public int cvv,month,year,limit;

    public int getCvv() {
        return cvv;
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

    public String getCard_firstname() {
        return card_firstname;
    }

    public String getCard_lastname() {
        return card_lastname;
    }

    public String getCard_number() {
        return card_number;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_billing(String card_billing) {
        this.card_billing = card_billing;
    }

    public void setCard_firstname(String card_firstname) {
        this.card_firstname = card_firstname;
    }

    public void setCard_lastname(String card_lastname) {
        this.card_lastname = card_lastname;
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
