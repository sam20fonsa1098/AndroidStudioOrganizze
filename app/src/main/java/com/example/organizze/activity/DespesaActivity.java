package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.config.ConfigFirebase;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.helper.DateUtil;
import com.example.organizze.model.Moviment;
import com.example.organizze.model.User;
import com.example.organizze.model.UserClass;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DespesaActivity extends AppCompatActivity {

    private TextInputEditText  fieldData, fieldCategory, fieldDescription;
    private EditText fieldMoney;

    private Moviment moviment;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private Double currentDespesa;
    private Double newDespesa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa);

        fieldMoney       = findViewById(R.id.editTextDespesaMoney);
        fieldData        = findViewById(R.id.textInputEditTextDespesaData);
        fieldCategory    = findViewById(R.id.textInputEditTextDespesaCategory);
        fieldDescription = findViewById(R.id.textInputEditTextDespesaDescription);

        databaseReference = ConfigFirebase.getDatabaseReference();
        firebaseAuth      = ConfigFirebase.getFirebaseAuth();

        fieldData.setText(DateUtil.currentDate());
        takeDespesa();

    }

    public void saveDespesa(View view) {
        if(validationFieldsDespesa()) {
            newDespesa = Double.parseDouble(fieldMoney.getText().toString());
            moviment = new Moviment(fieldData.getText().toString(),
                    fieldCategory.getText().toString(),
                    fieldDescription.getText().toString(),
                    newDespesa,
                    "Despesa");

            updateDespesa(newDespesa + currentDespesa);
            moviment.save();
            finish();
        }
    }

    public Boolean validationFieldsDespesa() {
        String textMoney       = fieldMoney.getText().toString();
        String textDescription = fieldDescription.getText().toString();
        String textCategory    = fieldCategory.getText().toString();
        String textDate        = fieldData.getText().toString();
        if(!textMoney.isEmpty()) {
            if(!textDate.isEmpty()) {
                if(!textCategory.isEmpty()) {
                    if(!textDescription.isEmpty()) {
                        return true;
                    }
                    else{
                        Toast.makeText(this, "Digite a descrição da despesa", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                else{
                    Toast.makeText(this, "Digite a categoria da despesa", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            else{
                Toast.makeText(this, "Digite a data da despesa", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        else{
            Toast.makeText(this, "Digite o valor da despesa", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void takeDespesa() {
        DatabaseReference userReference = databaseReference.child("users")
                                                           .child(Base64Custom.encodeBase64(firebaseAuth.getCurrentUser().getEmail()));

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserClass user = dataSnapshot.getValue(UserClass.class);
                currentDespesa = user.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateDespesa(Double value) {
        DatabaseReference userReference = databaseReference.child("users")
                .child(Base64Custom.encodeBase64(firebaseAuth.getCurrentUser().getEmail()));

        userReference.child("despesaTotal")
                .setValue(value);
    }
}
