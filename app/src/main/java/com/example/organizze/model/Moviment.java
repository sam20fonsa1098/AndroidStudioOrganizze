package com.example.organizze.model;

import android.text.Editable;
import android.util.Base64;

import com.example.organizze.config.ConfigFirebase;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.helper.DateUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Moviment {

    private String date;
    private String category;
    private String description;
    private Double money;
    private String type;

    public Moviment(String date, String category, String description, double money, String type) {
        this.date = date;
        this.category = category;
        this.description = description;
        this.money = money;
        this.type  = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void save() {
        FirebaseAuth firebaseAuth = ConfigFirebase.getFirebaseAuth();
        String idUser = Base64Custom.encodeBase64(firebaseAuth.getCurrentUser().getEmail());
        String date   = DateUtil.convertToNumber(this.date);

        DatabaseReference databaseReference = ConfigFirebase.getDatabaseReference();
        databaseReference.child("Moviments")
                .child(idUser)
                .child(date)
                .push()
                .setValue(this);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }
}
