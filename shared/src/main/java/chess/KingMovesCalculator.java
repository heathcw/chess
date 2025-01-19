package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator {

    private ChessGame.TeamColor teamColor;

    public KingMovesCalculator(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveCollection = new ArrayList<>();
        int row = myPosition.getRow() + 1;
        int col = myPosition.getColumn() + 1;
        ChessPosition checkPosition = new ChessPosition(row + 1, col);
        addMove(checkPosition, board, myPosition, moveCollection);

        checkPosition = new ChessPosition(row + 1, col + 1);
        addMove(checkPosition, board, myPosition, moveCollection);

        checkPosition = new ChessPosition(row, col + 1);
        addMove(checkPosition, board, myPosition, moveCollection);

        checkPosition = new ChessPosition(row - 1, col + 1);
        addMove(checkPosition, board, myPosition, moveCollection);

        checkPosition = new ChessPosition(row - 1, col);
        addMove(checkPosition, board, myPosition, moveCollection);

        checkPosition = new ChessPosition(row - 1, col - 1);
        addMove(checkPosition, board, myPosition, moveCollection);

        checkPosition = new ChessPosition(row, col - 1);
        addMove(checkPosition, board, myPosition, moveCollection);

        checkPosition = new ChessPosition(row + 1, col - 1);
        addMove(checkPosition, board, myPosition, moveCollection);

        return moveCollection;
    }

    private void addMove(ChessPosition checkPosition, ChessBoard board, ChessPosition myPosition, Collection<ChessMove> myCollection) {
        if (checkPosition.getRow() >= 8 || checkPosition.getRow() <= 0 || checkPosition.getColumn() >= 8 || checkPosition.getColumn() <= 0) {
            return;
        }
        if (board.getPiece(checkPosition) == null || board.getPiece(checkPosition).getTeamColor() != this.teamColor) {
            ChessMove move = new ChessMove(myPosition, checkPosition, null);
            myCollection.add(move);
        }
    }
}
