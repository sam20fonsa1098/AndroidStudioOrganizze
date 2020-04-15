package com.example.organizze.config;

import com.google.firebase.auth.FirebaseAuth;

public class ConfigFirebase {

    private static FirebaseAuth firebaseAuth;

    public static FirebaseAuth getFirebaseAuth(){
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

}
