package com.example.mystore.models;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String id, name, email, phone, address, type, image;
    private boolean ban;

    public UserModel(String id, String name, String email, String phone, String address, String type, String image, boolean ban) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.type = type;
        this.image = image;
        this.ban = ban;
    }

    public UserModel(String id, String name, String email, String type, boolean ban) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.type = type;
        this.ban = ban;
    }

    public UserModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getBan() {
        return ban;
    }

    public void setBan(boolean ban) {
        this.ban = ban;
    }
}
