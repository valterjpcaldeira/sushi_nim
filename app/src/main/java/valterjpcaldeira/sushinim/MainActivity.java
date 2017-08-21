package valterjpcaldeira.sushinim;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Clicar em Jogar
        ImageView imgJogar = (ImageView)findViewById(R.id.imageJogar);
        imgJogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.gc();
                startActivityForResult(new Intent(MainActivity.this, Jogo.class), REQUEST_CODE);
            }
        });

        //Clicar em Jogar
        ImageView imgOpcoes = (ImageView)findViewById(R.id.imageOpcoes);
        imgOpcoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.gc();
                startActivity(new Intent(MainActivity.this, Opcoes.class));
            }
        });

        //Clicar em Jogar
        ImageView imgInstrcutions = (ImageView)findViewById(R.id.instrucoes);
        imgInstrcutions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.gc();
                startActivity(new Intent(MainActivity.this, InstrucoesActivity.class));
            }
        });



    }



}
