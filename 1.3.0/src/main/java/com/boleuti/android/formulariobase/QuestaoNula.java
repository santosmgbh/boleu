package com.boleuti.android.formulariobase;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.boleuti.android.formulariobase.model.Questao;

/**
 * Created by almir.santos on 19/08/2015.
 */
public class QuestaoNula extends QuestaoTexto {


    public QuestaoNula(Context context, Questao questao) {
        super(context, questao);
    }

    @Override
    public View getView() {
        EditText text = (EditText) super.getView();
        text.setEnabled(false);
        text.setHint(getContext().getString(R.string.KEY_O_TIPO)+getQuestao().getIdTipoResposta()+getContext().getString(R.string.KEY_DE_QUESTAO_E_INEXISTENTE));
        return text;
    }
}
