package fr.example.quizz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Initialisation des SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/permanent_marker_regular.ttf");

        // Récupérer les données transmises par l'intent
        String username = getIntent().getStringExtra("username");
        int score = getIntent().getIntExtra("score", 0); // 0 est une valeur par défaut si aucune valeur n'est trouvée

        // Sauvegarder le score actuel de l'utilisateur
        saveScore(username, score);
        TextView usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setTypeface(typeface);

        TextView scoreTextView = findViewById(R.id.scoreTextView);
        scoreTextView.setTypeface(typeface);

        // Afficher le nom de l'utilisateur et son score
        usernameTextView = findViewById(R.id.usernameTextView);
        scoreTextView = findViewById(R.id.scoreTextView);
        usernameTextView.setText(username);
        scoreTextView.setText("Score : " + score);

        // Récupérer et afficher les 10 meilleurs scores
        List<ScoreEntry> topScores = getTopScores(10);
        displayTopScores(topScores);

        // Bouton pour recommencer
        Button restartButton = findViewById(R.id.restartButton);
        restartButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, ThemesActivity.class);
            startActivity(intent);
            finish(); // Optionnel : fermer cette activité pour empêcher de revenir en arrière
        });

        // Bouton pour revenir à l'écran de démarrage
        Button homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Optionnel : fermer cette activité pour empêcher de revenir en arrière
        });
    }

    private void saveScore(String username, int score) {
        // Sauvegarder le score de l'utilisateur spécifique dans SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("score_" + username, score);
        editor.apply();
    }

    private List<ScoreEntry> getTopScores(int limit) {
        // Récupérer tous les scores depuis SharedPreferences
        Map<String, ?> allEntries = sharedPreferences.getAll();
        List<ScoreEntry> scoreEntries = new ArrayList<>();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("score_")) {
                int score = (int) entry.getValue();
                String user = key.substring("score_".length());
                scoreEntries.add(new ScoreEntry(user, score));
            }
        }

        // Trier les scores par ordre décroissant
        Collections.sort(scoreEntries, new Comparator<ScoreEntry>() {
            @Override
            public int compare(ScoreEntry o1, ScoreEntry o2) {
                return Integer.compare(o2.getScore(), o1.getScore());
            }
        });

        // Limiter à "limit" scores
        return scoreEntries.subList(0, Math.min(scoreEntries.size(), limit));
    }

    private void displayTopScores(List<ScoreEntry> topScores) {
        TableLayout tableLayout = findViewById(R.id.topScoresTableLayout);

        for (int i = 0; i < topScores.size(); i++) {
            ScoreEntry entry = topScores.get(i);

            TableRow tableRow = new TableRow(this);
            TextView rankTextView = new TextView(this);
            TextView userTextView = new TextView(this);
            TextView scoreTextView = new TextView(this);

            rankTextView.setText(String.valueOf(i + 1));
            userTextView.setText(entry.getUser());
            scoreTextView.setText(String.valueOf(entry.getScore()));

            tableRow.addView(rankTextView);
            tableRow.addView(userTextView);
            tableRow.addView(scoreTextView);

            tableLayout.addView(tableRow);
        }
    }

    private static class ScoreEntry {
        private final String user;
        private final int score;

        public ScoreEntry(String user, int score) {
            this.user = user;
            this.score = score;
        }

        public String getUser() {
            return user;
        }

        public int getScore() {
            return score;
        }
    }
}
