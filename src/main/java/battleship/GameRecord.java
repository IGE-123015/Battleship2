package battleship;

public class GameRecord {
    public String date;
    public int totalMoves;
    public int hits;
    public int sunkShips;

    public GameRecord(int totalMoves, int hits, int sunkShips) {
        // Guarda a data e hora atual do jogo num formato limpo
        this.date = java.time.LocalDateTime.now().withNano(0).toString().replace("T", " ");
        this.totalMoves = totalMoves;
        this.hits = hits;
        this.sunkShips = sunkShips;
    }
}