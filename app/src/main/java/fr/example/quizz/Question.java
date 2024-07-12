package fr.example.quizz;

import java.util.List;

public class Question {
    private String question;
    private List<String> options;
    private int correctAnswer;
    private boolean isImageQuestion; // Ajout d'un champ pour indiquer le type de question

    // Constructeur unique
    public Question(String question, List<String> options, int correctAnswer, boolean isImageQuestion) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.isImageQuestion = isImageQuestion;
    }

    // Getters et setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public boolean isImageQuestion() {
        return isImageQuestion;
    }

    public void setImageQuestion(boolean imageQuestion) {
        isImageQuestion = imageQuestion;
    }

    public boolean isAnswerCorrect(int selectedOption) {
        return selectedOption == correctAnswer;
    }
}
