package fr.example.quizz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ThemesActivity extends AppCompatActivity {

    private LinearLayout themesLayout; // Layout pour ajouter dynamiquement les boutons de thème
    private MediaPlayer buttonClickPlayer; // MediaPlayer pour le son du clic de bouton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);

        themesLayout = findViewById(R.id.themesLayout); // Récupération du LinearLayout dans le layout XML

        // Initialisation du MediaPlayer pour le son du clic de bouton
        buttonClickPlayer = MediaPlayer.create(this, R.raw.button_click_sound);

        // Récupération de la liste des thèmes à partir des fichiers JSON dans le dossier assets
        List<String> themes = getThemesList();

        // Création des boutons de thème dynamiquement
        createThemeButtons(themes);

        // Récupération du nom d'utilisateur depuis SharedPreferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = preferences.getString("userName", "DefaultUser");

        // Affichage du nom d'utilisateur dans un TextView par exemple
        TextView usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText("Bienvenue, " + username + " !");

        // Appliquer la police "Permanent Marker" au TextView usernameTextView
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/permanent_marker_regular.ttf");
        usernameTextView.setTypeface(typeface, Typeface.BOLD);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Libération des ressources du MediaPlayer de son du clic de bouton
        if (buttonClickPlayer != null) {
            buttonClickPlayer.release();
            buttonClickPlayer = null;
        }
    }

    private List<String> getThemesList() {
        List<String> themes = new ArrayList<>();
        try {
            // Liste des fichiers dans le dossier assets
            String[] files = getAssets().list("");

            // Recherche des fichiers JSON et ajout à la liste des thèmes
            for (String file : files) {
                if (file.endsWith(".json")) {
                    themes.add(file.replace(".json", ""));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return themes;
    }

    private void createThemeButtons(List<String> themes) {
        // Création dynamique des boutons pour chaque thème
        for (final String theme : themes) {
            Button themeButton = new Button(this);
            themeButton.setText(theme);
            themeButton.setBackgroundResource(R.drawable.theme_button_background);
            themeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Jouer le son du clic de bouton
                    playButtonClickSound();

                    // Intent pour démarrer QuestionsActivity avec le thème sélectionné
                    Intent intent = new Intent(ThemesActivity.this, QuestionsActivity.class);
                    intent.putExtra("json_file_name", theme + ".json"); // Passer le nom du fichier JSON comme thème
                    startActivity(intent);
                }
            });

            // Définir les marges pour chaque bouton
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(16, 16, 16, 16); // Ajouter des marges ici (gauche, haut, droite, bas)
            themeButton.setLayoutParams(layoutParams);

            // Ajout du bouton au layout
            themesLayout.addView(themeButton);
        }
    }

    private void playButtonClickSound() {
        if (buttonClickPlayer != null) {
            buttonClickPlayer.start(); // Démarrer la lecture du son du clic de bouton
        }
    }
}
