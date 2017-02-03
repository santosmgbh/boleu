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

public interface Topico extends Serializable {


    public String getEntityid();



    public String getDescricao();



    public List<?extends Questao> getQuestaoList();


    
}
