package com.digicade.android.formulariobase;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.digicade.android.formulariobase.model.Questao;
import com.digicade.android.formulariobase.model.RespostaQuestao;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by almir.santos on 19/08/2015.
 */
public class QuestaoNumero extends QuestaoTexto {


    private EditText edtTextQuestao;
    public QuestaoNumero(Context context, Questao questao) {
        super(context, questao);
    }

    @Override
    protected EditText getEdtTextQuestao() {
        return edtTextQuestao;
    }

    @Override
    public View getView() {
        edtTextQuestao =  (EditText) inflate(getContext(), R.layout.view_questao_texto, null);
        edtTextQuestao.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));;
        edtTextQuestao.setInputType(InputType.TYPE_CLASS_NUMBER);
        edtTextQuestao.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    List<RespostaQuestao> resposta = new LinkedList<RespostaQuestao>();
                    RespostaQuestao respostaQuestao = getRespostaInstance();
                    String texto = ((EditText) v).getText().toString();
                    if(texto.equalsIgnoreCase(""))
                        return;
                    respostaQuestao.setRespNumero(Integer.valueOf(texto));
                    resposta.add(respostaQuestao);
                    boolean isSalvo = save(resposta);
//                    edtTextQuestao.setEnabled(!isSalvo);
                }
            }
        });
        for(RespostaQuestao rq: getQuestao().getRespostaQuestaoList()){
            String texto = rq.getRespNumero().toString();
            edtTextQuestao.setText(texto);
        }
        return edtTextQuestao;
    }

    @Override
    public void cleanRespostas() {
        edtTextQuestao.setText("");
        edtTextQuestao.setEnabled(true);
    }

    @Override
    public void validaResposta(List<?extends RespostaQuestao> rqs) throws QuestaoValidacaoException {
        super.validaResposta(rqs);
        for(RespostaQuestao rq: rqs){
            Integer numero = rq.getRespNumero();
            Integer faixaInicial = getQuestao().getFaixaIniNum();
            Integer faixaFinal = getQuestao().getFaixaFimNum();

            if(faixaInicial != null && faixaFinal != null && numero != null){
                if(!(numero >= faixaInicial && numero <= faixaFinal)){
                    throw new QuestaoValidacaoException(getContext().getString(R.string.KEY_O_NUMERO_DEVERA_ESTAR_ENTRE)+faixaInicial + getContext().getString(R.string.KEY_E)+faixaFinal+".");
                }
            }
        }
    }
}
