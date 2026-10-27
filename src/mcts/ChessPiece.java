package mcts;

import javafx.scene.paint.Color;
import sample.Main;

import java.io.Serializable;

public class ChessPiece {
    Main.PieceType type;
    boolean flipped;
    boolean selected;


    ChessPiece(Main.PieceType type) {
        this.type = type;
        this.flipped = false;
        this.selected = false;
    }

    public ChessPiece(Main.PieceType type, boolean flipped) {
        this.type = type;
        this.flipped = flipped;
        this.selected = false;
        this.setType(type);
    }

    public ChessPiece(Main.PieceType type, boolean flipped, boolean selected) {
        this.type = type;
        this.flipped = flipped;
        this.selected = selected;
    }

    public void setType(Main.PieceType type) {
        this.type = type;
        if(this.type == Main.PieceType.EMPTY){
            this.selected = false;
            this.flipped = false;
        }
    }

    public Main.PieceType getType() {
        return type;
    }

    public boolean isFlipped() {
        return flipped;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;

    }
}