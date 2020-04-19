package com.example.organizze.activity;

import android.content.Intent;
import android.os.Bundle;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import com.example.organizze.R;

public class HomeActivity extends AppCompatActivity {

    private FloatingActionMenu fabMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabMenu = findViewById(R.id.menu);
        fabMenu.setIconAnimated(false);

    }

    public void addDespesa(View view) {
        startActivity(new Intent(this, DespesaActivity.class));
    }

    public void addReceita(View view) {
        startActivity(new Intent(this, ReceitasActivity.class));
    }

}
