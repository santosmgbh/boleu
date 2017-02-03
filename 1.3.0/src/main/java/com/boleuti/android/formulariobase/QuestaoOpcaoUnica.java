package com.boleuti.android.formulariobase;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.boleuti.android.formulariobase.model.ItemListaValor;
import com.boleuti.android.formulariobase.model.Questao;
import com.boleuti.android.formulariobase.model.RespostaQuestao;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by almir.santos on 19/08/2015.
 */
public class QuestaoOpcaoUnica extends BaseQuestaoView {

    private RadioGroup radioGroup;

    public QuestaoOpcaoUnica(Context context, Questao questao) {
        super(context, questao);
    }



    @Override
    public View getView() {
        radioGroup = new RadioGroup(getContext());
        for(final ItemListaValor ilv: getQuestao().getLista()){
            final RadioButton radio = (RadioButton) inflate(getContext(), R.layout.radiobutton_layout, null);
            radio.setText(ilv.getDescricao());
            radio.setId(Integer.parseInt(ilv.getEntityid()));
            radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        List<RespostaQuestao> resposta = new LinkedList<RespostaQuestao>();
                        RespostaQuestao respostaQuestao = getRespostaInstance();
                        respostaQuestao.setIdItemListaValor(ilv);
                        resposta.add(respostaQuestao);
                        boolean isSalvo = save(resposta);
                        radioGroup.setEnabled(!isSalvo);
                        salto();
                        onNext();
                    }
                }
            });
            if(isChecked(ilv)) {
                radio.setChecked(true);
            }
            radioGroup.addView(radio);
        }
        return radioGroup;
    }

    @Override
    public void cleanRespostas() {
        radioGroup.clearCheck();
        radioGroup.setEnabled(true);
    }

    @Override
    public void validaResposta(List<?extends RespostaQuestao> rqs) throws QuestaoValidacaoException {
        super.validaResposta(rqs);
    }

    private boolean isChecked(ItemListaValor ilv){
        if(getQuestao().getRespostaQuestaoList() != null && !getQuestao().getRespostaQuestaoList().isEmpty()) {
            ItemListaValor idItemListaValor = getQuestao().getRespostaQuestaoList().iterator().next().getIdItemListaValor();
            if (idItemListaValor != null) {
                return idItemListaValor.equals(ilv);
            } else {
                return false;
            }
        }
        return false;
    }
}
