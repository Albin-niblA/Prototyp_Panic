package model.score;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScoreManager {

    private static final int MAX_ENTRIES = 10;
    private static final String FILE_PATH = "src/util/data/scores.dat";

    private final List<ScoreEntry> scores = new ArrayList<>();

    public ScoreManager() {
        File file = new File(FILE_PATH);
        File dir = file.getParentFile();
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        load();
    }

    public List<ScoreEntry> getScores() {
        return scores;
    }

    public boolean qualifies(int score) {
        return scores.size() < MAX_ENTRIES || score > scores.get(scores.size() - 1).getScore();
    }

    public void addScore(ScoreEntry entry) {
        scores.add(entry);
        scores.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());
        while (scores.size() > MAX_ENTRIES) {
            scores.remove(scores.size() - 1);
        }
        save();
    }

    private void load() {
        scores.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    String initials = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    scores.add(new ScoreEntry(initials, score));
                }
            }
        } catch (IOException | NumberFormatException e) {
            scores.clear();
        }
        scores.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());
        while (scores.size() > MAX_ENTRIES) {
            scores.remove(scores.size() - 1);
        }
    }

    private void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (ScoreEntry entry : scores) {
                writer.println(entry.getInitials() + "," + entry.getScore());
            }
        } catch (IOException e) {
            System.err.println("Failed to save scores: " + e.getMessage());
        }
    }
}
