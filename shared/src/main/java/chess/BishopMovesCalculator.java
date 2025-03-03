package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator {

    private final ChessGame.TeamColor teamColor;
    private boolean canAdd;

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
            canAdd = addMove(row, col, moveCollection, board, myPosition);
        }
        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() + 1;
        canAdd = true;

        while (canAdd && row < 8 && col > 1) {
            row++;
            col--;
            canAdd = addMove(row, col, moveCollection, board, myPosition);
        }
        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() + 1;
        canAdd = true;

        while (canAdd && row < 8 && col < 8) {
            row++;
            col++;
            canAdd = addMove(row, col, moveCollection, board, myPosition);
        }
        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() + 1;
        canAdd = true;

        while (canAdd && row > 1 && col < 8) {
            row--;
            col++;
            canAdd = addMove(row, col, moveCollection, board, myPosition);
        }

        return moveCollection;
    }

    private boolean addMove(int row, int col, Collection<ChessMove> collection, ChessBoard board, ChessPosition myPosition) {
        canAdd = false;
        ChessPosition checkPosition = new ChessPosition(row, col);
        if (board.getPiece(checkPosition) == null) {
            ChessMove move = new ChessMove(myPosition, checkPosition, null);
            collection.add(move);
            canAdd = true;
        } else if (board.getPiece(checkPosition).getTeamColor() != this.teamColor) {
            ChessMove move = new ChessMove(myPosition, checkPosition, null);
            collection.add(move);
        }
        return canAdd;
    }
}
