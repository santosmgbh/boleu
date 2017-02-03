package com.digicade.android.formulariobase;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.telephony.TelephonyManager;
import android.util.Log;


import com.digicade.android.formulariobase.model.RespostaQuestao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by almir.santos on 19/08/2014.
 */
public class DBUtils {

    public static List<RespostaQuestao> fillOrReplaceEntityid(Context context, List<?extends RespostaQuestao> respostasAtuais, List<?extends RespostaQuestao> respostasNovas){
        List<RespostaQuestao> respostasIdVerificado = new ArrayList<>(respostasNovas);
        boolean containsResposta = respostasAtuais != null && !respostasAtuais.isEmpty();
        if(containsResposta){
            List<?extends RespostaQuestao> respostas = respostasAtuais;
            for(int i = 0; i < respostas.size(); i++){
                RespostaQuestao rq = respostas.get(i);
                if(rq.getEntityid() != null && !rq.getEntityid().isEmpty()){
                    respostasIdVerificado.get(i).setEntityid(rq.getEntityid());
                }else{
                    String generatedEntityid = DBUtils.generateEntityid(context);
                    respostasIdVerificado.get(i).setEntityid(generatedEntityid);
                }
            }
        }else{
            for(RespostaQuestao rq: respostasIdVerificado) {
                String generatedEntityid = DBUtils.generateEntityid(context);
                rq.setEntityid(generatedEntityid);
            }
        }
        return respostasIdVerificado;
    }


    public static String generateEntityid(Context context){
        return "100000" + System.currentTimeMillis() + getDeviceId(context) + new Random().nextInt(1000);
    }

    private static String getDeviceId(Context context){
        String serial = null;
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager.getDeviceId() != null) {
            serial = telephonyManager.getDeviceId();
        }else{
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class);
                serial = (String) get.invoke(c, "ro.serialno");
            } catch (Exception ignored) {
            }
        }
        return zeroPad(serial, 16);
    }

    private final static String zeroPad (String value, int size) {
        String s = "0000000000"+value;
        return s.substring(s.length() - size);
    }


    public static void vacuum(SQLiteDatabase db) {
        db.execSQL("VACUUM");
    }

    /**
     * Calls {@link #executeSqlScript(Context, SQLiteDatabase, String, boolean)} with transactional set to true.
     *
     * @return number of statements executed.
     */
    public static int executeSqlScript(Context context, SQLiteDatabase db, String assetFilename) throws IOException {
        return executeSqlScript(context, db, assetFilename, true);
    }

    /**
     * Executes the given SQL asset in the given database (SQL file should be UTF-8). The database file may contain
     * multiple SQL statements. Statements are split using a simple regular expression (something like
     * "semicolon before a line break"), not by analyzing the SQL syntax. This will work for many SQL files, but check
     * yours.
     *
     * @return number of statements executed.
     */
    public static int executeSqlScript(Context context, SQLiteDatabase db, String assetFilename, boolean transactional)
            throws IOException {
        byte[] bytes = readAsset(context, assetFilename);
        String sql = new String(bytes, "UTF-8");
        String[] lines = sql.split(";(\\s)*[\n\r]");
        int count;
        if (transactional) {
            count = executeSqlStatementsInTx(db, lines);
        } else {
            count = executeSqlStatements(db, lines);
        }
        Log.i("database", "Executed " + count + " statements from SQL script '" + assetFilename + "'");
        return count;
    }

    public static int executeSqlStatementsInTx(SQLiteDatabase db, String[] statements) {
        db.beginTransaction();
        try {
            int count = executeSqlStatements(db, statements);
            db.setTransactionSuccessful();
            return count;
        } finally {
            db.endTransaction();
        }
    }

    public static int executeSqlStatements(SQLiteDatabase db, String[] statements) {
        int count = 0;
        for (String line : statements) {
            line = line.trim();
            if (line.length() > 0) {
                try {
                    db.execSQL(line);
                } catch (SQLiteException e) {
                    if (!e.getMessage().contains("Duplicate") && !e.getMessage().contains("duplicate")) {
                        throw e;
                    } else {
                        Logger.getLogger("database").log(Level.WARNING, e.getMessage());
                    }
                }
                count++;
            }
        }
        return count;
    }

    /**
     * Copies all available data from in to out without closing any stream.
     *
     * @return number of bytes copied
     */
    public static int copyAllBytes(InputStream in, OutputStream out) throws IOException {
        int byteCount = 0;
        byte[] buffer = new byte[4096];
        while (true) {
            int read = in.read(buffer);
            if (read == -1) {
                break;
            }
            out.write(buffer, 0, read);
            byteCount += read;
        }
        return byteCount;
    }

    public static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copyAllBytes(in, out);
        return out.toByteArray();
    }

    public static byte[] readAsset(Context context, String filename) throws IOException {
        InputStream in = context.getResources().getAssets().open(filename);
        try {
            return readAllBytes(in);
        } finally {
            in.close();
        }
    }

    public static void logTableDump(SQLiteDatabase db, String tablename) {
        Cursor cursor = db.query(tablename, null, null, null, null, null, null);
        try {
            String dump = DatabaseUtils.dumpCursorToString(cursor);
            Log.d("database erro", "dump");
        } finally {
            cursor.close();
        }
    }



    private static List<String> getTableColumns(SQLiteDatabase db, String tableName) {
        ArrayList<String> columns = new ArrayList<String>();
        String cmd = "pragma table_info(" + tableName + ");";
        Cursor cur = db.rawQuery(cmd, null);

        while (cur.moveToNext()) {
            columns.add(cur.getString(cur.getColumnIndex("name")));
        }
        cur.close();

        return columns;
    }

}
