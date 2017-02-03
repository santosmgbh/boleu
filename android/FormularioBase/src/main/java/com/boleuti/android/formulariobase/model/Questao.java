/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boleuti.android.formulariobase.model;


import java.io.Serializable;
import java.util.List;


/**
 *
 * @author almir.santos
 */

public interface Questao extends Serializable {




    public String getEntityid();

    public String getDescricao() ;

    public boolean isObrigatorio();

    public boolean isQuestaoVisivel();

    public String getMascara();

    public String getNome();


    public Integer getFaixaIniNum() ;


    public Integer getFaixaFimNum();


    public Integer getNumMaxSelecoes();


    public List<?extends RespostaQuestao> getRespostaQuestaoList();

    public void setRespostaQuestaoList(List<? extends RespostaQuestao> respostaQuestaoList);

    public TipoResposta getIdTipoResposta();

    public RespostaQuestao getRespostaInstance();


    public List<?extends ItemListaValor> getLista() ;
}
