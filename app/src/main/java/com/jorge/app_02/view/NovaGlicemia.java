package com.jorge.app_02.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jorge.app_02.R;
import com.jorge.app_02.controller.GlicemiaController;
import com.jorge.app_02.model.Glicemia;
import com.jorge.app_02.util.Managem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NovaGlicemia extends AppCompatActivity {

    private EditText editTextGlicemia, editTextComentario;
    private TextView textViewData, textViewHora, textViewStatus;
    private Button btnSalvarGlicemia;
    private GlicemiaController glicemiaController;
    private Managem managem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_glicemia);

        // Inicializa o controlador e o gerenciador de sessão
        glicemiaController = new GlicemiaController(this);
        managem = new Managem(this);

        // Referencia as views do layout XML
        setupViews();

        // Configura a data e hora atuais nos TextViews
        setupDateTime();

        // Configura o listener do botão de salvar
        setupSaveButton();
    }

    // Inicializa todas as Views referenciadas no layout.
    private void setupViews() {
        editTextGlicemia = findViewById(R.id.editTextGlicemia);
        editTextComentario = findViewById(R.id.editTextComentario);
        textViewData = findViewById(R.id.textViewData);
        textViewHora = findViewById(R.id.textViewHora);
        textViewStatus = findViewById(R.id.textViewStatus);
        btnSalvarGlicemia = findViewById(R.id.btnSalvarGlicemia);
    }

    // Define a data e hora atuais formatadas nos TextViews.
    // Agora com o fuso horário de Brasília para garantir a precisão.
    private void setupDateTime() {
        // Criando formatos para data e hora
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        // Definindo o fuso horário de Brasília (America/Sao_Paulo)
        TimeZone timeZoneBrasilia = TimeZone.getTimeZone("America/Sao_Paulo");
        dateFormat.setTimeZone(timeZoneBrasilia);
        timeFormat.setTimeZone(timeZoneBrasilia);

        // Definindo o texto com a data e hora formatadas
        textViewData.setText("Data: " + dateFormat.format(new Date()));
        textViewHora.setText("Hora: " + timeFormat.format(new Date()));
    }

    // Configura a ação do botão de salvar, chamando o método salvarGlicemia().
    private void setupSaveButton() {
        btnSalvarGlicemia.setOnClickListener(v -> salvarGlicemia());
    }

    // Obtém o ID do usuário logado a partir das SharedPreferences.
    private int getLoggedInUserId() {
        return managem.getLoggedInUserId();
    }

    // Determina o status da glicemia (Normal, Alta ou Baixa).
    private String getGlicemiaStatus(int valorGlicemia) {
        if (valorGlicemia >= 126) {
            return "Alta (Hiperglicemia)";
        } else if (valorGlicemia < 70) {
            return "Baixa (Hipoglicemia)";
        } else {
            return "Normal";
        }
    }

    // Coleta os dados da interface e salva o registro de glicemia.
    private void salvarGlicemia() {
        int userId = getLoggedInUserId();
        if (userId == -1) {
            Toast.makeText(this, "Erro: Usuário não logado. Por favor, faça login novamente.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        String glicemiaStr = editTextGlicemia.getText().toString();
        if (glicemiaStr.isEmpty()) {
            Toast.makeText(this, "Por favor, insira o valor da glicemia.", Toast.LENGTH_SHORT).show();
            return;
        }

        int glicemiaValue;
        try {
            glicemiaValue = Integer.parseInt(glicemiaStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valor de glicemia inválido. Por favor, insira apenas números.", Toast.LENGTH_SHORT).show();
            return;
        }

        String comentario = editTextComentario.getText().toString();

        Glicemia novaGlicemia = new Glicemia();
        novaGlicemia.setUserId(userId);
        novaGlicemia.setGlicemia(glicemiaValue);

        // Usando o mesmo formato de data e hora
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        TimeZone timeZoneBrasilia = TimeZone.getTimeZone("America/Sao_Paulo");
        dateFormat.setTimeZone(timeZoneBrasilia);
        timeFormat.setTimeZone(timeZoneBrasilia);

        novaGlicemia.setData(dateFormat.format(new Date()));
        novaGlicemia.setHora(timeFormat.format(new Date()));

        novaGlicemia.setStatus(getGlicemiaStatus(glicemiaValue));
        novaGlicemia.setComentario(comentario);

        long resultado = glicemiaController.salvarGlicemia(novaGlicemia);

        if (resultado > 0) {
            Toast.makeText(this, "Registro de glicemia salvo com sucesso!", Toast.LENGTH_SHORT).show();
            textViewStatus.setText("Status: " + novaGlicemia.getStatus());
            finish();
        } else {
            Toast.makeText(this, "Erro ao salvar registro de glicemia.", Toast.LENGTH_SHORT).show();
        }
    }
}