package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator {

    private ChessGame.TeamColor teamColor;

    public RookMovesCalculator(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveCollection = new ArrayList<>();
        int row = myPosition.getRow() + 1;
        int col = myPosition.getColumn() + 1;

        boolean canAdd = true;

        while (canAdd && row > 1) {
            row--;
            ChessPosition checkPosition = new ChessPosition(row, col);
            if (board.getPiece(checkPosition) == null) {
                ChessMove move = new ChessMove(myPosition, checkPosition, null);
                moveCollection.add(move);
            } else if (board.getPiece(checkPosition).getTeamColor() != this.teamColor) {
                ChessMove move = new ChessMove(myPosition, checkPosition, null);
                moveCollection.add(move);
                break;
            } else {
                canAdd = false;
            }
        }
        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() + 1;
        canAdd = true;

        while (canAdd && row < 8) {
            row++;
            ChessPosition checkPosition = new ChessPosition(row, col);
            if (board.getPiece(checkPosition) == null) {
                ChessMove move = new ChessMove(myPosition, checkPosition, null);
                moveCollection.add(move);
            } else if (board.getPiece(checkPosition).getTeamColor() != this.teamColor) {
                ChessMove move = new ChessMove(myPosition, checkPosition, null);
                moveCollection.add(move);
                break;
            } else {
                canAdd = false;
            }
        }
        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() + 1;
        canAdd = true;

        while (canAdd && col < 8) {
            col++;
            ChessPosition checkPosition = new ChessPosition(row, col);
            if (board.getPiece(checkPosition) == null) {
                ChessMove move = new ChessMove(myPosition, checkPosition, null);
                moveCollection.add(move);
            } else if (board.getPiece(checkPosition).getTeamColor() != this.teamColor) {
                ChessMove move = new ChessMove(myPosition, checkPosition, null);
                moveCollection.add(move);
                break;
            } else {
                canAdd = false;
            }
        }
        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() + 1;
        canAdd = true;

        while (canAdd && col > 1) {
            col--;
            ChessPosition checkPosition = new ChessPosition(row, col);
            if (board.getPiece(checkPosition) == null) {
                ChessMove move = new ChessMove(myPosition, checkPosition, null);
                moveCollection.add(move);
            } else if (board.getPiece(checkPosition).getTeamColor() != this.teamColor) {
                ChessMove move = new ChessMove(myPosition, checkPosition, null);
                moveCollection.add(move);
                break;
            } else {
                canAdd = false;
            }
        }

        return moveCollection;
    }
}
