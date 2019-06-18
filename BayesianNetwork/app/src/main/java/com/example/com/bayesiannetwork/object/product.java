package com.example.com.bayesiannetwork.object;

public class product {
    public String productname,image,description,id;
    int left;
    double price;

    public String getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public int getLeft() {
        return left;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getProductname() {
        return productname;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

}
