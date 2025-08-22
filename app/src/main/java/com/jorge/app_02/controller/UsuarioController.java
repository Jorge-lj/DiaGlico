package com.jorge.app_02.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.jorge.app_02.util.DiaGlicoDB;
import com.jorge.app_02.model.Usuario;

public class UsuarioController {

    private final DiaGlicoDB db;

    public UsuarioController(Context context) {
        this.db = new DiaGlicoDB(context);
    }

    public long salvarUsuario(Usuario usuario){
        ContentValues dados = new ContentValues();
        dados.put("nomeCompleto", usuario.getNomeCompleto());
        dados.put("email", usuario.getEmail());
        dados.put("nomeUsuario", usuario.getNomeUsuario());
        dados.put("senha", usuario.getSenha());
        dados.put("fotoPerfilUri", usuario.getFotoPerfilUri());

        return db.salvarObjeto("Usuarios", dados);
    }

    public boolean autenticarUsuario(String nomeUsuario, String senha) {
        Cursor cursor = db.autenticarUsuario(nomeUsuario, senha);
        boolean autenticado = false;
        if (cursor != null && cursor.moveToFirst()) {
            autenticado = true;
            cursor.close();
        }
        return autenticado;
    }

    public boolean atualizarUsuario(Usuario usuario) {
        int linhasAfetadas = db.atualizarUsuario(usuario);
        return linhasAfetadas > 0;
    }

    public boolean removerUsuario(int idUsuario) {
        int linhasAfetadas = db.removerUsuario(idUsuario);
        return linhasAfetadas > 0;
    }

    public Usuario buscarUsuarioPorNomeUsuario(String nomeUsuario) {
        Cursor cursor = db.buscarUsuarioPorNomeUsuario(nomeUsuario);
        Usuario usuario = null;
        if (cursor != null && cursor.moveToFirst()) {
            usuario = new Usuario();
            usuario.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            usuario.setNomeCompleto(cursor.getString(cursor.getColumnIndexOrThrow("nomeCompleto")));
            usuario.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            usuario.setNomeUsuario(cursor.getString(cursor.getColumnIndexOrThrow("nomeUsuario")));
            usuario.setSenha(cursor.getString(cursor.getColumnIndexOrThrow("senha")));
            usuario.setFotoPerfilUri(cursor.getString(cursor.getColumnIndexOrThrow("fotoPerfilUri")));
            cursor.close();
        }
        return usuario;
    }
}
