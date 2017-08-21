package valterjpcaldeira.sushinim;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by Valter on 31/10/2015.
 */
public class Tabuleiro {
    private int[][] tabuleiro;
    private int linhas;
    private int colunas;
    private int totalPecas = 0;
    public boolean cpuVence =false;
    private Jogada lastMove;
    private int moveValue;
    private final int ITEM = 1;
    private final int EMPTY = 0;
    private final int SPECIAL = 3;
    private final int REMOVED = -1;

    public int getLinhas(){
        return linhas;
    }

    public int getColunas(){
        return colunas;
    }

    public int getTotalPecas(){
        return totalPecas;
    }

    public Tabuleiro(int linhas, int colunas){
        tabuleiro = new int[linhas][colunas];
        this.linhas = linhas;
        this.colunas = colunas;
        Random r = new Random();

        for(int l = 0; l < linhas;l++){
            int pecas = r.nextInt(colunas - 1) + 1;
            totalPecas+=pecas;
            for(int c = 0 ; c < colunas; c++){
                if(c < pecas){
                    int prob = r.nextInt(100);
                    if(prob < 11){
                        tabuleiro[l][c] = SPECIAL;
                    }else{
                        tabuleiro[l][c] = ITEM;
                    }
                }else{
                    tabuleiro[l][c] = EMPTY;
                }
            }
        }
    }

    public Tabuleiro(Tabuleiro tab){
        this.linhas = tab.getLinhas();
        this.colunas = tab.getColunas();
        this.totalPecas = tab.totalPecas;
        this.tabuleiro = new int[linhas][colunas];
        for(int i = 0; i < linhas;i++){
            this.tabuleiro[i] = new int[colunas];
            this.tabuleiro[i] = tab.tabuleiro[i].clone();
        }
    }

    public boolean vazia(int linha, int coluna) {
        int valor = tabuleiro[linha][coluna];
        if(valor == EMPTY || valor == REMOVED) return true;
        return false;
    }

    public int getItemsInRow(int row){
        int items = 0;
        for(int c = 0; c < colunas; c++){
            if(tabuleiro[row][c] == ITEM || tabuleiro[row][c] == SPECIAL ){
                items++;
            }
        }
        return items;
    }

    public boolean fimDeJogo() {
        if(totalPecas == 0)return true;
        return false;
    }

    public void realizaJoagada(int linha, int quantidade) {

        Jogada aux = new Jogada();
        aux.quantidade = quantidade;
        aux.linha = linha;
        lastMove = aux;
        totalPecas -= quantidade;

        cleanLastMove();

        for(int c = colunas-1; quantidade > 0; c--){
            if(tabuleiro[linha][c] == ITEM) {
                tabuleiro[linha][c] = REMOVED;
                quantidade--;
            }
            if(tabuleiro[linha][c] == SPECIAL) {
                tabuleiro[linha][c] = REMOVED;
                quantidade--;
                for(int l = 0; l < linhas;l++) {
                    if (linha != l) {
                        for (int col = 0; col < colunas; col++) {
                            if(tabuleiro[l][col] == EMPTY){
                                Random r = new Random();
                                int prob = r.nextInt(100);
                                if(prob < 31){
                                    tabuleiro[l][col] = ITEM;
                                    totalPecas++;
                                }
                            }
                        }
                    }

                }
            }
        }

    }

    private void cleanLastMove() {
        for(int l = 0; l < linhas;l++){
            for(int c = 0 ; c < colunas; c++) {
                if (tabuleiro[l][c] == REMOVED) {
                    tabuleiro[l][c] = EMPTY;
                }
            }
        }
    }

    public int getNumLinesWithItems(){
        int numLinesWithItems = 0;
        for (int i = 0; i < linhas;i++){
            if(getItemsInRow(i) != 0)numLinesWithItems++;
        }
        return numLinesWithItems;
    }

    public boolean has2LinesPair() {
        if(getNumLinesWithItems() != 2)return false;
        int compare = -1;
        for (int i = 0; i < linhas;i++){
            int itensRow = getItemsInRow(i);
            if(itensRow != 0){
                if(compare == -1){
                    compare = itensRow;
                }else{
                    if(compare == itensRow){
                        return true;
                    }else{
                        return false;
                    }

                }
            }
        }
        return false;
    }

    public int getMoveValue() {
        return moveValue;
    }

    public void setMoveValue(int moveValue) {
        this.moveValue = moveValue;
    }

    public boolean has4LinesPair() {
        if(getNumLinesWithItems() != 4)return false;
        int compare = -1;
        int compareB = -1;
        boolean compare1 = false;
        boolean compare2 = false;
        for (int i = 0; i < linhas;i++){
            int numItens = getItemsInRow(i);
            if(numItens != 0){
                if(compare == -1){
                    compare = numItens;
                }else{
                    if(compare == numItens){
                        compare1 = true;
                    }else{
                        if(compareB == -1){
                            compareB = numItens;
                        }else{
                            if(compareB == numItens){
                                compare2 = true;
                            }else{
                                return false;
                            }
                        }
                    }

                }
            }
        }
        return compare1&compare2;
    }

    public boolean jogadaValida(LinkedList<Pair> pairs) {
        int row = -1;
        for (Pair p :
                pairs) {
            if(p.getRow() >= linhas || p.getCol() >= colunas)return false;
            if (!vazia(p.getRow(), p.getCol())) {
                if(row ==-1){
                    row = p.getRow();
                }else{
                    if(row != p.getRow())return false;
                }
            }
        }
        if(row == -1)return false;
        return true;
    }

    public void realizaJoagada(LinkedList<Pair> pairs) {
        cleanLastMove();

        for (Pair p :
                pairs) {
            if (!vazia(p.getRow(), p.getCol())) {
                if(tabuleiro[p.getRow()][p.getCol()] == SPECIAL) {
                    for(int l = 0; l < linhas;l++) {
                        if (p.getRow() != l) {
                            for (int c = 0; c < colunas; c++) {
                                if(tabuleiro[l][c] == EMPTY){
                                    Random r = new Random();
                                    int prob = r.nextInt(100);
                                    if(prob < 31){
                                        tabuleiro[l][c] = ITEM;
                                        totalPecas++;
                                    }
                                }
                            }
                        }

                    }
                }
                tabuleiro[p.getRow()][p.getCol()] = REMOVED;
                totalPecas--;
            }
        }
    }

    public Jogada getLastMove() {
        return lastMove;
    }

    public void setLastMove(Jogada lastMove) {
        this.lastMove = lastMove;
    }

    public boolean removed(int linha, int coluna) {
        int valor = tabuleiro[linha][coluna];
        if(valor == REMOVED) return true;
        return false;
    }

    public boolean isSpecial(int linha, int coluna) {
        return tabuleiro[linha][coluna] == SPECIAL;
    }

    public boolean isNormal(int linha, int coluna) {
        int valor = tabuleiro[linha][coluna];
        if(valor == ITEM) return true;
        return false;
    }
}
