package com.jorge.app_02.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jorge.app_02.R;
import com.jorge.app_02.controller.UsuarioController;
import com.jorge.app_02.model.Usuario;
import com.jorge.app_02.util.SessionManager; // Importar a nova classe

public class LoginActivity extends AppCompatActivity {

    private EditText nomeUsuarioEditText, senhaEditText;
    private Button btnLogin;
    private TextView textCadastro, btnLimparLogin;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializa o SessionManager
        sessionManager = new SessionManager(this);

        // Inicializa as views
        initViews();

        // Configura os listeners
        setupListeners();
    }

    private void initViews() {
        nomeUsuarioEditText = findViewById(R.id.nome_usuario_login);
        senhaEditText = findViewById(R.id.senha_login);
        btnLogin = findViewById(R.id.btn_login);
        textCadastro = findViewById(R.id.text_cadastro);
        btnLimparLogin = findViewById(R.id.btn_limpar_login);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
        textCadastro.setOnClickListener(v -> navigateToCadastro());
        btnLimparLogin.setOnClickListener(v -> clearLogin());
    }

    private void handleLogin() {
        String nomeUsuario = nomeUsuarioEditText.getText().toString();
        String senha = senhaEditText.getText().toString();

        if (nomeUsuario.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        UsuarioController controller = new UsuarioController(this);
        boolean autenticado = controller.autenticarUsuario(nomeUsuario, senha);

        if (autenticado) {
            Usuario usuarioLogado = controller.buscarUsuarioPorNomeUsuario(nomeUsuario);
            if (usuarioLogado != null) {
                // Usa o SessionManager para salvar a sessão de forma segura
                sessionManager.saveLoginSession(nomeUsuario, usuarioLogado.getId());
                Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                navigateToMain();
            } else {
                Toast.makeText(this, "Erro ao buscar dados do usuário.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Nome de usuário ou senha incorretos.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToCadastro() {
        Intent intent = new Intent(this, CadastroActivity.class);
        startActivity(intent);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void clearLogin() {
        sessionManager.clearLoginSession();
        nomeUsuarioEditText.setText("");
        senhaEditText.setText("");
        Toast.makeText(this, "Dados de login limpos.", Toast.LENGTH_SHORT).show();
    }
}