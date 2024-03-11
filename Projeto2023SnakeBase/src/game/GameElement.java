package game;


import environment.BoardPosition;
import environment.Cell;

import java.io.Serializable;

public abstract class GameElement implements Serializable {
    private BoardPosition position;

    public void setPosition(BoardPosition position) {
        this.position = position;
    }

    public BoardPosition getPosition() {
        return position;
    }
}
