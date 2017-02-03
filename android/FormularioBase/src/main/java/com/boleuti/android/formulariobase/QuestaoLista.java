package com.boleuti.android.formulariobase;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import com.boleuti.android.formulariobase.model.Questao;
import com.boleuti.android.formulariobase.model.RespostaQuestao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by almir.santos on 19/08/2015.
 */
public class QuestaoLista extends BaseQuestaoView {

    private RespostaLista respostaLista;


    public QuestaoLista(Context context, Questao questao) {
        super(context, questao);
    }


    @Override
    public View getView() {

        RespostaQuestao respostaQuestao = getRespostaInstance();
        List<?extends RespostaQuestao> rqs = getQuestao().getRespostaQuestaoList();
        if(rqs != null && !rqs.isEmpty()){
            respostaQuestao = rqs.get(0);
        }
        respostaLista = new RespostaLista(getContext(), respostaQuestao);
        respostaLista.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        QuestaoAutoCompleteAdapter sa = new QuestaoAutoCompleteAdapter(getContext(), new ArrayList<>(getQuestao().getLista()));
        respostaLista.setAdapter(sa);

        respostaLista.setRespostaListaListener(new RespostaLista.RespostaListaListener() {
            @Override
            public void onSave(RespostaQuestao respostaQuestao) {
                List<RespostaQuestao> rqs = new ArrayList<RespostaQuestao>();
                rqs.add(respostaQuestao);
                boolean isSalvo = save(rqs);
                respostaLista.setEnabled(!isSalvo);
                onNext();
            }
        });


        for(RespostaQuestao rq: getQuestao().getRespostaQuestaoList()){
            if(rq.getIdItemListaValor() != null) {
                String texto = rq.getIdItemListaValor().getDescricao();
                respostaLista.setText(texto);
            }
        }

        return respostaLista;
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        respostaLista.requestFocus();
        return false;
    }



    @Override
    public void validaResposta(List<?extends RespostaQuestao> rqs) throws QuestaoValidacaoException {
        super.validaResposta(rqs);
    }

    @Override
    public void cleanRespostas() {
        respostaLista.setText(" ");
        respostaLista.setEnabled(true);
    }
}
