package com.digicade.android.formulariobase;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;

import com.digicade.android.formulariobase.model.ItemListaValor;
import com.digicade.android.formulariobase.model.Questao;
import com.digicade.android.formulariobase.model.RespostaQuestao;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by almir.santos on 19/08/2015.
 */
public class QuestaoMultiplaEscolha extends BaseQuestaoView {

    RadioGroupResposta radioGroup;

    public QuestaoMultiplaEscolha(Context context, Questao questao) {
        super(context, questao);
    }


    @Override
    public View getView() {
        //TODO implementar
        radioGroup = new RadioGroupResposta(getContext());
        for(final ItemListaValor ilv: getQuestao().getLista()){
            RespostaCheckBox checkBox = (RespostaCheckBox) inflate(getContext(), R.layout.checkbox_layout, null);//new RespostaCheckBox(getContext(), getRespostaQuestao(ilv));
            checkBox.setOnCheck(new RespostaCheckBox.OnCheck() {
                @Override
                public boolean onCheck(RespostaQuestao respostaQuestao, CompoundButton buttonView) {
                    if (radioGroup.getCountCheckeds() <= getQuestao().getNumMaxSelecoes()) {
                        List<RespostaQuestao> respostas;
                        if (getQuestao().getRespostaQuestaoList() != null && !getQuestao().getRespostaQuestaoList().isEmpty()) {
                            respostas = (List<RespostaQuestao>)getQuestao().getRespostaQuestaoList();

                            boolean igualARespostaChecada = false;
                            for (int i = 0; i < respostas.size(); i++) {
                                igualARespostaChecada = respostas.get(i).getIdItemListaValor() != null &&
                                        respostas.get(i).getIdItemListaValor().equals(respostaQuestao.getIdItemListaValor());
                                if (igualARespostaChecada) {//Se a resposta for igual, só atualiza o item da lista de respostas.
                                    respostas.set(i, respostaQuestao);
                                    igualARespostaChecada = true;
                                    break;
                                }
                            }
                            if (!igualARespostaChecada) {//Caso não encontre uma resposta com esse itemlistavalor, então, será adicionada uma nova.
                                respostas.add(respostaQuestao);
                            }
                        } else {
                            respostas = new LinkedList<RespostaQuestao>();
                            respostas.add(respostaQuestao);
                        }
                        boolean isSalvo = save(respostas);
                        radioGroup.setEnabled(!isSalvo);

                        //PÓS validação
                        validarLimiteSelecao(respostas);
                        salto();
                        onNext();
                        return true;
                    } else {
                        setStatusResposta(StatusResposta.SALVO, getContext().getString(R.string.KEY_O_NUMERO_MAXIMO_DE_SELECOES_EH) + getQuestao().getNumMaxSelecoes());
                        return false;
                    }
                }
            });
            checkBox.setRespostaQuestao(getRespostaQuestao(ilv));
            radioGroup.addView(checkBox);
        }
        return radioGroup;
    }

    @Override
    public void cleanRespostas() {
        for(RespostaCheckBox rcb: radioGroup.mChecks){
            rcb.setChecked(false);
        }
        radioGroup.setEnabled(true);
    }

    private void validarLimiteSelecao(List<?extends RespostaQuestao> respostas){
        boolean todasRemovidas = true;
        for(RespostaQuestao rq: respostas){
            if(!rq.isRemover()) {
                todasRemovidas = false;
                break;
            }
        }
        if (respostas == null || respostas.isEmpty() || todasRemovidas) {
            if (getQuestao().isObrigatorio()) {
                setStatusResposta(StatusResposta.INVALIDO, getContext().getString(R.string.KEY_ESTA_QUESTAO_E_OBRIGATORIA));
            } else {
                setStatusResposta(StatusResposta.NORMAL, "");
            }
        }
    }


    private RespostaQuestao getRespostaQuestao(ItemListaValor ilv){
        if(getQuestao().getRespostaQuestaoList() != null && !getQuestao().getRespostaQuestaoList().isEmpty()) {
            for(RespostaQuestao rq: getQuestao().getRespostaQuestaoList()) {
                ItemListaValor idItemListaValor = rq.getIdItemListaValor();
                if (idItemListaValor != null) {
                    if(idItemListaValor.equals(ilv)){
                        return rq;
                    }
                }
            }
        }
        RespostaQuestao rq = getRespostaInstance();
        rq.setIdItemListaValor(ilv);
        return rq;
    }
}
