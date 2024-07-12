package fr.example.quizz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private EditText nameEditText;
    private Button startButton;
    private Button multiJouerButton; // Ajout du bouton multi-joueur

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des vues
        nameEditText = findViewById(R.id.nameEditText);
        startButton = findViewById(R.id.startButton);
        TextView welcomeText = findViewById(R.id.welcomeText);
        multiJouerButton = findViewById(R.id.multiJouerButton); // Initialisation du bouton multi-joueur

        // Appliquer la police "Permanent Marker"
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/permanent_marker_regular.ttf");
        nameEditText.setTypeface(typeface);
        startButton.setTypeface(typeface);
        welcomeText.setTypeface(typeface);
        multiJouerButton.setTypeface(typeface);

        // Configuration de l'écouteur du bouton Commencer
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = nameEditText.getText().toString().trim(); // Trim pour enlever les espaces inutiles
                if (!userName.isEmpty()) {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("userName", userName);
                    editor.apply();

                    Intent intent = new Intent(MainActivity.this, ThemesActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Entrer votre nom", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Configuration de l'écouteur du bouton Multi-joueur
        multiJouerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MultiplayerActivity.class);
                startActivity(intent);
            }
        });
    }
}
