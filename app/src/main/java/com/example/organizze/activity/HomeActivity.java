package com.example.organizze.activity;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.organizze.adapter.AdapterMoviments;
import com.example.organizze.config.ConfigFirebase;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.model.Moviment;
import com.example.organizze.model.MovimentClass;
import com.example.organizze.model.UserClass;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionMenu fabMenu;
    private MaterialCalendarView materialCalendarView;

    private TextView textHello, textMoney;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userReference;
    private ValueEventListener userValueEventListener;

    private RecyclerView recyclerView;
    private AdapterMoviments adapterMoviments;
    private List<Moviment> moviments = new ArrayList<>();
    private DatabaseReference movimentsRef;
    private ValueEventListener movimentsValueEventListener;
    private String monthYear;

    private Double despesa, receita;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        fabMenu = findViewById(R.id.menu);
        fabMenu.setIconAnimated(false);

        materialCalendarView = findViewById(R.id.calendarView);
        setCalendarView();


        textHello         = findViewById(R.id.textViewHello);
        textMoney         = findViewById(R.id.textViewMoney);
        databaseReference = ConfigFirebase.getDatabaseReference();
        firebaseAuth      = ConfigFirebase.getFirebaseAuth();

        recyclerView      = findViewById(R.id.recyclerViewMoviments);

        //Config the adapter
        adapterMoviments = new AdapterMoviments(moviments, this);

        //Config the RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapterMoviments);

        swipe();
    }

    public void swipe() {
        ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags  = ItemTouchHelper.ACTION_STATE_IDLE;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                excludeMoviment(viewHolder);
            }
        };
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    public void excludeMoviment(final RecyclerView.ViewHolder viewHolder) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle("Excluir a Movimentação da Conta");
        alertDialog.setMessage("Você tem certeza que deseja realmente exluir essa movimentação ?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int position = viewHolder.getAdapterPosition();
                Moviment moviment = moviments.get(position);
                String email = firebaseAuth.getCurrentUser().getEmail();
                String id    = Base64Custom.encodeBase64(email);
                movimentsRef = databaseReference.child("Moviments")
                                                .child(id)
                                                .child(monthYear)
                                                .child(moviment.getKey());
                movimentsRef.removeValue();
                adapterMoviments.notifyItemRemoved(position);
                updateMoney(moviment);
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(HomeActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
                adapterMoviments.notifyDataSetChanged();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    public void updateMoney(Moviment moviment) {
        userReference = databaseReference.child("users")
                .child(Base64Custom.encodeBase64(firebaseAuth.getCurrentUser().getEmail()));
        if(moviment.getType().equals("Despesa")) {
           userReference.child("despesaTotal").setValue(despesa - moviment.getMoney());
        }
        else{
           userReference.child("receitaTotal").setValue(receita - moviment.getMoney());
        }
        updateData();
    }

    public void getMoviments() {
        String email = firebaseAuth.getCurrentUser().getEmail();
        String id    = Base64Custom.encodeBase64(email);
        movimentsRef = databaseReference.child("Moviments")
                                        .child(id)
                                        .child(monthYear);

        movimentsValueEventListener = movimentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                moviments.clear();
                for(DataSnapshot data: dataSnapshot.getChildren()) {
                    MovimentClass movimentAux  = data.getValue(MovimentClass.class);
                    Moviment movimentAux2      = new Moviment(movimentAux.getDate(),
                                                              movimentAux.getCategory(),
                                                              movimentAux.getDescription(),
                                                              movimentAux.getMoney(),
                                                              movimentAux.getType());
                    movimentAux2.setKey(data.getKey());
                    moviments.add(movimentAux2);
                }
                adapterMoviments.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuOut:
                firebaseAuth = ConfigFirebase.getFirebaseAuth();
                firebaseAuth.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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

        CalendarDay currentDate = materialCalendarView.getCurrentDate();
        String month            = String.format("%02d", currentDate.getMonth() + 1);
        monthYear               = String.valueOf(month + "" + currentDate.getYear());

        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String month = String.format("%02d", date.getMonth() + 1);
                monthYear    = String.valueOf(month + "" + date.getYear());
                movimentsRef.removeEventListener(movimentsValueEventListener);
                getMoviments();
            }
        });
    }

    public void updateData() {
        userReference = databaseReference.child("users")
                .child(Base64Custom.encodeBase64(firebaseAuth.getCurrentUser().getEmail()));

        userValueEventListener = userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserClass userClass = dataSnapshot.getValue(UserClass.class);
                textHello.setText("Olá, " + userClass.getName());

                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                textMoney.setText("R$ " + decimalFormat.format(userClass.getReceitaTotal() - userClass.getDespesaTotal()));
                receita = userClass.getReceitaTotal();
                despesa = userClass.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*
        * Removing eventListener
        * */
        userReference.removeEventListener(userValueEventListener);
        movimentsRef.removeEventListener(movimentsValueEventListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateData();
        getMoviments();
    }
}
