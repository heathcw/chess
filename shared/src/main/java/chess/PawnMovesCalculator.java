package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator {

    private final ChessGame.TeamColor teamColor;

    public PawnMovesCalculator(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveCollection = new ArrayList<>();
        int row = myPosition.getRow() + 1;
        int col = myPosition.getColumn() + 1;
        ChessPosition checkPosition;

        switch (this.teamColor) {
            case WHITE:
                checkPosition = new ChessPosition(row + 1, col);
                if (row == 2 && board.getPiece(checkPosition) == null) {
                    checkPosition = new ChessPosition(row + 2, col);
                    normalMove(checkPosition, board, myPosition, moveCollection, 7);
                }
                checkPosition = new ChessPosition(row + 1, col);
                normalMove(checkPosition, board, myPosition, moveCollection, 7);

                checkPosition = new ChessPosition(row + 1, col + 1);
                captureMove(checkPosition, board, myPosition, moveCollection, 7);

                checkPosition = new ChessPosition(row + 1, col - 1);
                captureMove(checkPosition, board, myPosition, moveCollection, 7);

                break;
            case BLACK:
                checkPosition = new ChessPosition(row - 1, col);
                if (row == 7 && board.getPiece(checkPosition) == null) {
                    checkPosition = new ChessPosition(row - 2, col);
                    normalMove(checkPosition, board, myPosition, moveCollection, 0);
                }
                checkPosition = new ChessPosition(row - 1, col);
                normalMove(checkPosition, board, myPosition, moveCollection, 0);

                checkPosition = new ChessPosition(row - 1, col + 1);
                captureMove(checkPosition, board, myPosition, moveCollection, 0);

                checkPosition = new ChessPosition(row - 1, col - 1);
                captureMove(checkPosition, board, myPosition, moveCollection, 0);

                break;
        }

        return moveCollection;
    }

    private void captureMove(ChessPosition checkPosition, ChessBoard board, ChessPosition myPosition, Collection<ChessMove> myCollection, int end) {
        if (checkPosition.getRow() >= 8 || checkPosition.getRow() < 0 || checkPosition.getColumn() >= 8 || checkPosition.getColumn() < 0) {
            return;
        }
        if (board.getPiece(checkPosition) != null && board.getPiece(checkPosition).getTeamColor() != this.teamColor) {
            add(checkPosition, myPosition, myCollection, end);
        }
    }

    private void normalMove(ChessPosition checkPosition, ChessBoard board, ChessPosition myPosition, Collection<ChessMove> myCollection, int end) {
        if (checkPosition.getRow() >= 8 || checkPosition.getRow() < 0 || checkPosition.getColumn() >= 8 || checkPosition.getColumn() < 0) {
            return;
        }
        if (board.getPiece(checkPosition) == null) {
            add(checkPosition, myPosition, myCollection, end);
        }
    }

    private void promotion(ChessPosition checkPosition, ChessPosition myPosition, Collection<ChessMove> myCollection) {
        ChessMove move = new ChessMove(myPosition, checkPosition, ChessPiece.PieceType.QUEEN);
        myCollection.add(move);
        move = new ChessMove(myPosition, checkPosition, ChessPiece.PieceType.BISHOP);
        myCollection.add(move);
        move = new ChessMove(myPosition, checkPosition, ChessPiece.PieceType.ROOK);
        myCollection.add(move);
        move = new ChessMove(myPosition, checkPosition, ChessPiece.PieceType.KNIGHT);
        myCollection.add(move);
    }

    private void add(ChessPosition checkPosition, ChessPosition myPosition, Collection<ChessMove> myCollection,
                     int end) {
        if (checkPosition.getRow() == end) {
            promotion(checkPosition, myPosition, myCollection);
        } else {
            ChessMove move = new ChessMove(myPosition, checkPosition, null);
            myCollection.add(move);
        }
    }
}
