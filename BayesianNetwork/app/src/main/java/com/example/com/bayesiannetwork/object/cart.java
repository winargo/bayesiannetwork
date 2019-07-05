package com.example.com.bayesiannetwork.object;

public class cart {
    String cart_id,product_id,productname,img;
    int qty;
    double total;

    public double getTotal() {
        return total;
    }

    public String getImg() {
        return img;
    }

    public String getProductname() {
        return productname;
    }

    public int getQty() {
        return qty;
    }

    public String getCart_id() {
        return cart_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setCart_id(String cart_id) {
        this.cart_id = cart_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setTotal(double total) {
        this.total = total;
    }

}
