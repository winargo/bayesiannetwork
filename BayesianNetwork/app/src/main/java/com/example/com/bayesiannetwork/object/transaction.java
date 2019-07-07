package com.example.com.bayesiannetwork.object;

public class transaction {
    String tid,tpayment,tstatus,tcart,tdate;
    Double tdouble;

    public Double getTdouble() {
        return tdouble;
    }

    public String getTdate() {
        return tdate;
    }

    public String getTcart() {
        return tcart;
    }

    public String getTid() {
        return tid;
    }

    public String getTpayment() {
        return tpayment;
    }

    public String getTstatus() {
        return tstatus;
    }

    public void setTcart(String tcart) {
        this.tcart = tcart;
    }

    public void setTdate(String tdate) {
        this.tdate = tdate;
    }

    public void setTdouble(Double tdouble) {
        this.tdouble = tdouble;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public void setTpayment(String tpayment) {
        this.tpayment = tpayment;
    }

    public void setTstatus(String tstatus) {
        this.tstatus = tstatus;
    }
}
