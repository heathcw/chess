package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator extends MovesCalculator {

    private final ChessGame.TeamColor teamColor;

    public BishopMovesCalculator(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveCollection = new ArrayList<>();
        int row = myPosition.getRow() + 1;
        int col = myPosition.getColumn() + 1;
        this.canAdd = true;

        while (canAdd && row > 1 && col > 1) {
            row--;
            col--;
            canAdd = addMoveRookBishop(row, col, moveCollection, board, myPosition, this.teamColor);
        }
        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() + 1;
        canAdd = true;

        while (canAdd && row < 8 && col > 1) {
            row++;
            col--;
            canAdd = addMoveRookBishop(row, col, moveCollection, board, myPosition, this.teamColor);
        }
        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() + 1;
        canAdd = true;

        while (canAdd && row < 8 && col < 8) {
            row++;
            col++;
            canAdd = addMoveRookBishop(row, col, moveCollection, board, myPosition, this.teamColor);
        }
        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() + 1;
        canAdd = true;

        while (canAdd && row > 1 && col < 8) {
            row--;
            col++;
            canAdd = addMoveRookBishop(row, col, moveCollection, board, myPosition, this.teamColor);
        }

        return moveCollection;
    }
}
