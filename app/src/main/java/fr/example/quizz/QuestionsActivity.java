package fr.example.quizz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    private List<Question> questions;
    private int currentQuestionIndex = 0;

    private TextView questionTextView;
    private ImageView questionImageView;
    private RadioButton option1RadioButton, option2RadioButton, option3RadioButton, option4RadioButton;
    private Button nextButton, toggleSoundButton;
    private RadioGroup optionsRadioGroup;
    private ProgressBar progressBar;

    private int currentScore = 0;
    private SharedPreferences sharedPreferences;
    private String username;

    private String jsonFileName;
    private static final String TAG = "QuestionsActivity";

    // MediaPlayer pour la musique de fond
    private MediaPlayer mediaPlayer;
    private boolean isMusicPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);

        // Initialisation des vues
        questionTextView = findViewById(R.id.questionTextView);
        questionImageView = findViewById(R.id.questionImageView);
        option1RadioButton = findViewById(R.id.option1);
        option2RadioButton = findViewById(R.id.option2);
        option3RadioButton = findViewById(R.id.option3);
        option4RadioButton = findViewById(R.id.option4);
        nextButton = findViewById(R.id.nextButton);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        progressBar = findViewById(R.id.progressBar);
        toggleSoundButton = findViewById(R.id.toggleSoundButton);

        // Chargement des préférences utilisateur
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = sharedPreferences.getString("userName", "DefaultUser");

        // Appliquer la police "Rubik"
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/rubik_variablefont_wght.ttf");
        questionTextView.setTypeface(typeface, Typeface.BOLD);
        option1RadioButton.setTypeface(typeface);
        option2RadioButton.setTypeface(typeface);
        option3RadioButton.setTypeface(typeface);
        option4RadioButton.setTypeface(typeface);
        nextButton.setTypeface(typeface, Typeface.BOLD);
        toggleSoundButton.setTypeface(typeface);

        // Récupération du nom du fichier JSON depuis l'intent
        jsonFileName = getIntent().getStringExtra("json_file_name");
        if (jsonFileName != null) {
            loadQuestions(jsonFileName);
        } else {
            Log.e(TAG, "No JSON file name provided");
        }

        // Configuration de la barre de progression
        if (questions != null) {
            progressBar.setMax(questions.size());
        }

        // Affichage de la première question si des questions sont disponibles
        if (questions != null && !questions.isEmpty()) {
            displayQuestion(currentQuestionIndex);
        } else {
            Log.e(TAG, "No questions available or error loading questions");
        }

        // Action du bouton Next
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedOptionIndex = getSelectedOptionIndex();
                if (selectedOptionIndex != -1) {
                    // Logique pour vérifier la réponse
                    if (questions.get(currentQuestionIndex).isAnswerCorrect(selectedOptionIndex)) {
                        currentScore++;
                    }
                }

                currentQuestionIndex++;
                updateProgress(currentQuestionIndex);

                // Affichage de la prochaine question ou affichage des résultats finaux
                if (currentQuestionIndex < questions.size()) {
                    displayQuestion(currentQuestionIndex);
                } else {
                    showResult();
                }
            }
        });

        // Action du bouton pour activer/couper le son
        toggleSoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMusic();
            }
        });

        // Initialisation du MediaPlayer pour la musique de fond
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        mediaPlayer.setLooping(true); // Lecture en boucle
        mediaPlayer.start(); // Démarrage de la musique
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Libération des ressources du MediaPlayer
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // Méthode pour charger les questions depuis un fichier JSON
    private void loadQuestions(String jsonFileName) {
        try {
            InputStream is = getAssets().open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            Gson gson = new Gson();
            Type questionListType = new TypeToken<ArrayList<Question>>(){}.getType();
            questions = gson.fromJson(json, questionListType);

            if (questions != null) {
                for (Question question : questions) {
                    if (jsonFileName.equals("logo_questions.json")) {
                        question.setImageQuestion(true);
                    } else {
                        question.setImageQuestion(false);
                    }
                }

                Collections.shuffle(questions);
                if (questions.size() > 5) {
                    questions = questions.subList(0, 5);
                }
            } else {
                Log.e(TAG, "No questions loaded from JSON");
            }
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Error loading questions: " + e.getMessage());
        }
    }

    // Méthode pour afficher la question actuelle
    private void displayQuestion(int index) {
        if (questions != null && index < questions.size()) {
            Question question = questions.get(index);

            if (question.isImageQuestion()) {
                questionImageView.setVisibility(View.VISIBLE);
                questionTextView.setVisibility(View.GONE);
                questionImageView.setImageDrawable(loadDrawableFromAssets(question.getQuestion()));
            } else {
                questionImageView.setVisibility(View.GONE);
                questionTextView.setVisibility(View.VISIBLE);
                questionTextView.setText(question.getQuestion());
            }

            List<String> options = question.getOptions();

            option1RadioButton.setText(options.get(0));
            option2RadioButton.setText(options.get(1));
            option3RadioButton.setText(options.get(2));
            option4RadioButton.setText(options.get(3));

            resetRadioButtons();
        } else {
            Log.e(TAG, "No questions available or index out of bounds");
        }
    }

    // Méthode pour charger une image depuis les ressources Assets
    private Drawable loadDrawableFromAssets(String filename) {
        try {
            InputStream is = getAssets().open(filename);
            return Drawable.createFromStream(is, null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Méthode pour réinitialiser les boutons radio
    private void resetRadioButtons() {
        optionsRadioGroup.clearCheck();
    }

    // Méthode pour sauvegarder le score dans les préférences
    private void saveScore(int score) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("score_" + username, score);
        editor.apply();
    }

    // Méthode pour mettre à jour la barre de progression
    private void updateProgress(int progress) {
        progressBar.setProgress(progress);
    }

    // Méthode pour obtenir l'indice de l'option sélectionnée
    private int getSelectedOptionIndex() {
        int selectedOptionId = optionsRadioGroup.getCheckedRadioButtonId();
        if (selectedOptionId == R.id.option1) {
            return 0;
        } else if (selectedOptionId == R.id.option2) {
            return 1;
        } else if (selectedOptionId == R.id.option3) {
            return 2;
        } else if (selectedOptionId == R.id.option4) {
            return 3;
        } else {
            return -1;
        }
    }

    // Méthode pour activer ou couper la musique
    private void toggleMusic() {
        if (isMusicPlaying) {
            mediaPlayer.pause();
            toggleSoundButton.setText("\uD83D\uDD07");
        } else {
            mediaPlayer.start();
            toggleSoundButton.setText("\uD83D\uDD0A");
        }
        isMusicPlaying = !isMusicPlaying;
    }

    // Méthode pour afficher les résultats finaux
    private void showResult() {
        Log.d(TAG, "Final Score: " + currentScore);
        Intent intent = new Intent(QuestionsActivity.this, ResultActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("score", currentScore);
        startActivity(intent);
        finish();
    }
}
