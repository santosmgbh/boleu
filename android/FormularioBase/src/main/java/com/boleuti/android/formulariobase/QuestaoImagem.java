package com.boleuti.android.formulariobase;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;


//import com.bumptech.glide.Glide;
import com.boleuti.android.formulariobase.model.Questao;

import java.io.FileNotFoundException;
import java.io.IOException;

public class QuestaoImagem extends BaseQuestaoView{

    private static final long serialVersionUID = 1L;

    private ImageButton mTirarFoto, mExcluirFoto;
    private ImageView mImagePreview;
    private Uri urlMedia;
    private FrameLayout layoutImagePreview;

    public QuestaoImagem(Context context, Questao questao) {
        super(context, questao);
    }

    private void buildMultimidia(){
        mTirarFoto.setVisibility(GONE);
//        try {
//            Glide.with(getContext())
//                    .load(ajustaFoto())
//                    .centerCrop()
//                    .crossFade()
//                    .into(mImagePreview);
//        } catch (PhotoSizeException e) {
//            e.printStackTrace();
//        }
    }

    private Bitmap getBitmap(){
        getContext().getContentResolver().notifyChange(urlMedia, null);
        ContentResolver cr = getContext().getContentResolver();
        Bitmap bitmap = null;
        int w = 0;
        int h = 0;
        Matrix mtx = new Matrix();

            // Ajusta orientação da imagem
            // joga a imagem em uma variável
        try {
            bitmap = MediaStore.Images.Media.getBitmap(cr, urlMedia);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    protected Bitmap ajustaFoto() throws PhotoSizeException {
        getContext().getContentResolver().notifyChange(urlMedia, null);
        ContentResolver cr = getContext().getContentResolver();
        Bitmap bitmap = null;
        int w = 0;
        int h = 0;
        Matrix mtx = new Matrix();

        try {
            // Ajusta orientação da imagem
            // joga a imagem em uma variável
            bitmap = MediaStore.Images.Media.getBitmap(cr, urlMedia);

            // captura as dimensões da imagem
            w = bitmap.getWidth();
            h = bitmap.getHeight();
            if (w == 0 || h == 0) {
                throw new PhotoSizeException();
            }
            mtx = new Matrix();

            // pega o caminho onda a imagem está salva
            ExifInterface exif = new ExifInterface(urlMedia.getPath());

            // pega a orientação real da imagem
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            // gira a imagem de acordo com a orientação
            switch (orientation) {
                case 3: // ORIENTATION_ROTATE_180
                    mtx.postRotate(180);
                    break;
                case 6: //ORIENTATION_ROTATE_90
                    mtx.postRotate(90);
                    break;
                case 8: //ORIENTATION_ROTATE_270
                    mtx.postRotate(270);
                    break;
                default: //ORIENTATION_ROTATE_0
                    mtx.postRotate(0);
                    break;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // cria variável com a imagem rotacionada
        Bitmap rotatedBmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
        BitmapDrawable bmpd = new BitmapDrawable(rotatedBmp);

        // redimensiona a imagem
        Integer lateral = 500; //256 tamanho final da dimensão maior da imagem
        try {

            // uma nova instancia do bitmap rotacionado
            Bitmap bmp = bmpd.getBitmap();

            //define um indice = 1 pois se der erro vai manter a imagem como está.
            Integer idx = 1;

            // reupera as dimensões da imagem
            w = bmp.getWidth();
            h = bmp.getHeight();

            // verifica qual a maior dimensão e divide pela lateral final para definir qual o indice de redução
            if (w >= h) {
                idx = w / lateral;
            } else {
                idx = h / lateral;
            }

            // acplica o indice de redução nas novas dimensões
            w = w / idx;
            h = h / idx;

            // cria nova instancia da imagem já redimensionada
            Bitmap bmpReduzido = Bitmap.createScaledBitmap(bmp, w, h, true);
            return bmpReduzido;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public View getView() {
//        View v = inflate(getContext(), R.layout.layout_questao_imagem, null);
//        layoutImagePreview = (FrameLayout) v.findViewById(R.id.layoutImagePreview);
//        mTirarFoto = (ImageButton) v.findViewById(R.id.btnTirarFoto);
//        mImagePreview = (ImageView) v.findViewById(R.id.imagePreview);
//        mExcluirFoto = (ImageButton) v.findViewById(R.id.deletePhoto);
//
//        mExcluirFoto.setOnClickListener(this);
//        mImagePreview.setOnClickListener(this);
//        mTirarFoto.setOnClickListener(this);
//        if(getRespostaInstance().getRespTexto() != null && !getRespostaInstance().getRespTexto().isEmpty()){
//            urlMedia = Uri.parse(getRespostaInstance().getRespTexto());
//            buildMultimidia();
//        }else{
//            layoutImagePreview.setVisibility(GONE);
//            mTirarFoto.setVisibility(VISIBLE);
//        }
//
//        return v;
        return null;
    }

    public class PhotoSizeException extends Exception {
        public PhotoSizeException() {
            super(getContext().getString(R.string.KEY_O_TAMANHO_DA_FOTO_PRECISA_SER));
        }
    }




    @Override
    public void onClick(View v) {
//        if(v.getId() == R.id.imagePreview){
//            LinearLayout ll = new LinearLayout(getContext());
//            ll.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            ImageView ivPreview = new ImageView(getContext());
//            ivPreview.setImageBitmap(getBitmap());
//            ll.addView(ivPreview);
//            AlertDialog dialogPhotoPreview = new AlertDialog.Builder(getContext()).setView(ll).create();
//            dialogPhotoPreview.show();
//        }else if(v.getId() == R.id.deletePhoto){
//            AlertDialog adb = new AlertDialog.Builder(getContext())
//                    .setMessage("Deseja realmente excluir essa imagem?")
//                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            File imagem = new File(urlMedia.getPath());
//                            if(imagem.exists())
//                                imagem.delete();
//                            mImagePreview.setImageBitmap(null);
//                            layoutImagePreview.setVisibility(GONE);
//                            mTirarFoto.setVisibility(VISIBLE);
//                            removerResposta();
//                        }
//                    })
//                    .setNegativeButton("Não", null).create();
//            adb.show();
//        }else{
//            final CameraView c = new CameraView(getContext(), System.currentTimeMillis()+"", CameraView.MEDIA_TYPE_IMAGE);
//            final AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(c).create();
//            c.setOnCameraResult(new CameraView.OnCameraResult() {
//                @Override
//                public void onCapture(String url) {
//                    Glide.with(getContext())
//                            .load(url)
//                            .centerCrop()
//                            .crossFade()
//                    .into(mImagePreview);
//                    dialog.dismiss();
//                }
//
//                @Override
//                public void onDiscard() {
//                    dialog.dismiss();
//                }
//            });
//            dialog.show();
//        }
    }

}
