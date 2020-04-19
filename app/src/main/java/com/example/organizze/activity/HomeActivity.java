package com.example.organizze.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.organizze.config.ConfigFirebase;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.model.UserClass;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import com.example.organizze.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionMenu fabMenu;
    private MaterialCalendarView materialCalendarView;

    private TextView textHello, textMoney;
    private String name;
    private Double receita, despesa;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabMenu = findViewById(R.id.menu);
        fabMenu.setIconAnimated(false);

        materialCalendarView = findViewById(R.id.calendarView);
        setCalendarView();

        textHello         = findViewById(R.id.textViewHello);
        textMoney         = findViewById(R.id.textViewMoney);
        databaseReference = ConfigFirebase.getDatabaseReference();
        firebaseAuth      = ConfigFirebase.getFirebaseAuth();
        updateData();
    }


    public void addDespesa(View view) {
        startActivity(new Intent(this, DespesaActivity.class));
    }

    public void addReceita(View view) {
        startActivity(new Intent(this, ReceitasActivity.class));
    }

    public void setCalendarView() {
        CharSequence months[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        materialCalendarView.setTitleMonths(months);

        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

            }
        });
    }

    public void updateData() {
        DatabaseReference userReference = databaseReference.child("users")
                .child(Base64Custom.encodeBase64(firebaseAuth.getCurrentUser().getEmail()));
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserClass userClass = dataSnapshot.getValue(UserClass.class);
                textHello.setText("Olá, " + userClass.getName());
                textMoney.setText(Double.toString(userClass.getReceitaTotal() - userClass.getDespesaTotal()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
