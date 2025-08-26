package com.jorge.app_02.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jorge.app_02.model.Usuario;

public class DiaGlicoDB extends SQLiteOpenHelper {

    private static final String TAG = "DiaGlicoDB";
    private static final String DB_NAME = "diaGlico.db";
    // CORREÇÃO: Aumente a versão para forçar a recriação do banco de dados
    private static final int DB_VERSION = 4;

    public DiaGlicoDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Nomes das tabelas
    public static final String TABLE_USUARIOS = "Usuarios";
    public static final String TABLE_GLICEMIAS = "Glicemias";

    // Nomes das colunas da tabela de usuários
    public static final String KEY_ID = "id";
    public static final String KEY_NOME_COMPLETO = "nomeCompleto";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NOME_USUARIO = "nomeUsuario";
    public static final String KEY_SENHA = "senha";
    public static final String KEY_FOTO_PERFIL_URI = "fotoPerfilUri";

    // Nomes das colunas da tabela de glicemias
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_GLICEMIA = "glicemia";
    public static final String KEY_DATA = "data";
    public static final String KEY_HORA = "hora";
    public static final String KEY_STATUS = "status";
    public static final String KEY_COMENTARIO = "comentario";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_USUARIOS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USUARIOS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_NOME_COMPLETO + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_NOME_USUARIO + " TEXT UNIQUE,"
                + KEY_SENHA + " TEXT,"
                + KEY_FOTO_PERFIL_URI + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_USUARIOS_TABLE);
        Log.d(TAG, "Tabela de usuários criada com sucesso.");

        String CREATE_GLICEMIAS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_GLICEMIAS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USER_ID + " INTEGER, "
                + KEY_GLICEMIA + " INTEGER, "
                + KEY_DATA + " TEXT, "
                + KEY_HORA + " TEXT, "
                + KEY_STATUS + " TEXT,"
                + KEY_COMENTARIO + " TEXT, "
                + "FOREIGN KEY(" + KEY_USER_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + KEY_ID + ") ON DELETE CASCADE" + ")";
        sqLiteDatabase.execSQL(CREATE_GLICEMIAS_TABLE);
        Log.d(TAG, "Tabela de glicemias criada com sucesso.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // Exclui as tabelas antigas e as recria
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_GLICEMIAS);
        onCreate(sqLiteDatabase);
    }

    public long salvarObjeto(String tabela, ContentValues dados) {
        SQLiteDatabase db = this.getWritableDatabase();
        long resultado = -1;
        try {
            resultado = db.insertOrThrow(tabela, null, dados);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao salvar na tabela " + tabela + ": " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return resultado;
    }

    public Cursor autenticarUsuario(String nomeUsuario, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] colunas = {KEY_ID, KEY_NOME_USUARIO};
        String selecao = KEY_NOME_USUARIO + " = ? AND " + KEY_SENHA + " = ?";
        String[] argumentos = {nomeUsuario, senha};
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USUARIOS, colunas, selecao, argumentos, null, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao autenticar usuário: " + e.getMessage());
        }
        return cursor;
    }

    public Cursor buscarUsuarioPorNomeUsuario(String nomeUsuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selecao = KEY_NOME_USUARIO + " = ?";
        String[] argumentos = {nomeUsuario};
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_USUARIOS, null, selecao, argumentos, null, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar usuário: " + e.getMessage());
        }
        return cursor;
    }

    public int atualizarUsuario(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOME_COMPLETO, usuario.getNomeCompleto());
        values.put(KEY_EMAIL, usuario.getEmail());
        values.put(KEY_NOME_USUARIO, usuario.getNomeUsuario());
        values.put(KEY_SENHA, usuario.getSenha());
        values.put(KEY_FOTO_PERFIL_URI, usuario.getFotoPerfilUri());

        int linhasAfetadas = db.update(TABLE_USUARIOS, values, KEY_ID + " = ?", new String[]{String.valueOf(usuario.getId())});
        db.close();
        return linhasAfetadas;
    }

    public int removerUsuario(int idUsuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        int linhasAfetadas = db.delete(TABLE_USUARIOS, KEY_ID + " = ?", new String[]{String.valueOf(idUsuario)});
        db.close();
        return linhasAfetadas;
    }

    public Cursor buscarGlicemiasPorIdUsuario(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] colunas = {KEY_ID, KEY_GLICEMIA, KEY_DATA, KEY_HORA, KEY_STATUS, KEY_COMENTARIO};
        String selecao = KEY_USER_ID + " = ?";
        String[] argumentos = {String.valueOf(userId)};
        String orderBy = KEY_DATA + " DESC, " + KEY_HORA + " DESC";
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_GLICEMIAS, colunas, selecao, argumentos, null, null, orderBy);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar glicemias: " + e.getMessage());
        }
        return cursor;
    }

    public Cursor buscarGlicemiasFiltradas(int userId, String filtroTipo, String filtroValor) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] colunas = {KEY_ID, KEY_GLICEMIA, KEY_DATA, KEY_HORA, KEY_STATUS, KEY_COMENTARIO};
        String selecao = KEY_USER_ID + " = ?";
        String[] argumentos = new String[]{String.valueOf(userId)};

        if (filtroTipo != null && !filtroTipo.isEmpty()) {
            if (filtroTipo.equals("status")) {
                selecao += " AND " + KEY_STATUS + " LIKE ?";
                argumentos = new String[]{String.valueOf(userId), "%" + filtroValor + "%"};
            } else if (filtroTipo.equals("data")) {
                selecao += " AND " + KEY_DATA + " LIKE ?";
                argumentos = new String[]{String.valueOf(userId), "%" + filtroValor + "%"};
            } else if (filtroTipo.equals("valor")) {
                selecao += " AND " + KEY_GLICEMIA + " = ?";
                argumentos = new String[]{String.valueOf(userId), filtroValor};
            }
        }
        String orderBy = KEY_DATA + " DESC, " + KEY_HORA + " DESC";
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_GLICEMIAS, colunas, selecao, argumentos, null, null, orderBy);
        } catch (Exception e) {
            Log.e(TAG, "Erro ao buscar glicemias: " + e.getMessage());
            // Se houver erro, retorne um cursor nulo para evitar crashes
            return null;
        } finally {
            // O cursor é retornado, então a conexão não deve ser fechada aqui
            // O desenvolvedor que chamar a função será responsável por fechar o cursor e o db
        }
        return cursor;
    }

    public int removerGlicemia(int idGlicemia) {
        SQLiteDatabase db = this.getWritableDatabase();
        int linhasAfetadas = db.delete(TABLE_GLICEMIAS, KEY_ID + " = ?", new String[]{String.valueOf(idGlicemia)});
        db.close();
        return linhasAfetadas;
    }
}
