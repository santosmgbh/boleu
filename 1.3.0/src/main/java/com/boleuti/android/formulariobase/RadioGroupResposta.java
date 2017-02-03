package com.boleuti.android.formulariobase;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by almir.santos on 10/09/2015.
 */
public class RadioGroupResposta extends RadioGroup {

    List<RespostaCheckBox> mChecks = new ArrayList<>();

    public RadioGroupResposta(Context context) {
        super(context);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        mChecks.add((RespostaCheckBox) child);
    }

    public int getCountCheckeds(){
        int countCheckeds = 0;
        for(RespostaCheckBox rs: mChecks){
            if(rs.isChecked()) {
                countCheckeds++;
            }
        }
        return countCheckeds;
    }
}
