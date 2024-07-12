package fr.example.quizz;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MultiplayerActivity extends AppCompatActivity {

    private LinearLayout part1Layout, part2Layout;
    private EditText questionEditText, answerEditText, player2AnswerEditText;
    private Button validateButtonPart1, validateButtonPart2;

    private String question, answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/permanent_marker_regular.ttf");

        // Initialisation des vues
        part1Layout = findViewById(R.id.part1Layout);
        part2Layout = findViewById(R.id.part2Layout);
        questionEditText = findViewById(R.id.questionEditText);
        answerEditText = findViewById(R.id.answerEditText);
        player2AnswerEditText = findViewById(R.id.player2AnswerEditText);
        validateButtonPart1 = findViewById(R.id.validateButtonPart1);
        validateButtonPart2 = findViewById(R.id.validateButtonPart2);

        // Appliquer la police personnalisée aux TextView joueur1TextView et joueur2TextView
        TextView joueur1TextView = findViewById(R.id.joueur1TextView);
        joueur1TextView.setTypeface(typeface);

        TextView joueur2TextView = findViewById(R.id.joueur2TextView);
        joueur2TextView.setTypeface(typeface);

        // Cacher la partie 2 (Section du Joueur 2)
        part2Layout.setVisibility(View.GONE);

        // Action du bouton Valider pour le Joueur 1
        validateButtonPart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupérer la question et la réponse du Joueur 1
                question = questionEditText.getText().toString().trim();
                answer = answerEditText.getText().toString().trim();

                // Afficher la question du Joueur 1 dans le TextView de la partie 2
                TextView questionTextView = findViewById(R.id.questionTextView);
                questionTextView.setText(question);

                // Masquer la partie 1 et afficher la partie 2
                part1Layout.setVisibility(View.GONE);
                part2Layout.setVisibility(View.VISIBLE);

                // Effacer le champ de réponse du Joueur 2
                player2AnswerEditText.setText("");
            }
        });

        // Action du bouton Valider pour le Joueur 2
        validateButtonPart2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Récupérer la réponse du Joueur 2
                String player2Answer = player2AnswerEditText.getText().toString().trim();

                // Comparer la réponse du Joueur 2 avec celle du Joueur 1
                if (player2Answer.equalsIgnoreCase(answer)) {
                    // Afficher un toast avec "Bonne réponse !"
                    showToast("Bonne réponse !");
                    // Changer le fond d'écran en vert
                    changeBackgroundColor(android.R.color.holo_green_light);
                    // Réinitialiser les champs du Joueur 1 pour une nouvelle question
                    questionEditText.setText("");
                    answerEditText.setText("");
                    // Afficher la partie 1 et masquer la partie 2 pour le Joueur 1
                    part1Layout.setVisibility(View.VISIBLE);
                    part2Layout.setVisibility(View.GONE);
                } else {
                    // Afficher un toast avec "Mauvaise réponse !"
                    showToast("Mauvaise réponse !");
                    // Changer le fond d'écran en rouge
                    changeBackgroundColor(android.R.color.holo_red_light);
                    // Démarrer l'activité ResultActivity
                    startActivity(new Intent(MultiplayerActivity.this, ResultActivity.class));
                    finish(); // Terminer l'activité actuelle
                }
            }
        });
    }

    // Méthode pour afficher un toast avec le message passé en paramètre
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Méthode pour changer la couleur de fond de l'écran
    private void changeBackgroundColor(int colorResId) {
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(colorResId));
    }
}
