package battleship;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Scoreboard {
    private static final String FILE_NAME = "scoreboard.json";

    public static void saveScore(int moves, int hits, int sinks) {
        // Cria um Gson configurado para formatar o JSON de forma bonita (com
        // indentação)
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(FILE_NAME);
        List<GameRecord> scores = new ArrayList<>();
        Type listType = new TypeToken<List<GameRecord>>() {
        }.getType();

        try {
            // Se o ficheiro já existir, lê o histórico antigo primeiro
            if (file.exists() && file.length() > 0) {
                try (FileReader reader = new FileReader(file)) {
                    scores = gson.fromJson(reader, listType);
                    if (scores == null)
                        scores = new ArrayList<>(); // Prevenção extra
                }
            }

            // Adiciona a pontuação do jogo que acabou de terminar
            scores.add(new GameRecord(moves, hits, sinks));

            // Grava a lista atualizada de volta no ficheiro
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(scores, writer);
            }
        } catch (Exception e) {
            System.err.println("Erro ao guardar o Scoreboard: " + e.getMessage());
        }
    }

    public static void printScoreboard() {
        Gson gson = new Gson();
        File file = new File(FILE_NAME);

        if (!file.exists() || file.length() == 0) {
            System.out.println("\nAinda não há jogos registados no Scoreboard.");
            return;
        }

        try {
            Type listType = new TypeToken<List<GameRecord>>() {
            }.getType();
            try (FileReader reader = new FileReader(file)) {
                List<GameRecord> scores = gson.fromJson(reader, listType);

                System.out.println("\n============= SCOREBOARD ==============");
                for (int i = 0; i < scores.size(); i++) {
                    GameRecord r = scores.get(i);
                    System.out.printf("Jogo %d [%s] -> Jogadas: %d | Tiros Certeiros: %d | Navios Afundados: %d%n",
                            (i + 1), r.getDate(), r.getTotalMoves(), r.getHits(), r.getSunkShips());
                }
                System.out.println("=======================================\n");
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar o Scoreboard: " + e.getMessage());
        }
    }
}