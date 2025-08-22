package com.jorge.app_02.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jorge.app_02.R;
import com.jorge.app_02.adapter.GlicemiaAdapter;
import com.jorge.app_02.controller.GlicemiaController;
import com.jorge.app_02.model.Glicemia;
import com.jorge.app_02.util.SessionManager; // Importar a nova classe

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textViewNomeUsuario;
    private FloatingActionButton btnNovoRegistro, btnVerPerfil;
    private RecyclerView recyclerViewGlicemias;
    private GlicemiaAdapter glicemiaAdapter;
    private GlicemiaController glicemiaController;
    private List<Glicemia> listaGlicemias;
    private SessionManager sessionManager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        glicemiaController = new GlicemiaController(this);
        sessionManager = new SessionManager(this); // Inicializa o SessionManager

        textViewNomeUsuario = findViewById(R.id.textViewNomeUsuario);
        btnNovoRegistro = findViewById(R.id.btnNovoRegistro);
        btnVerPerfil = findViewById(R.id.btnVerPerfil);
        recyclerViewGlicemias = findViewById(R.id.recyclerViewGlicemias);

        recyclerViewGlicemias.setLayoutManager(new LinearLayoutManager(this));
        listaGlicemias = new ArrayList<>();
        glicemiaAdapter = new GlicemiaAdapter(this, listaGlicemias);
        recyclerViewGlicemias.setAdapter(glicemiaAdapter);

        loadUserData();
        setupButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGlicemias();
    }

    private void loadUserData() {
        String loggedInUsername = sessionManager.getLoggedInUsername();
        if (loggedInUsername != null) {
            textViewNomeUsuario.setText("Olá, " + loggedInUsername);
        } else {
            textViewNomeUsuario.setText("Olá, Usuário");
        }
    }

    private void loadGlicemias() {
        int userId = sessionManager.getLoggedInUserId();
        if (userId != -1) {
            List<Glicemia> glicemias = glicemiaController.buscarGlicemiasPorUsuario(userId);
            listaGlicemias.clear();
            listaGlicemias.addAll(glicemias);
            glicemiaAdapter.notifyDataSetChanged();
            if (glicemias.isEmpty()) {
                Toast.makeText(this, "Nenhum registro de glicemia encontrado.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Erro: ID do usuário não encontrado.", Toast.LENGTH_SHORT).show();
            // Opcional: Redirecionar para o Login
            // Intent intent = new Intent(this, LoginActivity.class);
            // startActivity(intent);
            // finish();
        }
    }

    private void setupButtons() {
        btnNovoRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NovaGlicemia.class);
            startActivity(intent);
        });

        btnVerPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PerfilUsuarioActivity.class);
            startActivity(intent);
        });
    }
}