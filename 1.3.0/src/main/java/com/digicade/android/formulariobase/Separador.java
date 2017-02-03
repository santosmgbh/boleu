package com.digicade.android.formulariobase;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.digicade.android.formulariobase.model.Questao;
import com.digicade.android.formulariobase.model.RespostaQuestao;

import java.util.List;

/**
 * Created by almir.santos on 02/09/2015.
 */
public class Separador extends BaseQuestaoView {

    public Separador(Context context, Questao questao) {
        super(context, questao);
        setExcluirVisible(false);
        setTxtObrigatorioVisible(false);
    }

    @Override
    public View getView() {
        View v = inflate(getContext(), R.layout.separador_view, null);
        TextView txtDescricao = (TextView) v.findViewById(R.id.txtDescricao);
        txtDescricao.setText(getQuestao().getDescricao());
        getTxtDescricao().setVisibility(GONE);
        setStatusResposta(StatusResposta.NORMAL);
        return v;
    }

    @Override
    public void validaResposta(List<?extends RespostaQuestao> rqs) {
        //Questão neutra. Não precisa de validação de resposta.
    }

    @Override
    public void cleanRespostas() {

    }

}
