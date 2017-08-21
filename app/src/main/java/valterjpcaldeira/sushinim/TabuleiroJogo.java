package valterjpcaldeira.sushinim;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.LinkedList;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * TODO: document your custom view class.
 */
public class TabuleiroJogo extends View {
    private Tabuleiro tabuleiro;
    private Activity atividade = null;
    private enum Estado {
        JOGADOR_A_JOGAR, IA_A_JOGAR, IA_A_PENSAR,
        FIM_DE_JOGO
    };
    private Estado estado = Estado.JOGADOR_A_JOGAR;

    private Paint paint;
    private int dimQuadradoLinha;
    private int dimQuadradoCol;
    private int nrColunas=6;
    private int nrLinhas=4;
    private boolean modoVertical;
    private Toast toastActual;
    private ProgressDialog progresso;
    private AlertDialog.Builder builder;
    private AlertDialog finishDialog;
    private int profundidade;
    private Jogada ultimaJogada;
    private boolean fingerDown = false;
    private Path path = new Path();
    private float eventX;
    private float eventY;
    private Bitmap bitMapNormalSushi = null;
    private Bitmap bitMapSpecialSushi = null;
    private Bitmap bitMapEatenSushi = null;


    public TabuleiroJogo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabuleiroJogo(Activity atividade) {
        super(atividade);
        this.atividade = atividade;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(atividade);

        profundidade = Integer.parseInt(sharedPref.getString("nivel_dificuldade","1"));

        if(sharedPref.getBoolean("ordem_jogar",true)){
            estado = Estado.JOGADOR_A_JOGAR;
        }else{
            estado = Estado.IA_A_JOGAR;
        }

        tabuleiro = new Tabuleiro(nrLinhas,nrColunas);
        paint= new Paint();

        progresso = new ProgressDialog(getContext());

        progresso.setMessage("Thinking...");
        progresso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progresso.getWindow().setGravity(Gravity.BOTTOM);
        progresso.setIndeterminate(true);
        builder = new AlertDialog.Builder(atividade)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Sushi Nim")
                .setPositiveButton("OK", null);

        finishDialog = builder.create();
        finishDialog.setMessage("Don't Eat the LAST sushi!!");
        finishDialog.show();

    }

    //Load a bitmap from a resource with a target size
    static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    //Given the bitmap size and View size calculate a subsampling size (powers of 2)
    static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;	//Default subsampling size
        // See if image raw height and width is bigger than that of required view
        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            //bigger
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    protected void onMeasure(int widthMeasure, int heightMeasure){
        int width = MeasureSpec.getSize(widthMeasure);
        int height = MeasureSpec.getSize(heightMeasure);

        dimQuadradoCol = width / nrColunas;
        dimQuadradoLinha = height / nrLinhas;

        setMeasuredDimension(width, height);
    }

    protected  void onDraw(Canvas canvas){

        if(bitMapNormalSushi == null){
            bitMapNormalSushi = decodeSampledBitmapFromResource(getResources(), R.drawable.normalsushi, dimQuadradoCol, dimQuadradoLinha);
        }
        if(bitMapEatenSushi == null){
            bitMapEatenSushi = decodeSampledBitmapFromResource(getResources(), R.drawable.normalsushieaten, dimQuadradoCol, dimQuadradoLinha);
        }
        if(bitMapSpecialSushi == null){
            bitMapSpecialSushi = decodeSampledBitmapFromResource(getResources(), R.drawable.specialshushi, dimQuadradoCol, dimQuadradoLinha);
        }

        if(estado != Estado.IA_A_PENSAR)progresso.hide();

        paint.setColor(ContextCompat.getColor(getContext(), R.color.green));
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        invalidate();

        for(int linha = 0; linha < nrLinhas; linha++){
            for(int coluna = 0; coluna < nrColunas; coluna++){
                int a = coluna * dimQuadradoCol;
                int b = linha  * dimQuadradoLinha;
                //paint.setColor(Color.WHITE);
                paint.setStrokeWidth(3);
                //canvas.drawRect(a, b, a + dimQuadradoCol, b + dimQuadradoLinha, paint);
                paint.setStrokeWidth(0);
                //paint.setColor(Color.rgb(123, 167, 123));
                //canvas.drawRect(a+3,b+3,a+dimQuadradoCol-3,b+dimQuadradoLinha-3,paint);
                RectF rectangle = new RectF(a, b,a + (dimQuadradoCol),b + (dimQuadradoLinha));
                if(tabuleiro.isNormal(linha, coluna)){
                    canvas.drawBitmap(bitMapNormalSushi, null, rectangle, paint);
                    invalidate();
                }

                if(tabuleiro.removed(linha,coluna)){
                    canvas.drawBitmap(bitMapEatenSushi, null, rectangle, paint);
                    invalidate();
                }

                if(tabuleiro.isSpecial(linha,coluna)){
                    canvas.drawBitmap(bitMapSpecialSushi, null, rectangle, paint);
                    invalidate();
                }
            }
        }

        paint.setStyle(Paint.Style.STROKE);
        invalidate();
        paint.setColor(Color.BLACK);

        //Desenhar traço do dedo
        paint.setAntiAlias(true);
        paint.setStrokeWidth(12f);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        canvas.drawPath(path, paint);
        if(fingerDown){
            paint.setColor(Color.BLUE);
            canvas.drawCircle(eventX, eventY, 20, paint);
            paint.setColor((Color.BLACK));
        }
        invalidate();

        switch (estado){
            case IA_A_JOGAR: jogadaComputador(); break;
            case FIM_DE_JOGO: finalDeJogo(); break;
        }
    }

