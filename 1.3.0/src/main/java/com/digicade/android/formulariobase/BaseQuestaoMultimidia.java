package com.digicade.android.formulariobase;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.digicade.android.formulariobase.model.Questao;

/**
 * Created by almir.santos on 14/03/2016.
 */
public abstract class BaseQuestaoMultimidia extends BaseQuestaoView {


    public BaseQuestaoMultimidia(Context context, Questao questao) {
        super(context, questao);
    }


    public abstract void setResult(Intent intent);

}
