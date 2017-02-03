package com.digicade.android.formulariobase;

import android.content.Context;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.digicade.android.formulariobase.model.Questao;
import com.digicade.android.formulariobase.model.RespostaQuestao;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by almir.santos on 19/08/2015.
 */
public class QuestaoTexto extends BaseQuestaoView {


    EditText mEdtTextQuestao;


    public QuestaoTexto(Context context, Questao questao) {
        super(context, questao);
    }



    protected EditText getEdtTextQuestao(){
        return mEdtTextQuestao;
    }

    @Override
    public View getView() {
        mEdtTextQuestao = (EditText) inflate(getContext(), R.layout.view_questao_texto, null);
        mEdtTextQuestao.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        String mascara = getQuestao().getMascara();
        if(mascara != null) {
            mEdtTextQuestao.addTextChangedListener(Mask.insert(mascara, mEdtTextQuestao));
            mEdtTextQuestao.setHint(mascara.replace("#", "_").replace("9", "_"));
        }
        mEdtTextQuestao.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    saveText(mEdtTextQuestao);
                }
            }
        });
        for(RespostaQuestao rq: getQuestao().getRespostaQuestaoList()){
            String texto = rq.getRespTexto();
            mEdtTextQuestao.setText(texto);
        }
        getEdtTextQuestao().setImeActionLabel(getContext().getString(R.string.KEY_PROX), EditorInfo.IME_ACTION_NEXT);
        getEdtTextQuestao().setImeOptions(EditorInfo.IME_ACTION_NEXT);
        getEdtTextQuestao().setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode) {
                    onNext();
                }
                return false;
            }
        });
        return mEdtTextQuestao;
    }

    @Override
    protected void onRadioNrNsNaSelected(boolean selected) {
        if(getEdtTextQuestao() != null) {
            getEdtTextQuestao().setEnabled(!selected);
            if (selected)
                getEdtTextQuestao().setText("");
        }
    }

    @Override
    protected OnClickListener getOutFocusListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(!isNsNrNaSelected()) {
//                    saveText(getEdtTextQuestao());
//                }
            }
        };
    }

    private void saveText(View v){
        List<RespostaQuestao> resposta = new LinkedList<RespostaQuestao>();
        RespostaQuestao respostaQuestao = getRespostaInstance();
        if(getQuestao().getRespostaQuestaoList() != null && !getQuestao().getRespostaQuestaoList().isEmpty()){
            respostaQuestao = getQuestao().getRespostaQuestaoList().get(0);
        }
        String texto = ((EditText) v).getText().toString();
        respostaQuestao.setRespTexto(texto);
        respostaQuestao.setNaoAplica((short)0);
        if(texto.isEmpty()){
            respostaQuestao.setIsRemover(true);
        }
        resposta.add(respostaQuestao);
        boolean isSalvo = save(resposta);
//        mEdtTextQuestao.setEnabled(!isSalvo);

        for(RespostaQuestao rq: resposta){
            if((rq.getRespTexto() == null || rq.getRespTexto().isEmpty()) &&
                    (rq.getNaoAplica() == null || rq.getNaoAplica() < RespostaQuestao.NAO_APLICA)){
                if(getQuestao().isObrigatorio()) {
                    setStatusResposta(StatusResposta.INVALIDO, getContext().getString(R.string.KEY_ESTA_QUESTAO_E_OBRIGATORIA));
//                    mEdtTextQuestao.setEnabled(true);
                }else{
                    setStatusResposta(StatusResposta.NORMAL, "");
                }
            }
        }
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        getEdtTextQuestao().requestFocus();
        return false;
    }

    @Override
    public void validaResposta(List<?extends RespostaQuestao> rqs) throws QuestaoValidacaoException {
        super.validaResposta(rqs);
    }

    @Override
    public void cleanRespostas() {
        getEdtTextQuestao().setText("");
        mEdtTextQuestao.setEnabled(true);
    }
}
