package chess;

import java.util.Collection;

public class MovesCalculator {

    protected boolean canAdd;

    public MovesCalculator() {
    }

    protected boolean addMoveRookBishop(int row, int col, Collection<ChessMove> collection, ChessBoard board,
                                        ChessPosition myPosition, ChessGame.TeamColor teamColor) {
        canAdd = false;
        ChessPosition checkPosition = new ChessPosition(row, col);
        if (board.getPiece(checkPosition) == null) {
            ChessMove move = new ChessMove(myPosition, checkPosition, null);
            collection.add(move);
            canAdd = true;
        } else if (board.getPiece(checkPosition).getTeamColor() != teamColor) {
            ChessMove move = new ChessMove(myPosition, checkPosition, null);
            collection.add(move);
        }
        return canAdd;
    }

    protected void addMoveKnightKing(ChessPosition checkPosition, ChessBoard board, ChessPosition myPosition,
                                     Collection<ChessMove> myCollection, ChessGame.TeamColor teamColor) {
        if (checkPosition.getRow() >= 8 || checkPosition.getRow() < 0 || checkPosition.getColumn() >= 8 || checkPosition.getColumn() < 0) {
            return;
        }
        if (board.getPiece(checkPosition) == null || board.getPiece(checkPosition).getTeamColor() != teamColor) {
            ChessMove move = new ChessMove(myPosition, checkPosition, null);
            myCollection.add(move);
        }
    }
}
