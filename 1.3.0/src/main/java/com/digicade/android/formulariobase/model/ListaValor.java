/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digicade.android.formulariobase.model;


import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 *
 * @author almir.santos
 */

public interface ListaValor extends Serializable {


    public Collection<?extends ItemListaValor> getItemListaValorList();


    
}
