package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends MovesCalculator {

    private final ChessGame.TeamColor teamColor;

    public RookMovesCalculator(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveCollection = new ArrayList<>();
        int row = myPosition.getRow() + 1;
        int col = myPosition.getColumn() + 1;

        this.canAdd = true;

        while (canAdd && row > 1) {
            row--;
            canAdd = addMoveRookBishop(row, col, moveCollection, board, myPosition, this.teamColor);
        }
        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() + 1;
        canAdd = true;

        while (canAdd && row < 8) {
            row++;
            canAdd = addMoveRookBishop(row, col, moveCollection, board, myPosition, this.teamColor);
        }
        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() + 1;
        canAdd = true;

        while (canAdd && col < 8) {
            col++;
            canAdd = addMoveRookBishop(row, col, moveCollection, board, myPosition, this.teamColor);
        }
        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() + 1;
        canAdd = true;

        while (canAdd && col > 1) {
            col--;
            canAdd = addMoveRookBishop(row, col, moveCollection, board, myPosition, this.teamColor);
        }

        return moveCollection;
    }
}
