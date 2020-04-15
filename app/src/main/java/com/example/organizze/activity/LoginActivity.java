package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText fieldEmail, fieldPassword;
    private Button buttonLoginFinish;
    private User user;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fieldEmail        = findViewById(R.id.editTextLoginEmail);
        fieldPassword     = findViewById(R.id.editTextLoginPassword);
        buttonLoginFinish = findViewById(R.id.buttonLoginFinish);

        buttonLoginFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email    = fieldEmail.getText().toString();
                String password = fieldPassword.getText().toString();
                if(!email.isEmpty()){
                    if(!password.isEmpty()){
                        user = new User("Anonimo", email, password);
                        validationLogin();

                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Preencha a senha", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(LoginActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validationLogin(){
        firebaseAuth = ConfigFirebase.getFirebaseAuth();
        firebaseAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        openHome();
                    }
                    else{
                        String exception = "";
                        try{
                            throw task.getException();
                        }
                        catch (FirebaseAuthInvalidUserException e ){
                            exception = "Usuário não está cadastrado";
                        }
                        catch (FirebaseAuthInvalidCredentialsException e) {
                            exception = "Email e senha não correspondem a um usuário cadastrado";
                        }
                        catch (Exception e ){
                            exception = "Erro ao logar com o usuário: " + e.getMessage();
                        }
                        Toast.makeText(LoginActivity.this, exception, Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    public void openHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }
}
