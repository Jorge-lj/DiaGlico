package com.jorge.app_02.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jorge.app_02.R;
import com.jorge.app_02.adapter.GlicemiaAdapter;
import com.jorge.app_02.controller.GlicemiaController;
import com.jorge.app_02.model.Glicemia;
import com.jorge.app_02.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GlicemiaAdapter.OnItemLongClickListener {

    private TextView textViewNomeUsuario;
    private FloatingActionButton btnNovoRegistro, btnVerPerfil;
    private RecyclerView recyclerViewGlicemias;
    private GlicemiaAdapter glicemiaAdapter;
    private GlicemiaController glicemiaController;
    private List<Glicemia> listaGlicemias;
    private SessionManager sessionManager;
    private EditText editTextSearch;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        glicemiaController = new GlicemiaController(this);
        sessionManager = new SessionManager(this);

        textViewNomeUsuario = findViewById(R.id.textViewNomeUsuario);
        btnNovoRegistro = findViewById(R.id.btnNovoRegistro);
        btnVerPerfil = findViewById(R.id.btnVerPerfil);
        recyclerViewGlicemias = findViewById(R.id.recyclerViewGlicemias);
        editTextSearch = findViewById(R.id.editTextSearch);

        recyclerViewGlicemias.setLayoutManager(new LinearLayoutManager(this));
        listaGlicemias = new ArrayList<>();
        // Note a adição do "this" como listener no construtor do adaptador
        glicemiaAdapter = new GlicemiaAdapter(this, listaGlicemias, this);
        recyclerViewGlicemias.setAdapter(glicemiaAdapter);

        loadUserData();
        setupButtons();
        setupSearch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGlicemias(""); // Recarrega a lista sem filtro quando a tela volta a ser visível
    }

    private void loadUserData() {
        String loggedInUsername = sessionManager.getLoggedInUsername();
        if (loggedInUsername != null) {
            textViewNomeUsuario.setText("Olá, " + loggedInUsername);
        } else {
            textViewNomeUsuario.setText("Olá, Usuário");
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

    private void setupSearch() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Chama o método para filtrar a lista toda vez que o texto muda
                loadGlicemias(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadGlicemias(String termoBusca) {
        int userId = sessionManager.getLoggedInUserId();
        if (userId != -1) {
            // Busca todos os registros do usuário
            List<Glicemia> glicemias = glicemiaController.buscarGlicemiasPorUsuario(userId);
            List<Glicemia> filteredList = new ArrayList<>();

            // Se o termo de busca não estiver vazio, filtra a lista
            if (termoBusca != null && !termoBusca.isEmpty()) {
                String termoBuscaLower = termoBusca.toLowerCase();
                for (Glicemia g : glicemias) {
                    // Filtra por data, hora, valor ou comentário
                    if (g.getData().toLowerCase().contains(termoBuscaLower) ||
                            g.getHora().toLowerCase().contains(termoBuscaLower) ||
                            String.valueOf(g.getGlicemia()).contains(termoBuscaLower) ||
                            g.getComentario().toLowerCase().contains(termoBuscaLower)) {
                        filteredList.add(g);
                    }
                }
            } else {
                // Se o termo de busca estiver vazio, exibe a lista completa
                filteredList = glicemias;
            }

            // Atualiza a lista no adaptador
            glicemiaAdapter.updateList(filteredList);

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "Nenhum registro encontrado.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Erro: ID do usuário não encontrado.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onItemLongClick(Glicemia glicemia) {
        // Exibe um diálogo de confirmação para exclusão
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Tem certeza que deseja excluir este registro de " + glicemia.getGlicemia() + " mg/dL?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    // Chama o controlador para deletar o registro do banco de dados
                    boolean sucesso = glicemiaController.removerGlicemia(glicemia.getId());
                    if (sucesso) {
                        Toast.makeText(MainActivity.this, "Registro excluído com sucesso.", Toast.LENGTH_SHORT).show();
                        loadGlicemias(""); // Recarrega a lista para refletir a exclusão
                    } else {
                        Toast.makeText(MainActivity.this, "Erro ao excluir o registro.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }
}
