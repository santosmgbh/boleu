package com.digicade.android.formulariobase;

/**
 * Created by almir.santos on 09/09/2015.
 */
public class QuestaoValidacaoException extends Exception {

    private String mensagemValidacao;

    public QuestaoValidacaoException() {
        this("");
    }

    public QuestaoValidacaoException(String mensagemValidacao) {
        super(mensagemValidacao);
        this.mensagemValidacao = mensagemValidacao;
    }

    public String getMensagemValidacao() {
        return mensagemValidacao;
    }
}
