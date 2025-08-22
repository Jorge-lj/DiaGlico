package com.jorge.app_02.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.jorge.app_02.model.Glicemia;
import com.jorge.app_02.util.DiaGlicoDB;
import java.util.ArrayList;
import java.util.List;

public class GlicemiaController {

    private static final String TAG = "GlicemiaController";
    private final DiaGlicoDB db;

    public GlicemiaController(Context context) {
        this.db = new DiaGlicoDB(context);
    }

    /**
     * Salva um novo registro de glicemia no banco de dados.
     * @param glicemia O objeto Glicemia a ser salvo.
     * @return O ID da nova linha inserida, ou -1 em caso de erro.
     */
    public long salvarGlicemia(Glicemia glicemia) {
        ContentValues dados = new ContentValues();
        // Os nomes das colunas devem corresponder exatamente aos da tabela
        dados.put(DiaGlicoDB.KEY_USER_ID, glicemia.getUserId());
        dados.put(DiaGlicoDB.KEY_GLICEMIA, glicemia.getGlicemia());
        dados.put(DiaGlicoDB.KEY_DATA, glicemia.getData());
        dados.put(DiaGlicoDB.KEY_HORA, glicemia.getHora());
        dados.put(DiaGlicoDB.KEY_STATUS, glicemia.getStatus());
        dados.put(DiaGlicoDB.KEY_COMENTARIO, glicemia.getComentario());

        // Chama o método genérico para salvar na tabela "Glicemias"
        return db.salvarObjeto(DiaGlicoDB.TABLE_GLICEMIAS, dados);
    }

    /**
     * Busca todos os registros de glicemia para um usuário específico.
     * @param userId O ID do usuário para o qual buscar os registros.
     * @return Uma lista de objetos Glicemia.
     */
    public List<Glicemia> buscarGlicemiasPorUsuario(int userId) {
        List<Glicemia> glicemias = new ArrayList<>();
        Cursor cursor = db.buscarGlicemiasPorIdUsuario(userId);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                glicemias.add(criarGlicemiaDoCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return glicemias;
    }

    /**
     * Busca registros de glicemia com base em um filtro.
     * @param userId O ID do usuário.
     * @param filtroTipo O tipo de filtro (ex: "data", "status").
     * @param filtroValor O valor do filtro.
     * @return Uma lista de objetos Glicemia.
     */
    public List<Glicemia> buscarGlicemiasFiltradas(int userId, String filtroTipo, String filtroValor) {
        List<Glicemia> glicemias = new ArrayList<>();
        Cursor cursor = db.buscarGlicemiasFiltradas(userId, filtroTipo, filtroValor);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                glicemias.add(criarGlicemiaDoCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return glicemias;
    }

    /**
     * Remove um registro de glicemia do banco de dados.
     * @param idGlicemia O ID da glicemia a ser removida.
     * @return true se a remoção for bem-sucedida, false caso contrário.
     */
    public boolean removerGlicemia(int idGlicemia) {
        int linhasAfetadas = db.removerGlicemia(idGlicemia);
        return linhasAfetadas > 0;
    }

    /**
     * Converte um Cursor (resultado de uma busca no DB) em um objeto Glicemia.
     * @param cursor O Cursor a ser convertido.
     * @return Um objeto Glicemia preenchido.
     */
    private Glicemia criarGlicemiaDoCursor(Cursor cursor) {
        Glicemia glicemia = new Glicemia();
        try {
            glicemia.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DiaGlicoDB.KEY_ID)));
            glicemia.setGlicemia(cursor.getInt(cursor.getColumnIndexOrThrow(DiaGlicoDB.KEY_GLICEMIA)));
            glicemia.setData(cursor.getString(cursor.getColumnIndexOrThrow(DiaGlicoDB.KEY_DATA)));
            glicemia.setHora(cursor.getString(cursor.getColumnIndexOrThrow(DiaGlicoDB.KEY_HORA)));
            glicemia.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(DiaGlicoDB.KEY_STATUS)));
            glicemia.setComentario(cursor.getString(cursor.getColumnIndexOrThrow(DiaGlicoDB.KEY_COMENTARIO)));
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Erro ao obter dados do cursor. Verifique os nomes das colunas: " + e.getMessage());
        }
        return glicemia;
    }
}