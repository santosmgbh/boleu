package com.boleuti.android.formulariobase;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.boleuti.android.formulariobase.R;
import com.boleuti.android.formulariobase.model.Questao;
import com.boleuti.android.formulariobase.model.RespostaQuestao;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by almir.santos on 19/08/2015.
 */
public abstract class BaseQuestaoView extends LinearLayout implements QuestaoView, View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private Questao mQuestao;
    private TextView txtDescricao;
    private TextView txtObrigatorio;
    private LinearLayout layoutQuestao;
    private ImageView btnExcluir;
    private View mMain;
    private RadioGroup mRadioNsNrNa;
    private static final int RADIO_NA = R.id.radioNA;
    private static final int RADIO_NR = R.id.radioNR;
    private static final int RADIO_NS = R.id.radioNS;

    private QuestaoListener mQuestaoListener;



    public BaseQuestaoView(Context context, Questao questao) {
        super(context, null, 0);
        mQuestao = questao;
        setId(questao.hashCode());
        View v = inflate(context, R.layout.questao_view, null);
        mMain = v.findViewById(R.id.questaoMainView);
        btnExcluir = (ImageView) mMain.findViewById(R.id.btnExcluir);
        txtDescricao = (TextView) mMain.findViewById(R.id.txtDescricao);
        txtObrigatorio = (TextView) mMain.findViewById(R.id.txtObrigatorio);
        layoutQuestao = (LinearLayout) mMain.findViewById(R.id.layoutQuestao);
        mRadioNsNrNa = (RadioGroup) mMain.findViewById(R.id.radioOpcoesNsNrNa);


        btnExcluir.setOnClickListener(this);
        mRadioNsNrNa.setOnCheckedChangeListener(this);


        layoutQuestao.addView(getView(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        setOnClickListener(getOutFocusListener());
        updateUI();

        addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void updateUI(){
        setVisibility(getQuestao().isQuestaoVisivel() ? VISIBLE : GONE);
        if (mQuestao!=null){
            if (mQuestao.getNome()!=null)
                setDescricao(Html.fromHtml(mQuestao.getNome()));
            else{
                if (mQuestao.getDescricao()!=null){
                    setDescricao(Html.fromHtml(mQuestao.getDescricao()));
                }else{
                    setDescricao(Html.fromHtml(mQuestao.getEntityid()));
                }

            }
        }

        if(getQuestao().isObrigatorio()) {
            txtObrigatorio.setVisibility(VISIBLE);
        }

        if(isRespondido()) {
            inicializaRespostas();
        }
    }

    //----MÉTODOS EXTERNOS

    public void setQuestao(Questao q){
        mQuestao = q;
        updateUI();
    }

    public void limparRespostas(){
        cleanRespostas();
        if(mRadioNsNrNa.getVisibility() == VISIBLE)
            mRadioNsNrNa.clearCheck();
        setStatusResposta(StatusResposta.NORMAL);
    }

    @Override
    public void validaResposta(List<?extends RespostaQuestao> rqs) throws QuestaoValidacaoException {
        if(rqs == null || rqs.isEmpty()){
            if(getQuestao().isObrigatorio()) {
                throw new QuestaoValidacaoException(getContext().getString(R.string.KEY_ESTA_QUESTAO_E_OBRIGATORIA));
            }else {
                return;
            }
        }
        for (RespostaQuestao rq : rqs) {
            //Tipos de resposta
            if (!isRespondido(rq)) {
                if(getQuestao().isObrigatorio()) {
                    throw new QuestaoValidacaoException(getContext().getString(R.string.KEY_ESTA_QUESTAO_E_OBRIGATORIA));
                }else{
//                    throw new QuestaoValidacaoException();
                    return;
                }
            }
        }
    }

    private boolean isRespondido(RespostaQuestao rq){
        boolean nansnr = (rq.getNaoAplica() == null || rq.getNaoAplica() < RespostaQuestao.NAO_APLICA);
        boolean respTexto = (rq.getRespTexto() == null || rq.getRespTexto().isEmpty());
        boolean respNumero = rq.getRespNumero() == null;
        boolean respData = rq.getRespData() == null;
        boolean itemListaValor = rq.getIdItemListaValor() == null;
        boolean respJson = rq.getRespJson() == null;
        return !(nansnr && respTexto && respNumero && respData && itemListaValor && respJson);
    }



    public final Questao getQuestao(){
        return mQuestao;
    }

    public void setQuestaoListener(QuestaoListener mQuestaoListener) {
        this.mQuestaoListener = mQuestaoListener;
    }




    //----MÉTODOS USADOS PELAS EXTENSÕES

    protected void inicializaRespostas(){
        setStatusResposta(StatusResposta.SALVO);
        Short naoAplica = getQuestao().getRespostaQuestaoList().get(0).getNaoAplica();
        if(naoAplica != null) {
            int idNaoAplica = 0;
            switch (naoAplica) {
                case RespostaQuestao.NAO_APLICA:
                    idNaoAplica = R.id.radioNA;
                    break;
                case RespostaQuestao.NAO_SABE:
                    idNaoAplica = R.id.radioNS;
                    break;
                case RespostaQuestao.NAO_RESPONDEU:
                    idNaoAplica = R.id.radioNR;
                    break;
            }
            if (idNaoAplica != 0)
                mRadioNsNrNa.check(idNaoAplica);
        }
    }

    protected final void setOpcoesNsNrNaVisible(boolean visible){
        mRadioNsNrNa.setVisibility(visible ? VISIBLE : GONE);
    }

    protected void cleanRespostas(){
    }

    protected final void setTxtObrigatorioVisible(boolean visible){
        txtObrigatorio.setVisibility(visible ? VISIBLE : GONE);
    }


    protected final void setExcluirVisible(boolean visible){
        btnExcluir.setVisibility(visible?VISIBLE:GONE);
    }

//    protected final boolean isNsNrNaSelected(){
//        return mRadioNsNrNa.getCheckedRadioButtonId() != -1;
//    }

    protected final boolean save(List<?extends RespostaQuestao> rqs){
        boolean respostaValida = true;
        if(mQuestaoListener != null && rqs != null) {
            try {
                getQuestao().setRespostaQuestaoList(DBUtils.fillOrReplaceEntityid(getContext(), getQuestao().getRespostaQuestaoList(), rqs));
                validaResposta(rqs);
            } catch (QuestaoValidacaoException e) {
                respostaValida = false;
                setStatusResposta(StatusResposta.INVALIDO, e.getMensagemValidacao());
            }
            if (mQuestaoListener.onSave(getQuestao())) {
                setStatusResposta(StatusResposta.SALVO);
                //deixa o componente verde indicando sucesso na gravação da resposta
            } else {
                setStatusResposta(StatusResposta.NORMAL);
                //deixa o componente vermelho indicando problema na gravação da resposta
            }
        }
        return respostaValida;
    }

    protected final boolean onNext(){
        if(mQuestaoListener != null) {
            return mQuestaoListener.onNext(this);
        }else{
            return false;
        }
    }

    protected final void salto(){
        if(mQuestaoListener != null)
            mQuestaoListener.onSalto(getQuestao());
    }

    protected final RespostaQuestao getRespostaInstance(){
        return getQuestao().getRespostaInstance();
    }

    public final void validacaoResposta() throws QuestaoValidacaoException {
        validaResposta(getQuestao().getRespostaQuestaoList());
    }

    protected final void setStatusResposta(StatusResposta statusResposta, String mensagemErro){
        View layoutErroValidacao = mMain.findViewById(R.id.layoutErroValidacao);
        switch (statusResposta){
            case SALVO:
              //  mMain.setBackgroundColor(Color.rgb(220, 235, 218));
                break;
            case NORMAL:
                mMain.setEnabled(true);
              //  mMain.setBackgroundColor(Color.WHITE);
                break;
            case INVALIDO:
                mMain.setEnabled(true);
               // mMain.setBackgroundColor(Color.WHITE);
                break;
        }
        if(mensagemErro != null && !mensagemErro.isEmpty()) {
            layoutErroValidacao.setVisibility(VISIBLE);
            TextView txtErroValidacao = (TextView) layoutErroValidacao.findViewById(R.id.txtErroValidacao);
            txtErroValidacao.setText(mensagemErro);
        }else{
            layoutErroValidacao.setVisibility(INVISIBLE);
        }

    }

    protected final void setStatusResposta(StatusResposta statusResposta){
        setStatusResposta(statusResposta, null);
    }



    protected OnClickListener getOutFocusListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };}

    protected final TextView getTxtDescricao(){
        return txtDescricao;
    }


    protected final void setDescricao(CharSequence descricao){
        txtDescricao.setText(descricao);
    }

    protected void onRadioNrNsNaSelected(boolean selected){

    }


    //----MÉTODOS INTERNOS



    private boolean isRespondido(){
        return mQuestao.getRespostaQuestaoList() != null && !mQuestao.getRespostaQuestaoList().isEmpty();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnExcluir){
            removerResposta();
        }
    }

    protected void removerResposta(){
        mQuestaoListener.onRemove(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId != -1) {
            short naoAplica = RespostaQuestao.NAO_APLICA;
            if(checkedId == RADIO_NA)
                naoAplica = RespostaQuestao.NAO_APLICA;
            else if(checkedId == RADIO_NS)
                naoAplica = RespostaQuestao.NAO_SABE;
            else if(checkedId == RADIO_NR)
                naoAplica = RespostaQuestao.NAO_RESPONDEU;

            List<RespostaQuestao> resposta = new LinkedList<>();
            if (getQuestao().getRespostaQuestaoList() != null && !getQuestao().getRespostaQuestaoList().isEmpty()) {
                RespostaQuestao rq = getQuestao().getRespostaQuestaoList().get(0);
                rq.setNaoAplica(naoAplica);
                rq.setRespTexto("");
                rq.setRespJson("");
                rq.setRespNumero(null);
                rq.setRespData(null);
                resposta.add(rq);
            } else {
                RespostaQuestao rq = getRespostaInstance();
                rq.setNaoAplica(naoAplica);
                resposta.add(rq);
            }
            save(resposta);
            onRadioNrNsNaSelected(true);
        }else{
            onRadioNrNsNaSelected(false);
        }
    }


    public enum StatusResposta{
        SALVO, NORMAL, INVALIDO
    }



    public interface QuestaoListener {
        boolean onSave(Questao q);
        void onRemove(BaseQuestaoView q);
        void onSalto(Questao q);
        boolean onNext(BaseQuestaoView q);
    }


}
