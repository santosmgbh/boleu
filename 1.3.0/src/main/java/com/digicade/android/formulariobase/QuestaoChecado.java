package com.digicade.android.formulariobase;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;


import com.digicade.android.formulariobase.model.Questao;
import com.digicade.android.formulariobase.model.RespostaQuestao;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by almir.santos on 19/08/2015.
 */
public class QuestaoChecado extends BaseQuestaoView {

    private CheckBox checkBox;

    public QuestaoChecado(Context context, Questao questao) {
        super(context, questao);
    }


    @Override
    public View getView() {
        checkBox = (CheckBox) inflate(getContext(), R.layout.checkbox_layout, null);
        checkBox.setText(getQuestao().getDescricao());
        checkBox.setId(Integer.parseInt(getQuestao().getEntityid()));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                List<RespostaQuestao> resposta = new LinkedList<RespostaQuestao>();
                if (getQuestao().getRespostaQuestaoList() != null && !getQuestao().getRespostaQuestaoList().isEmpty()) {
                    RespostaQuestao rq = getQuestao().getRespostaQuestaoList().get(0);
                    rq.setRespNumero(isChecked ? 1 : 0);
                    resposta.add(rq);
                } else {
                    RespostaQuestao rq = getRespostaInstance();
                    rq.setRespNumero(isChecked ? 1 : 0);
                    resposta.add(rq);
                }
                save(resposta);
            }
        });
        if(getQuestao().getRespostaQuestaoList() != null && !getQuestao().getRespostaQuestaoList().isEmpty()) {
            List<?extends RespostaQuestao> rqs = getQuestao().getRespostaQuestaoList();
            if(rqs.get(0).getRespNumero() != null)
                checkBox.setChecked(rqs.get(0).getRespNumero() == 1?true:false);
        }
        return checkBox;
    }

    @Override
    public void validaResposta(List<?extends RespostaQuestao> rqs) throws QuestaoValidacaoException {
        super.validaResposta(rqs);
    }

    @Override
    public void cleanRespostas() {
        checkBox.setChecked(false);
    }

}
