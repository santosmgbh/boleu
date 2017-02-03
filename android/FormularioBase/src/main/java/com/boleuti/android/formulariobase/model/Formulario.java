/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.boleuti.android.formulariobase.model;


import java.io.Serializable;
import java.util.Collection;


/**
 *
 * @author almir.santos
 */

public interface Formulario extends Serializable {



    public Collection<?extends Topico> getTopicoList();


    
}
