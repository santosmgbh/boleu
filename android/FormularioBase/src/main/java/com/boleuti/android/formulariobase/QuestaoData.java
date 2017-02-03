package com.boleuti.android.formulariobase;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.boleuti.android.formulariobase.model.Questao;
import com.boleuti.android.formulariobase.model.RespostaQuestao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by almir.santos on 19/08/2015.
 */
public class QuestaoData  extends BaseQuestaoView{

    public static final int DATE_DIALOG = 0;
    public static final int HORA_DIALOG = 2;
    private EditText edtTextQuestao;

    public QuestaoData(Context context, Questao questao) {
        super(context, questao);
    }


    @Override
    public View getView() {
        edtTextQuestao = (EditText) inflate(getContext(), R.layout.view_questao_texto, null);
        edtTextQuestao.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        edtTextQuestao.setEnabled(false);
//        edtTextQuestao.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onCreateDialog((EditText) v, dateDialogType()).show();
//            }
//        });
        edtTextQuestao.setKeyListener(null);
        edtTextQuestao.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    onCreateDialog((EditText) v, dateDialogType()).show();
                }
            }
        });
        for(RespostaQuestao rq: getQuestao().getRespostaQuestaoList()){
            Date data = rq.getRespData();
            if(data != null) {
                edtTextQuestao.setText(getDateFormat().format(data));
            }

        }
        return edtTextQuestao;
    }

    public SimpleDateFormat getDateFormat(){
        return new SimpleDateFormat("dd/MM/yyyy");
    }

    public int dateDialogType(){
        return DATE_DIALOG;
    }

    @Override
    public void validaResposta(List<?extends RespostaQuestao> rqs) throws QuestaoValidacaoException {
        super.validaResposta(rqs);
    }

    @Override
    public void cleanRespostas() {
        edtTextQuestao.setText("");
    }

    public Dialog onCreateDialog(final EditText edtTextTemp, int id) {
        String dataGetDialog = "";
        String VAZIO = "";
        Calendar calendario = Calendar.getInstance();
        final Calendar dateAndTime = Calendar.getInstance();
        int _ano = calendario.get(Calendar.YEAR);
        int _mes = calendario.get(Calendar.MONTH);
        int _dia = calendario.get(Calendar.DAY_OF_MONTH);
        switch (id) {
            case DATE_DIALOG:
                dataGetDialog = edtTextTemp.getText().toString();
                if (!dataGetDialog.equals(VAZIO)) {
                    Integer[] arrData = getArrDateFormatada(dataGetDialog);
                    _dia = arrData[0];
                    _mes = arrData[1] - 1;
                    _ano = arrData[2];
                }
                //retorno da data j� formatando
                DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int _ano, int _mes, int _dia) {
                                String data = String.valueOf(100 + _dia).substring(1) + "/" + String.valueOf(100 + (_mes + 1)).substring(1) + "/" + String.valueOf(10000 + _ano).substring(1);
                                edtTextTemp.setText(data);
                                saveDate(dateAndTime.getTime());
                                onNext();
                            }
                        }, _ano, _mes, _dia);
                datePicker.setTitle(getContext().getString(R.string.KEY_SELECIONE_UMA_DATA));
                datePicker.setCancelable(false);
                datePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        saveDate(null);
                        edtTextTemp.setText("");
                        Toast.makeText(getContext(), R.string.KEY_DATA_CANCELADA, Toast.LENGTH_SHORT).show();
                    }
                });
                return datePicker;

            case HORA_DIALOG:
                dataGetDialog = edtTextTemp.getText().toString();
                int _horas = 8;
                int _minutos = 0;
                if (!dataGetDialog.equals(VAZIO)) {
                    _horas = Integer.valueOf(dataGetDialog.split(":")[0].toString());
                    _minutos = Integer.valueOf(dataGetDialog.split(":")[1].toString());
                }

                TimePickerDialog timePicker = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            public void onTimeSet(TimePicker view, int _horas,
                                                  int _minutos) {
                                dateAndTime.set(Calendar.HOUR_OF_DAY, _horas);
                                dateAndTime.set(Calendar.MINUTE, _minutos);
                                String hora = String.valueOf(100 + _horas).substring(1) + ":" + String.valueOf(100 + (_minutos)).substring(1);
                                edtTextTemp.setText(hora);
                                saveDate(dateAndTime.getTime());
                                onNext();
                            }
                        }, _horas, _minutos, true);
                timePicker.setTitle(getContext().getString(R.string.KEY_SELECIONE_UMA_HORA));
                timePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        saveDate(null);
                        edtTextTemp.setText("");
                        Toast.makeText(getContext(), R.string.KEY_HORA_CANCELADA, Toast.LENGTH_SHORT).show();
                    }
                });

                return timePicker;
        }
        return null;
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        edtTextQuestao.requestFocus();
        return false;
    }

    private void saveDate(Date data){
        List<RespostaQuestao> resposta = new LinkedList<RespostaQuestao>();
        RespostaQuestao rq = getRespostaInstance();
        rq.setRespData(data);
        resposta.add(rq);
        save(resposta);
    }

    public Integer[] getArrDateFormatada(String dataFormatoPtBr) {
        try {
            String[] arrData = dataFormatoPtBr.split("/");
            int dia = Integer.parseInt(arrData[0]);
            int mes = Integer.parseInt(arrData[1]);
            int ano = Integer.parseInt(arrData[2]);
            return new Integer[]{dia, mes, ano};
        } catch (Exception e) {
            return new Integer[]{1, 1, 2014};
        }
    }

}