    private int rowInit;
    private int colInit;
    private LinkedList<Pair> pairs = new LinkedList<Pair>();

    @Override
    public boolean onTouchEvent(MotionEvent event){
        eventX = event.getX();
        eventY = event.getY();

        if(estado != Estado.JOGADOR_A_JOGAR)return false;

        if(toastActual != null) toastActual.cancel();

        int action = event.getAction();

        if(action == MotionEvent.ACTION_DOWN){
            colInit = ((int) event.getX())/dimQuadradoCol;
            rowInit = ((int) event.getY())/dimQuadradoLinha;
            Pair p = new Pair(colInit,rowInit);
            if(pairs.size() == 0){
                pairs.addLast(p);
            }else{
                if(!pairs.getLast().equals(p)){
                    pairs.addLast(p);
                }
            }
            fingerDown = true;
            path.moveTo(eventX, eventY);
            return true;
        }

        if(action == MotionEvent.ACTION_MOVE) {
            path.lineTo(eventX, eventY);
            colInit = ((int) event.getX())/dimQuadradoCol;
            rowInit = ((int) event.getY())/dimQuadradoLinha;
            Pair p = new Pair(colInit,rowInit);
            if(!pairs.getLast().equals(p)){
                if(!pairs.contains(p)){
                    pairs.addLast(p);
                }
            }
            return true;
        }
        if(action == MotionEvent.ACTION_UP){
            int col = ((int) event.getX())/dimQuadradoCol;
            int row = ((int) event.getY())/dimQuadradoLinha;
            fingerDown = false;
            path.reset();

            if(! tabuleiro.jogadaValida(pairs)){
                toastActual = Toast.makeText(getContext(),"Invalid Move",Toast.LENGTH_LONG);
                pairs = new LinkedList<Pair>();
                toastActual.show();
            }else{
                tabuleiro.realizaJoagada(pairs);
                pairs = new LinkedList<Pair>();
                if(tabuleiro.fimDeJogo()){
                    tabuleiro.cpuVence = true;
                    estado = Estado.FIM_DE_JOGO;
                }else{
                    estado = Estado.IA_A_JOGAR;
                }

                invalidate();
            }
            return true;
        }
        invalidate();
        return false;
    }

    private void jogadaComputador(){
        estado = Estado.IA_A_PENSAR;
        progresso.show();
        invalidate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(profundidade == 1){
                    ultimaJogada = Minimax.obterJogadaRandom(tabuleiro);
                }else{
                    ultimaJogada = Minimax.obterMelhorJogada(tabuleiro,profundidade-1);
                }


                tabuleiro.realizaJoagada(ultimaJogada.linha, ultimaJogada.quantidade);

                if(tabuleiro.fimDeJogo()){
                    tabuleiro.cpuVence = false;
                    estado = Estado.FIM_DE_JOGO;
                }else{
                    estado = Estado.JOGADOR_A_JOGAR;
                }
                postInvalidate();
            }
        }).start();
    }

    private void finalDeJogo(){
        progresso.hide();
        String message;
        if(tabuleiro.cpuVence){
            message = "You Lose!";
        }else{

            message="You Win! Lucky...!";
        }

        tabuleiro = new Tabuleiro(nrLinhas,nrColunas);
        estado = Estado.JOGADOR_A_JOGAR;

        finishDialog = builder.create();
        finishDialog.setMessage(message);
        finishDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bitMapNormalSushi.recycle();
                bitMapNormalSushi = null;

                bitMapSpecialSushi.recycle();
                bitMapSpecialSushi = null;

                bitMapEatenSushi.recycle();
                bitMapEatenSushi = null;
                
                System.gc();
            }
        });
        finishDialog.show();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    }
