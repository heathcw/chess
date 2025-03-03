package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator extends MovesCalculator {

    private final ChessGame.TeamColor teamColor;

    public KnightMovesCalculator(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveCollection = new ArrayList<>();
        int row = myPosition.getRow() + 1;
        int col = myPosition.getColumn() + 1;

        ChessPosition checkPosition = new ChessPosition(row + 2, col + 1);
        addMoveKnightKing(checkPosition, board, myPosition, moveCollection, teamColor);

        checkPosition = new ChessPosition(row + 1, col + 2);
        addMoveKnightKing(checkPosition, board, myPosition, moveCollection, teamColor);

        checkPosition = new ChessPosition(row - 1, col + 2);
        addMoveKnightKing(checkPosition, board, myPosition, moveCollection, teamColor);

        checkPosition = new ChessPosition(row - 2, col + 1);
        addMoveKnightKing(checkPosition, board, myPosition, moveCollection, teamColor);

        checkPosition = new ChessPosition(row - 2, col - 1);
        addMoveKnightKing(checkPosition, board, myPosition, moveCollection, teamColor);

        checkPosition = new ChessPosition(row - 1, col - 2);
        addMoveKnightKing(checkPosition, board, myPosition, moveCollection, teamColor);

        checkPosition = new ChessPosition(row + 1, col - 2);
        addMoveKnightKing(checkPosition, board, myPosition, moveCollection, teamColor);

        checkPosition = new ChessPosition(row + 2, col - 1);
        addMoveKnightKing(checkPosition, board, myPosition, moveCollection, teamColor);

        return moveCollection;
    }
}
