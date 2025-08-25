package com.jorge.app_02.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.jorge.app_02.R;
import com.jorge.app_02.controller.UsuarioController;
import com.jorge.app_02.model.Usuario;
import com.jorge.app_02.util.ImagemSalva;

import java.io.FileDescriptor;
import java.io.IOException;

public class PerfilUsuarioActivity extends AppCompatActivity {

    private static final String TAG = "PerfilDebug";

    private EditText nomeCompletoEditText, emailEditText, nomeUsuarioEditText, senhaEditText;
    private Button btnSalvarAlteracoes, btnExcluirConta, btnSalvarFotoPerfilGaleria;
    private ImageView imageViewFotoPerfil;

    private UsuarioController usuariosController;
    private Usuario usuarioAtual;
    private Uri selectedImageUri;

    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_LOGGED_IN_USERNAME = "loggedInUsername";

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        usuariosController = new UsuarioController(this);

        nomeCompletoEditText = findViewById(R.id.nome_completo_perfil);
        emailEditText = findViewById(R.id.email_perfil);
        nomeUsuarioEditText = findViewById(R.id.nome_usuario_perfil);
        senhaEditText = findViewById(R.id.senha_perfil);
        btnSalvarAlteracoes = findViewById(R.id.btn_salvar_alteracoes_perfil);
        btnExcluirConta = findViewById(R.id.btn_excluir_conta);
        imageViewFotoPerfil = findViewById(R.id.imageView_foto_perfil);
        btnSalvarFotoPerfilGaleria = findViewById(R.id.btn_salvar_foto_perfil_galeria);

        setupLaunchers();
        carregarUsuarioLogado();

        imageViewFotoPerfil.setOnClickListener(v -> verificarPermissaoGaleria());
        btnSalvarAlteracoes.setOnClickListener(v -> salvarAlteracoes());
        btnExcluirConta.setOnClickListener(v -> confirmarExclusaoConta());
        btnSalvarFotoPerfilGaleria.setOnClickListener(v -> salvarImagemGaleria());
    }

    @SuppressLint("WrongConstant")
    private void setupLaunchers() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) openImagePicker();
                    else Toast.makeText(this, "Permissão negada.", Toast.LENGTH_SHORT).show();
                });

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            selectedImageUri = uri;
                            imageViewFotoPerfil.setImageURI(selectedImageUri);
                            final int takeFlags = result.getData().getFlags()
                                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            try {
                                getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
                            } catch (SecurityException e) {
                                Log.e(TAG, "Falha ao persistir URI: " + e.getMessage());
                            }
                        }
                    }
                });
    }

    private void carregarUsuarioLogado() {
        String loggedInUsername = getLoggedInUsername();
        if (loggedInUsername != null) {
            usuarioAtual = usuariosController.buscarUsuarioPorNomeUsuario(loggedInUsername);
            if (usuarioAtual != null) preencherCampos(usuarioAtual);
            else clearLoginCredentialsAndRedirectToLogin();
        } else clearLoginCredentialsAndRedirectToLogin();
    }

    private String getLoggedInUsername() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString(KEY_LOGGED_IN_USERNAME, null);
    }

    private void preencherCampos(Usuario usuario) {
        nomeCompletoEditText.setText(usuario.getNomeCompleto());
        emailEditText.setText(usuario.getEmail());
        nomeUsuarioEditText.setText(usuario.getNomeUsuario());
        senhaEditText.setText("");
        if (usuario.getFotoPerfilUri() != null && !usuario.getFotoPerfilUri().isEmpty()) {
            selectedImageUri = Uri.parse(usuario.getFotoPerfilUri());
            imageViewFotoPerfil.setImageURI(selectedImageUri);
        } else {
            imageViewFotoPerfil.setImageResource(R.drawable.placeholder_profile_picture);
        }
    }

    private void verificarPermissaoGaleria() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_IMAGES
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            openImagePicker();
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void salvarAlteracoes() {
        if (usuarioAtual == null) return;

        String nome = nomeCompletoEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String usuario = nomeUsuarioEditText.getText().toString().trim();
        String senha = senhaEditText.getText().toString().trim();

        if (nome.isEmpty() || email.isEmpty() || usuario.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }

        usuarioAtual.setNomeCompleto(nome);
        usuarioAtual.setEmail(email);
        usuarioAtual.setNomeUsuario(usuario);
        if (!senha.isEmpty()) usuarioAtual.setSenha(senha);

        usuarioAtual.setFotoPerfilUri(selectedImageUri != null ? selectedImageUri.toString() : "");

        boolean atualizado = usuariosController.atualizarUsuario(usuarioAtual);
        if (atualizado) {
            Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Erro ao atualizar perfil.", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarImagemGaleria() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Nenhuma imagem selecionada.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(selectedImageUri, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd);
            pfd.close();

            if (bitmap != null) {
                String filename = "perfil_" + System.currentTimeMillis() + ".jpg";
                ImagemSalva.saveBitmapToGallery(this, bitmap, filename);
                Toast.makeText(this, "Imagem salva na galeria!", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(this, "Erro ao processar bitmap.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao salvar imagem: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarExclusaoConta() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Exclusão")
                .setMessage("Deseja excluir sua conta? Esta ação é irreversível.")
                .setPositiveButton("Sim", (dialog, which) -> excluirConta())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void excluirConta() {
        if (usuarioAtual != null) {
            boolean removido = usuariosController.removerUsuario(usuarioAtual.getId());
            if (removido) clearLoginCredentialsAndRedirectToLogin();
            else Toast.makeText(this, "Erro ao excluir conta.", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearLoginCredentialsAndRedirectToLogin() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        settings.edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
