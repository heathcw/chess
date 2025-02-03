package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> moveCollection = new ArrayList<>();
        ChessPiece pieceToMove = this.board.getPiece(startPosition);
        if (pieceToMove == null) {
            return null;
        }
        TeamColor team = pieceToMove.getTeamColor();
        moveCollection = pieceToMove.pieceMoves(this.board, startPosition);
        Collection<ChessMove> removeCollection = new ArrayList<>();
        for (ChessMove move: moveCollection) {
            //if move would put king in check, remove move
            ChessPiece placeHolder = this.board.getPiece(move.getEndPosition());
            this.board.addPiece(move.getEndPosition(), pieceToMove);
            this.board.addPiece(move.getStartPosition(), null);

            //check if king is in check
            if (isInCheck(team)) {
                removeCollection.add(move);
            }
            //return back to normal
            this.board.addPiece(move.getEndPosition(), placeHolder);
            this.board.addPiece(move.getStartPosition(), pieceToMove);
        }
        for (ChessMove remove: removeCollection) {
            moveCollection.remove(remove);
        }
        return moveCollection;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece pieceToMove = this.board.getPiece(move.getStartPosition());
        if (pieceToMove == null || pieceToMove.getTeamColor() != this.teamTurn) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> checkMove = validMoves(move.getStartPosition());
        if (!checkMove.contains(move)) {
            throw new InvalidMoveException();
        } else {
            if (move.getPromotionPiece() != null) {
                ChessPiece promotion = new ChessPiece(pieceToMove.getTeamColor(), move.getPromotionPiece());
                this.board.addPiece(move.getEndPosition(), promotion);
                this.board.addPiece(move.getStartPosition(), null);
            } else {
                this.board.addPiece(move.getEndPosition(), pieceToMove);
                this.board.addPiece(move.getStartPosition(), null);
            }
        }
        if (this.teamTurn == TeamColor.WHITE) {
            this.teamTurn = TeamColor.BLACK;
        } else {
            this.teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = new ChessPosition(1,1);
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition checkPosition = new ChessPosition(i,j);
                ChessPiece checkPiece = this.board.getPiece(checkPosition);
                if (checkPiece != null && checkPiece.getPieceType() == ChessPiece.PieceType.KING && checkPiece.getTeamColor() == teamColor) {
                    kingPosition = checkPosition;
                }
            }
        }
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition checkPosition = new ChessPosition(i,j);
                ChessPiece checkPiece = this.board.getPiece(checkPosition);
                if (checkPiece != null && checkPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> checkMoves = checkPiece.pieceMoves(this.board, checkPosition);
                    for (ChessMove move : checkMoves) {
                        if (move.getEndPosition().getRow() == kingPosition.getRow() && move.getEndPosition().getColumn() == kingPosition.getColumn()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition checkPosition = new ChessPosition(i,j);
                ChessPiece checkPiece = this.board.getPiece(checkPosition);
                if (checkPiece != null && checkPiece.getPieceType() == ChessPiece.PieceType.KING && checkPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> checkMoves = validMoves(checkPosition);
                    if (checkMoves.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition checkPosition = new ChessPosition(i,j);
                ChessPiece checkPiece = this.board.getPiece(checkPosition);
                if (checkPiece != null && checkPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> checkMove = validMoves(checkPosition);
                    if (!checkMove.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
