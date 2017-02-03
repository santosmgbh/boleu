package com.digicade.android.formulariobase;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.digicade.android.formulariobase.model.RespostaQuestao;

/**
 * Created by almir.santos on 01/09/2015.
 */
public class RespostaCheckBox extends CheckBox implements CompoundButton.OnCheckedChangeListener {

    private OnCheck mOnCheck;

    private RespostaQuestao respostaQuestao;

    public RespostaCheckBox(Context context) {
        super(context);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setTextAlignment(TEXT_ALIGNMENT_CENTER);
        }
        setOnCheckedChangeListener(this);
    }

    public RespostaCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setTextAlignment(TEXT_ALIGNMENT_CENTER);
        }
        setOnCheckedChangeListener(this);
    }

    public RespostaCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setTextAlignment(TEXT_ALIGNMENT_CENTER);
        }
        setOnCheckedChangeListener(this);

    }

    public void setRespostaQuestao(RespostaQuestao respostaQuestao) {
        this.respostaQuestao = respostaQuestao;
        setText(respostaQuestao.getIdItemListaValor().getDescricao());
        setId(Integer.parseInt(respostaQuestao.getIdItemListaValor().getEntityid()));
        if(respostaQuestao.getEntityid() != null && !respostaQuestao.getEntityid().isEmpty()){
            setChecked(true);
        }
    }

    public void setOnCheck(OnCheck mOnCheck) {
        this.mOnCheck = mOnCheck;
    }




    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        respostaQuestao.setIsRemover(!isChecked);
        if(!mOnCheck.onCheck(respostaQuestao, buttonView)){
            buttonView.setChecked(false);
        }
    }

    public interface OnCheck{
        boolean onCheck(RespostaQuestao respostaQuestao, CompoundButton buttonView);
    }



}
