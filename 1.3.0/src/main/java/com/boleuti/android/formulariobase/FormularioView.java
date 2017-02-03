package com.boleuti.android.formulariobase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.boleuti.android.formulariobase.model.Formulario;
import com.boleuti.android.formulariobase.model.Questao;
import com.boleuti.android.formulariobase.model.RespostaQuestao;
import com.boleuti.android.formulariobase.model.Topico;
import com.boleuti.android.formulariobase.nicespinner.NiceSpinner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by almir.santos on 31/07/2015.
 */
public class FormularioView extends LinearLayout implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Formulario mFormulario;
    private FragmentManager mFragmentManager;
    private Button btnAnterior;
    private Button btnProximo;
    private Button btnFinalizar;
    private int mTopicoPosicao;
    private List<Topico> mTopicos;
    private Position mPositionStatus;
    private NiceSpinner mSpnTopicos;
    private LinearLayout fragmentFormularioView;
    private LinearLayout buttonNavegacaView;
    private TextView nameTopico;
    private TextView sizeTopico;
    private TextView currentTopico;
    private boolean erroValidacao = false;
    private boolean isFinalizarEnabled = false;
    private static FormularioListener mFormularioListener = new FormularioListener() {
        @Override
        public Topico getTopico(String entityid) {
            return null;
        }

        @Override
        public void deleteResposta(RespostaQuestao rq) {

        }

        @Override
        public void addOrReplaceResposta(RespostaQuestao resposta) {

        }

        @Override
        public void onFinish(Formulario formulario) {

        }

        @Override
        public boolean onError(QuestaoValidacaoException ex) {
            return false;
        }
    };
    private static final Map<Character, Class<?extends BaseQuestaoView>> tiposQuestoesMap = new HashMap<>();

    public FormularioView(Context context) {
        this(context, null);
    }

    public FormularioView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FormularioView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initTiposQuestoes();
        View v = inflate(context, R.layout.formulario_view, null);

        btnAnterior = (Button) v.findViewById(R.id.btnAnterior);
        btnProximo = (Button) v.findViewById(R.id.btnProximo);
        nameTopico = (TextView) v.findViewById(R.id.blocoName);
        sizeTopico = (TextView) v.findViewById(R.id.blocoSize);
        currentTopico = (TextView) v.findViewById(R.id.blocoCurrent);
        btnFinalizar = (Button) v.findViewById(R.id.btnFinalizar);

        buttonNavegacaView = (LinearLayout) v.findViewById(R.id.button_navegacao);
        fragmentFormularioView = (LinearLayout) v.findViewById(R.id.fragmentFormulario);
        mSpnTopicos = (NiceSpinner) v.findViewById(R.id.spnTopicos);

        btnAnterior.setOnClickListener(this);
        btnProximo.setOnClickListener(this);
        btnFinalizar.setOnClickListener(this);

        mSpnTopicos.setOnItemSelectedListener(this);

        mSpnTopicos.setOnSelected(new NiceSpinner.OnSelected() {
            @Override
            public void onSelected(int position) {
                if(erroValidacao) {
                    mSpnTopicos.setSelectedIndex(mTopicoPosicao);
                }else {
                    mTopicoPosicao = position;
                }
            }
        });

        addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        updateUI();

    }

    private Point getDisplayInfo(){
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }


    public void montarFormulario(FragmentManager fragmentManager, Formulario formulario, @NonNull FormularioListener listener){

        mFormularioListener = listener;
        mFormulario = formulario;
        mFragmentManager = fragmentManager;
        if(mFormulario != null){
            mTopicos = Arrays.asList(mFormulario.getTopicoList().toArray(new Topico[0]));
            sizeTopico.setText(String.valueOf(mTopicos.size()));
            mSpnTopicos.attachDataSource(mTopicos);
            try {
                changeTopicoPosition(0);
                buttonsControl();
            } catch (QuestaoValidacaoException e) {
                e.printStackTrace();
            }
        }
    }


    public void setFinalizarEnable(boolean isVisible){
        isFinalizarEnabled = isVisible;
        updateUI();
    }

    public void registerTipoQuestao(char codTipoQuestao, Class<?extends BaseQuestaoView> questaoView){
        tiposQuestoesMap.put(codTipoQuestao, questaoView);
    }

    /*
        Todos os tipos padrões de questões. Para adicionar algum específico deve-se usar o método registerTipoQuestao()
     */
    private void initTiposQuestoes() {
        tiposQuestoesMap.put('R',QuestaoOpcaoUnica.class);
        tiposQuestoesMap.put('D',QuestaoData.class);
        tiposQuestoesMap.put('T',QuestaoTexto.class);
        tiposQuestoesMap.put('L',QuestaoLista.class);
        tiposQuestoesMap.put('E',QuestaoMultiplaEscolha.class);
        tiposQuestoesMap.put('H',QuestaoHora.class);
        tiposQuestoesMap.put('C',QuestaoChecado.class);
        tiposQuestoesMap.put('S',Separador.class);
        tiposQuestoesMap.put('M',QuestaoMoeda.class);
        tiposQuestoesMap.put('N',QuestaoNumero.class);
        tiposQuestoesMap.put('X',QuestaoImagem.class);
    }


    private void updateUI(){
        mSpnTopicos.setSelectedIndex(mTopicoPosicao);
        nameTopico.setText(mSpnTopicos.getText());
        currentTopico.setText(String.valueOf(mTopicoPosicao+1));
        if(mPositionStatus != null)
            buttonsControl();
        if(isFinalizarEnabled)
            btnFinalizar.setVisibility(mPositionStatus == Position.LAST || mPositionStatus == Position.UNIQUE ? VISIBLE : GONE);
    }

    private void buttonsControl(){
        btnAnterior.setAlpha(1);
        btnProximo.setAlpha(1);
        btnAnterior.setEnabled(mPositionStatus != Position.FIRST && mPositionStatus != Position.UNIQUE);
        btnProximo.setEnabled(mPositionStatus != Position.LAST && mPositionStatus != Position.UNIQUE);
        if(!btnAnterior.isEnabled() || mPositionStatus == Position.UNIQUE)
            btnAnterior.setAlpha(.2f);
        if(!btnProximo.isEnabled()|| mPositionStatus == Position.UNIQUE)
            btnProximo.setAlpha(.2f);
    }


    @Override
    public void onClick(View v) {
        try {
            callValidaRespostas();
            int i = v.getId();
            if (i == R.id.btnAnterior) {
                previousTopico();

            } else if (i == R.id.btnProximo) {
                nextTopico();

            } else if (i == R.id.btnFinalizar) {
                mFormularioListener.onFinish(mFormulario);

            }
        } catch (QuestaoValidacaoException e) {
            mFormularioListener.onError(e);
            Toast.makeText(getContext(),e.getMensagemValidacao(), Toast.LENGTH_LONG).show();
        }
        updateUI();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        try {
            changeTopicoPosition(position);
            erroValidacao = false;
        } catch (QuestaoValidacaoException e) {
            mFormularioListener.onError(e);
            erroValidacao = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    enum Position{
        UNIQUE, FIRST, NORMAL, LAST
    }


    public interface FormularioListener{
        Topico getTopico(String entityid);

        void deleteResposta(RespostaQuestao rq);

        void addOrReplaceResposta(RespostaQuestao resposta);

        void onFinish(Formulario formulario);

        public boolean onError(QuestaoValidacaoException ex);
    }

    private void nextTopico() throws QuestaoValidacaoException {
        changeTopicoPosition(mTopicoPosicao + 1);
        ++mTopicoPosicao;
    }

    private void previousTopico() throws QuestaoValidacaoException {
        changeTopicoPosition(mTopicoPosicao - 1);
        --mTopicoPosicao;
    }

    private void changeTopicoPosition(final int mPosicao) throws QuestaoValidacaoException {
        mPositionStatus = Position.FIRST;
        if(mTopicos.size() == 1){
            mPositionStatus = Position.UNIQUE;
        }else {
            if (mPosicao == mTopicos.size() - 1) {
                mPositionStatus = Position.LAST;
            } else if (mPosicao != 0) {
                mPositionStatus = Position.NORMAL;
            }
        }
        Topico topico = mTopicos.get(mPosicao);
        FragmentTopico fragmentTopico;

        Topico t = mFormularioListener.getTopico(topico.getEntityid());
        fragmentTopico = FragmentTopico.newInstance(t);
        fragmentTopico.setTopicoListener(new FragmentTopico.TopicoListener() {
            @Override
            public void onTopicoEnded() {
                btnProximo.performClick();
            }
        });
        boolean isNext = mPosicao > mTopicoPosicao;
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if(isNext){
//            transaction.setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim);//TODO: problema na versão da SDK
        }
        transaction.replace(R.id.fragmentFormulario, fragmentTopico, t.getEntityid());
        transaction.commit();
        updateUI();
    }

    public void callValidaRespostas() throws QuestaoValidacaoException {
        if (mTopicos!=null) {
            FragmentTopico fragmentTopicoAtual = (FragmentTopico) mFragmentManager.findFragmentByTag(mTopicos.get(mTopicoPosicao).getEntityid());
            if (fragmentTopicoAtual != null) {
                fragmentTopicoAtual.validaQuestoes();
            }
        }
    }


    public static class FragmentTopico extends Fragment implements OnClickListener, BaseQuestaoView.QuestaoListener {

        private static final String ARG_TOPICO = "ARG_TOPICO";

        private Topico topico;
        private TopicoListener mTopicoListener;


        public static FragmentTopico newInstance(Topico topico) {
            FragmentTopico fragment = new FragmentTopico();
            Bundle args = new Bundle();
            args.putSerializable(ARG_TOPICO, topico);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                topico = (Topico) getArguments().get(ARG_TOPICO);
            }
        }

        public FragmentTopico() {
        }

        public interface TopicoListener{
            void onTopicoEnded();
        }


        private TextView tituloTopico;
        private List<BaseQuestaoView> mQuestoesView;
        private LinearLayout mScrollQuestoes;

        public void setTopico(Topico topico) {
            this.topico = topico;
            updateUI();
        }

        public void setTopicoListener(TopicoListener mTopicoListener) {
            this.mTopicoListener = mTopicoListener;
        }

        private ScrollView mScrollView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final View v = inflater.inflate(R.layout.topico_view, null);
            tituloTopico = (TextView) v.findViewById(R.id.tituloTopico);
            mScrollView = (ScrollView) v.findViewById(R.id.scrollQuestoes);
            mScrollQuestoes = (LinearLayout) v.findViewById(R.id.layoutScrollQuestoes);

            updateUI();
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.fullScroll(View.FOCUS_UP);
                }
            });

            return v;
        }



        public void validaQuestoes() throws QuestaoValidacaoException {
            for(BaseQuestaoView bqv: mQuestoesView){
                if(bqv!=null && bqv.getVisibility() == VISIBLE) {
                    try {
                        bqv.clearFocus();
                        bqv.validacaoResposta();
                    }catch (QuestaoValidacaoException e){
                        if (bqv!=null)
                            bqv.setStatusResposta(BaseQuestaoView.StatusResposta.INVALIDO, e.getMensagemValidacao());
                        throw e;
                    }
                }
            }
        }


        private void updateUI(){
            mQuestoesView = new ArrayList<>();
            tituloTopico.setText(topico.getDescricao());

            for(int i = 0; i < topico.getQuestaoList().size(); i++){

                Questao q = topico.getQuestaoList().get(i);

                char tipoResposta = q.getIdTipoResposta().getEntityid().charAt(0);

                BaseQuestaoView questaoView = (BaseQuestaoView) mScrollQuestoes.findViewById(q.hashCode());
                if(questaoView == null){
                    questaoView = getQuestaoView(tiposQuestoesMap.get(tipoResposta), q);
                    if (questaoView!=null){
                        questaoView.setQuestaoListener(this);
                        mScrollQuestoes.addView(questaoView);
                    }

                }else {
                    questaoView.setQuestao(q);
                }
                mQuestoesView.add(questaoView);

            }
        }

        private BaseQuestaoView getQuestaoView(Class<?extends BaseQuestaoView> typeQuestaoView, Questao q) {
            if(typeQuestaoView == null){
                return new QuestaoNula(getActivity(), q);
            }
            try {
                return typeQuestaoView.getConstructor(Context.class, Questao.class).newInstance(getActivity(), q);
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        public void onClick(View v) {

        }


        @Override
        public boolean onSave(Questao  q) {
            for(int i = 0; i< q.getRespostaQuestaoList().size(); i++) {
                RespostaQuestao resposta = q.getRespostaQuestaoList().get(i);
                if(!resposta.isRemover()) {

                    resposta.setDtResposta(new Date());
                    resposta.setIdQuestao(q);

                    mFormularioListener.addOrReplaceResposta(resposta);
                }else{
                    mFormularioListener.deleteResposta(resposta);
                    q.getRespostaQuestaoList().remove(i);
                }
            }
            return true;
        }

        @Override
        public void onRemove(final BaseQuestaoView q) {
            new AlertDialog.Builder(getActivity()).
                    setTitle(R.string.KEY_REMOVER_RESPOSTA).
                    setMessage(R.string.KEY_DESEJA_REMOVER_A_RESPOSTA_DESSA_QUESTAO).
                    setPositiveButton(R.string.KEY_SIM, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            List<?extends RespostaQuestao> respostas = q.getQuestao().getRespostaQuestaoList();
                            for(int i = 0; i< respostas.size(); i++) {
                                mFormularioListener.deleteResposta(respostas.get(i));
                                respostas.remove(i);
                            }
                            q.limparRespostas();
                            Toast.makeText(getActivity().getApplicationContext(), R.string.KEY_DADOS_DE_RESPOSTA_REMOVIDO, Toast.LENGTH_SHORT).show();
                        }
                    }).
                    setNegativeButton(R.string.KEY_NAO, null).show();


        }



        @Override
        public void onSalto(Questao q1) {
            topico = mFormularioListener.getTopico(topico.getEntityid());
            for(Questao q: topico.getQuestaoList()){
                for(RespostaQuestao rq: q.getRespostaQuestaoList()){
                    if(!q.isQuestaoVisivel()){
                        mFormularioListener.deleteResposta(rq);
                    }
                }
            }
            updateUI();
        }

        @Override
        public boolean onNext(BaseQuestaoView q) {
            int idViewAtual = mQuestoesView.indexOf(q);
            int idViewProxima = (idViewAtual + 1);
            if(idViewProxima < mQuestoesView.size()-1) {
                mQuestoesView.get(idViewProxima).requestFocus();

            }

            return false;
        }

    }
}
