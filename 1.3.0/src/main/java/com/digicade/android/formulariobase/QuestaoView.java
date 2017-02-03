package com.digicade.android.formulariobase;

import android.view.View;

import com.digicade.android.formulariobase.model.RespostaQuestao;

import java.util.List;

/**
 * Created by almir.santos on 19/08/2015.
 */
public interface QuestaoView{

    View getView();

    /**
     *  Lance a exceção QuestaoValidacaoException passando a mensagem de erro caso o tenha.
     * @param rqs
     * @throws QuestaoValidacaoException
     */
    void validaResposta(List<? extends RespostaQuestao> rqs) throws QuestaoValidacaoException;


}
