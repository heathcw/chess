package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator extends MovesCalculator {

    private final ChessGame.TeamColor teamColor;

    public KingMovesCalculator(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveCollection = new ArrayList<>();
        int row = myPosition.getRow() + 1;
        int col = myPosition.getColumn() + 1;
        ChessPosition checkPosition = new ChessPosition(row + 1, col);
        addMoveKnightKing(checkPosition, board, myPosition, moveCollection, this.teamColor);

        checkPosition = new ChessPosition(row + 1, col + 1);
        addMoveKnightKing(checkPosition, board, myPosition, moveCollection, this.teamColor);

        checkPosition = new ChessPosition(row, col + 1);
        addMoveKnightKing(checkPosition, board, myPosition, moveCollection, this.teamColor);

        checkPosition = new ChessPosition(row - 1, col + 1);
        addMoveKnightKing(checkPosition, board, myPosition, moveCollection, this.teamColor);

        checkPosition = new ChessPosition(row - 1, col);
        addMoveKnightKing(checkPosition, board, myPosition, moveCollection, this.teamColor);

        checkPosition = new ChessPosition(row - 1, col - 1);
        addMoveKnightKing(checkPosition, board, myPosition, moveCollection, this.teamColor);

        checkPosition = new ChessPosition(row, col - 1);
        addMoveKnightKing(checkPosition, board, myPosition, moveCollection, this.teamColor);

        checkPosition = new ChessPosition(row + 1, col - 1);
        addMoveKnightKing(checkPosition, board, myPosition, moveCollection, this.teamColor);

        return moveCollection;
    }
}
