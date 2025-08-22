package com.jorge.app_02.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jorge.app_02.R;
import com.jorge.app_02.controller.GlicemiaController;
import com.jorge.app_02.model.Glicemia;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NovaGlicemia extends AppCompatActivity {

    // Views da interface do usuário
    private EditText editTextGlicemia, editTextComentario;
    private TextView textViewData, textViewHora, textViewStatus;
    private Button btnSalvarGlicemia;

    // Controlador para interagir com o banco de dados
    private GlicemiaController glicemiaController;

    // Constantes para SharedPreferences (armazenamento local)
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_LOGGED_IN_USER_ID = "loggedInUserId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_glicemia);

        // Inicializa o controlador
        glicemiaController = new GlicemiaController(this);

        // Referencia as views do layout XML
        setupViews();

        // Configura a data e hora atuais nos TextViews
        setupDateTime();

        // Configura o listener do botão de salvar
        setupSaveButton();
    }

    /**
     * Inicializa todas as Views referenciadas no layout.
     */
    private void setupViews() {
        editTextGlicemia = findViewById(R.id.editTextGlicemia);
        editTextComentario = findViewById(R.id.editTextComentario);
        textViewData = findViewById(R.id.textViewData);
        textViewHora = findViewById(R.id.textViewHora);
        textViewStatus = findViewById(R.id.textViewStatus);
        btnSalvarGlicemia = findViewById(R.id.btnSalvarGlicemia);
    }

    /**
     * Define a data e hora atuais formatadas nos TextViews.
     */
    private void setupDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        textViewData.setText("Data: " + dateFormat.format(new Date()));
        textViewHora.setText("Hora: " + timeFormat.format(new Date()));
    }

    /**
     * Configura a ação do botão de salvar, chamando o método salvarGlicemia().
     */
    private void setupSaveButton() {
        btnSalvarGlicemia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarGlicemia();
            }
        });
    }

    /**
     * Obtém o ID do usuário logado a partir das SharedPreferences.
     * @return O ID do usuário, ou -1 se não estiver logado.
     */
    private int getLoggedInUserId() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getInt(KEY_LOGGED_IN_USER_ID, -1);
    }

    /**
     * Determina o status da glicemia (Normal, Alta ou Baixa).
     * @param valorGlicemia O valor da glicemia inserido pelo usuário.
     * @return Uma string com o status correspondente.
     */
    private String getGlicemiaStatus(int valorGlicemia) {
        if (valorGlicemia >= 126) {
            return "Alta (Hiperglicemia)";
        } else if (valorGlicemia < 70) {
            return "Baixa (Hipoglicemia)";
        } else {
            return "Normal";
        }
    }

    /**
     * Salva o novo registro de glicemia no banco de dados.
     * Inclui validações para garantir a integridade dos dados.
     */
    private void salvarGlicemia() {
        // Verifica se o ID do usuário é válido
        int userId = getLoggedInUserId();
        if (userId == -1) {
            Toast.makeText(this, "Erro: Usuário não logado. Por favor, faça login novamente.", Toast.LENGTH_LONG).show();
            // Redireciona para a tela de login
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // Obtém o valor da glicemia
        String glicemiaStr = editTextGlicemia.getText().toString();
        if (glicemiaStr.isEmpty()) {
            Toast.makeText(this, "Por favor, insira o valor da glicemia.", Toast.LENGTH_SHORT).show();
            return;
        }

        int glicemiaValue;
        try {
            // Tenta converter a string para um número inteiro
            glicemiaValue = Integer.parseInt(glicemiaStr);
        } catch (NumberFormatException e) {
            // Exibe um erro se a conversão falhar
            Toast.makeText(this, "Valor de glicemia inválido. Por favor, insira apenas números.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Obtém o comentário
        String comentario = editTextComentario.getText().toString();

        // Cria e preenche o objeto Glicemia
        Glicemia novaGlicemia = new Glicemia();
        novaGlicemia.setUserId(userId);
        novaGlicemia.setGlicemia(glicemiaValue);
        novaGlicemia.setData(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()));
        novaGlicemia.setHora(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        novaGlicemia.setStatus(getGlicemiaStatus(glicemiaValue));
        novaGlicemia.setComentario(comentario);

        // Salva o objeto no banco de dados através do controlador
        long resultado = glicemiaController.salvarGlicemia(novaGlicemia);

        // Exibe o resultado para o usuário
        if (resultado > 0) {
            Toast.makeText(this, "Registro de glicemia salvo com sucesso!", Toast.LENGTH_SHORT).show();
            textViewStatus.setText("Status: " + novaGlicemia.getStatus());
            // Finaliza a activity para retornar à lista de registros
            finish();
        } else {
            Toast.makeText(this, "Erro ao salvar registro de glicemia.", Toast.LENGTH_SHORT).show();
        }
    }
}