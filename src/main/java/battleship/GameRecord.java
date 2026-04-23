package battleship;

public class GameRecord {
    private String date;
    private int totalMoves;
    private int hits;
    private int sunkShips;

    public GameRecord(int totalMoves, int hits, int sunkShips) {
        // Guarda a data e hora atual do jogo num formato limpo
        this.date = java.time.LocalDateTime.now().withNano(0).toString().replace("T", " ");
        this.totalMoves = totalMoves;
        this.hits = hits;
        this.sunkShips = sunkShips;
    }

    public String getDate() {
        return date;
    }

    public int getTotalMoves() {
        return totalMoves;
    }

    public int getHits() {
        return hits;
    }

    public int getSunkShips() {
        return sunkShips;
    }
}
