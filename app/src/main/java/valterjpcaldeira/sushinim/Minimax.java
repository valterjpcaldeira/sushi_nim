package valterjpcaldeira.sushinim;

import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Valter on 31/10/2015.
 */
public class Minimax {
    public static Jogada obterMelhorJogada(Tabuleiro tabuleiro, int profundidade) {

        //Build first simulate moves
        Tabuleiro backup = new Tabuleiro(tabuleiro);
        ArrayList<Tabuleiro> tabuleiros = getAllMovesPossible(backup);

        Jogada jog = simulateMoves(tabuleiros,profundidade-1, true);

        return jog;
    }

    private static Jogada simulateMoves(ArrayList<Tabuleiro> tabuleiros, int deep, boolean cpuMove) {
        Jogada result = new Jogada();

        if(tabuleiros.size() == 0){
            if(cpuMove){
                result.value = 200;
            }else{
                result.value = -300;
            }
            return result;
        }


        int bestValueMove = 0;
        if(cpuMove){
            bestValueMove = -1000;
        }else{
            bestValueMove = 1000;
        }


        //END
        if(deep <= 0){
            for (Tabuleiro t : tabuleiros) {
                int moveValue = getValueOfBoard(t);
                if(cpuMove){
                    if(moveValue > bestValueMove){
                        bestValueMove = moveValue;
                        result = t.getLastMove();
                        result.value = bestValueMove;
                    }
                }
            }
            return result;
        }

        //IF NOT END
        for (Tabuleiro t : tabuleiros) {
            ArrayList<Tabuleiro> tabuleirosAux = getAllMovesPossible(t);
            Jogada jog =null;
            if(cpuMove){
                jog = simulateMoves(tabuleirosAux, deep, !cpuMove);
            }else{
                jog = simulateMoves(tabuleirosAux, deep - 1, !cpuMove);
            }

            if(cpuMove){
                if(bestValueMove < jog.value){
                    bestValueMove = jog.value;
                    result = t.getLastMove();
                    result.value = jog.value;
                    if(bestValueMove >= 100){
                        return result;
                    }
                }
            }else{
                if(bestValueMove > jog.value){
                    bestValueMove = jog.value;
                    result = t.getLastMove();
                    result.value = jog.value;
                    if(bestValueMove < 0)return result;
                }
            }
        }
        return result;
    }

    private static int getValueOfBoard(Tabuleiro t) {
        if(t.getTotalPecas() == 1)return 100;
        if(t.getTotalPecas() == 0)return -100;
        if(t.has2LinesPair())return 80;
        if(t.has4LinesPair())return 80;
        if(t.getTotalPecas()%2 == 0)return 20;
        return 0;
    }

    public static ArrayList<Tabuleiro> getAllMovesPossible(Tabuleiro tabuleiro) {
        ArrayList<Tabuleiro> tabuleiros = new ArrayList<Tabuleiro>();
        for (int i = 0; i < tabuleiro.getLinhas(); i++) {
            for (int j = 1; j <= tabuleiro.getItemsInRow(i); j++) {
                Tabuleiro tabAux = new Tabuleiro(tabuleiro);
                tabAux.realizaJoagada(i, j);
                tabuleiros.add(tabAux);
            }
        }
        return tabuleiros;
    }

    public static Jogada obterJogadaRandom(Tabuleiro tabuleiro) {
        Jogada res = new Jogada();
        Random r = new Random();
        int linha = r.nextInt(tabuleiro.getLinhas());
        int pecas = -11;
        while((pecas = tabuleiro.getItemsInRow(linha)) <= 0){
            linha = r.nextInt(tabuleiro.getLinhas());
        }
        if(pecas != 1) {
            pecas = r.nextInt(pecas - 1) + 1;
        }
        res.quantidade = pecas;
        res.linha = linha;

        return res;
    }
}
