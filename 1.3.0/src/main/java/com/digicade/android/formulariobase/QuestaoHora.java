package com.digicade.android.formulariobase;

import android.content.Context;

import com.digicade.android.formulariobase.model.Questao;

import java.text.SimpleDateFormat;

/**
 * Created by almir.santos on 19/08/2015.
 */
public class QuestaoHora extends QuestaoData{

    public QuestaoHora(Context context, Questao questao) {
        super(context, questao);
    }

    @Override
    public int dateDialogType() {
        return QuestaoData.HORA_DIALOG;
    }

    @Override
    public SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("HH:mm");
    }
}
