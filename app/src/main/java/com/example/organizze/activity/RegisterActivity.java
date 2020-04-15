package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.config.ConfigFirebase;
import com.example.organizze.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText fieldName, fieldEmail, fieldPassword;
    private Button buttonRegisterFinish;
    private FirebaseAuth firebaseAuth;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fieldName            = findViewById(R.id.editTextRegisterName);
        fieldEmail           = findViewById(R.id.editTextRegisterEmail);
        fieldPassword        = findViewById(R.id.editTextRegisterPassword);
        buttonRegisterFinish = findViewById(R.id.buttonRegisterFinish);


        buttonRegisterFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = fieldName.getText().toString();
                String email = fieldEmail.getText().toString();
                String password = fieldPassword.getText().toString();

                if(!name.isEmpty()){
                    if(!email.isEmpty()){
                        if(!password.isEmpty()){
                            user = new User(name, email, password);
                            registerUser();
                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Preencha a senha", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(RegisterActivity.this, "Preencha o nome!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void registerUser(){
        firebaseAuth = ConfigFirebase.getFirebaseAuth();
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Sucesso ao cadastrar usuário", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Erro ao cadastrar usuário", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}
