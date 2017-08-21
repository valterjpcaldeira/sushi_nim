package valterjpcaldeira.sushinim;

/**
 * Created by Valter on 22/11/2015.
 */
public class Pair {
    private int col;
    private int row;

    public Pair(int col, int row){
        this.col = col;
        this.row = row;
    }

    @Override
    public boolean equals(Object o){
        Pair p = (Pair)o;
        return p.getCol() == this.getCol() && p.getRow() == this.getRow();
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }
}
