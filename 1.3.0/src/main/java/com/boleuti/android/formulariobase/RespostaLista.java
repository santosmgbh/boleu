package com.boleuti.android.formulariobase;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.boleuti.android.formulariobase.model.ItemListaValor;
import com.boleuti.android.formulariobase.model.RespostaQuestao;

import java.util.LinkedList;
import java.util.List;

public class RespostaLista extends LinearLayout implements View.OnClickListener {

    private static final long serialVersionUID = 1L;
    boolean isFirstTurn = true;
    private RespostaQuestao mRespostaQuestao;
    private AutoCompleteTextView autoComplete;
    private Button btnPesquisar;
    private int mListSelection = -1;
    private RespostaListaListener mRespostaListaListener = new RespostaListaListener() {
        @Override
        public void onSave(RespostaQuestao respostaQuestao) {

        }
    };

    public RespostaLista(Context context, RespostaQuestao respostaQuestao) {
        super(context);
        View v = inflate(context, R.layout.resposta_lista, null);
        btnPesquisar = (Button) v.findViewById(R.id.btnPesquisar);
        btnPesquisar.setOnClickListener(this);

        autoComplete = (AutoCompleteTextView) v.findViewById(R.id.autoComplete);
        autoComplete.setTextColor(Color.BLACK);

        autoComplete.setSelectAllOnFocus(true);
        autoComplete.addTextChangedListener(getTextChangeListener());
        autoComplete.setOnItemClickListener(getItemClickListener());
        autoComplete.setOnFocusChangeListener(getFocusChange());

        mRespostaQuestao = respostaQuestao;

        if(mRespostaQuestao != null){
            if(mRespostaQuestao.getIdItemListaValor() != null) {
                autoComplete.setText(mRespostaQuestao.getIdItemListaValor().getDescricao());
                autoComplete.clearFocus();
                setEnabled(false);
            }
        }

        addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        autoComplete.requestFocus();
        return false;
    }

    public void setRespostaListaListener(RespostaListaListener mRespostaListaListener) {
        this.mRespostaListaListener = mRespostaListaListener;
    }

    public void setEnabled(boolean enabled) {
        autoComplete.setEnabled(enabled);
        btnPesquisar.setEnabled(enabled);
    }

    private void save() {
        String pesquisa = getText().toString().trim();
        if (pesquisa.isEmpty()) {
            setText(" ");
        }
        QuestaoAutoCompleteAdapter aa = getAdapter();
        if (aa != null) {

            List<RespostaQuestao> resposta = new LinkedList<RespostaQuestao>();
            RespostaQuestao respostaQuestao = mRespostaQuestao;
//            respostaQuestao.setIsRemover(true);

            if (mListSelection != ListView.INVALID_POSITION &&
                    !autoComplete.getText().toString().isEmpty()
                    && !autoComplete.getText().toString().equals(" ")) {
                ItemListaValor itemListaValor = aa.getItemListaValor(pesquisa);
                respostaQuestao.setIdItemListaValor(itemListaValor);
                resposta.add(respostaQuestao);

                setText(itemListaValor.getDescricao());
                setRespostaQuestao(respostaQuestao);
                respostaQuestao.setIsRemover(false);

            }
            mRespostaListaListener.onSave(respostaQuestao);
        }
    }

    @NonNull
    private OnFocusChangeListener getFocusChange() {
        return new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    save();
                }
            }
        };
    }

    @NonNull
    private AdapterView.OnItemClickListener getItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!adapterView.getAdapter().isEmpty()) {
                    ItemListaValor ilv = (ItemListaValor) adapterView.getAdapter().getItem(i);
                    if (ilv != null) {
                        mListSelection = i;
                        autoComplete.setText(ilv.getDescricao());
                        save();
                    }
                } else {
                    autoComplete.setText(" ");
                }
            }
        };
    }

    @NonNull
    private TextWatcher getTextChangeListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ListAdapter la = autoComplete.getAdapter();
                if (la != null) {
                    if(!isFirstTurn) {
                        autoComplete.setCompletionHint("(" + la.getCount() + getContext().getString(R.string.KEY_REGISTROS_ENCONTRADOS));
                        autoComplete.showDropDown();
                        isFirstTurn = false;
                    }
                }
            }
        };
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.btnPesquisar) {
            String pesquisa = autoComplete.getText().toString().trim();
            if (pesquisa.isEmpty()) {
                autoComplete.setText(" ");
            }
            final QuestaoAutoCompleteAdapter aa = ((QuestaoAutoCompleteAdapter) autoComplete.getAdapter());
            if (aa != null) {
                aa.setOnFilterChange(new QuestaoAutoCompleteAdapter.OnFilterChange() {
                    @Override
                    public void onFilterChange(int count) {
                        autoComplete.setCompletionHint("(" + count + getContext().getString(R.string.KEY_REGISTROS_ENCONTRADOS));
                        autoComplete.showDropDown();
                    }
                });
                aa.getFilter().filter(pesquisa, new Filter.FilterListener() {
                    @Override
                    public void onFilterComplete(int i) {
                        autoComplete.setCompletionHint("(" + i + getContext().getString(R.string.KEY_REGISTROS_ENCONTRADOS));
                        autoComplete.showDropDown();
                    }
                });
            }

        }

    }

    public Editable getText() {
        return autoComplete.getText();
    }

    public void setText(CharSequence charSequence) {
        autoComplete.setText(charSequence);
    }

    public void setCompletionHint(CharSequence completionHint) {
        autoComplete.setCompletionHint(completionHint);
    }

    public void showDropDown() {
        autoComplete.showDropDown();
    }

    public QuestaoAutoCompleteAdapter getAdapter() {
        return (QuestaoAutoCompleteAdapter) autoComplete.getAdapter();
    }

    public <T extends ListAdapter & Filterable> void setAdapter(T adapter) {
        autoComplete.setAdapter(adapter);
    }

    private void setRespostaQuestao(RespostaQuestao respostaQuestao) {
        this.mRespostaQuestao = respostaQuestao;
    }


    public interface RespostaListaListener {
        void onSave(RespostaQuestao respostaQuestao);
    }
}
