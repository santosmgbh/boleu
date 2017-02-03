package com.digicade.android.formulariobase;

import android.content.Context;
import android.widget.RadioButton;

import com.digicade.android.formulariobase.model.ItemListaValor;

/**
 * Created by almir.santos on 22/10/2015.
 */
public class RespostaRadioButton extends RadioButton {



    public RespostaRadioButton(Context context, ItemListaValor itemListaValor) {
        super(context, null, 0, R.layout.checkbox_layout);
        setPadding(0, 0, 0, 10);
        setTextSize(25);
        setTextAlignment(TEXT_ALIGNMENT_CENTER);
        setText(itemListaValor.getDescricao());
        setId(Integer.parseInt(itemListaValor.getEntityid()));
    }
}
