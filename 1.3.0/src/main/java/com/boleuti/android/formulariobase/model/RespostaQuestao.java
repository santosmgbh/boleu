/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boleuti.android.formulariobase.model;



import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author almir.santos
 */

public interface RespostaQuestao extends Serializable {

    public static final int NAO_APLICA = 77, NAO_SABE = 88, NAO_RESPONDEU = 99;



    public boolean isRemover();

    public void setIsRemover(boolean isRemover);

    public String getEntityid();

    public void setEntityid(String entityid);

    public String getRespTexto();

    public void setRespTexto(String respTexto);

    public Integer getRespNumero();

    public void setRespNumero(Integer respNumero);

    public Date getRespData();

    public void setRespData(Date respData);

    public String getRespJson();

    public void setRespJson(String respJson);

    public void setDtResposta(Date dtResposta);

    public Short getNaoAplica();

    public void setNaoAplica(Short naoAplica);


    public void setIdQuestao(Questao idQuestao);

    public ItemListaValor getIdItemListaValor();

    public void setIdItemListaValor(ItemListaValor idItemListaValor) ;

    
}
