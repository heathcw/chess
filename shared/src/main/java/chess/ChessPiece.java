package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor pieceColor;
    private PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moveCollection = new ArrayList<>();

        if (this.type == PieceType.KING) {
            KingMovesCalculator kingMoves = new KingMovesCalculator(this.pieceColor);
            moveCollection = kingMoves.pieceMoves(board, myPosition);
        } else if (this.type == PieceType.BISHOP) {
            BishopMovesCalculator bishopMoves = new BishopMovesCalculator(this.pieceColor);
            moveCollection = bishopMoves.pieceMoves(board, myPosition);
        } else if (this.type == PieceType.KNIGHT) {
            KnightMovesCalculator knightMoves = new KnightMovesCalculator(this.pieceColor);
            moveCollection = knightMoves.pieceMoves(board, myPosition);
        } else if (this.type == PieceType.ROOK) {
            RookMovesCalculator rookMoves = new RookMovesCalculator(this.pieceColor);
            moveCollection = rookMoves.pieceMoves(board, myPosition);
        }

        return moveCollection;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
