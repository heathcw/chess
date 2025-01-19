package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator {

    private ChessGame.TeamColor teamColor;

    public QueenMovesCalculator(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveCollection = new ArrayList<>();

        BishopMovesCalculator diagonalMoves = new BishopMovesCalculator(this.teamColor);
        RookMovesCalculator perpendicularMoves = new RookMovesCalculator(this.teamColor);

        moveCollection = diagonalMoves.pieceMoves(board, myPosition);
        moveCollection.addAll(perpendicularMoves.pieceMoves(board, myPosition));

        return moveCollection;
    }
}
