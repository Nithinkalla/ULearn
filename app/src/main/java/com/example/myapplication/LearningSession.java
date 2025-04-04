package com.example.myapplication;

public class LearningSession {
    private String topic;
    private long timeTaken; // In seconds
    private int quizScore;

    public LearningSession(String topic, long timeTaken, int quizScore) {
        this.topic = topic;
        this.timeTaken = timeTaken;
        this.quizScore = quizScore;
    }

    public String getTopic() { return topic; }
    public long getTimeTaken() { return timeTaken; }
    public int getQuizScore() { return quizScore; }
    public void setQuizScore(int score) { this.quizScore = score; }
}