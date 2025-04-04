package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

public class ProgressManager {
    private SharedPreferences prefs;
    private static final String PREF_NAME = "LearningProgress";
    private static final String SESSION_COUNT = "session_count";

    public ProgressManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void addSession(LearningSession session) {
        int count = prefs.getInt(SESSION_COUNT, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("session_topic_" + count, session.getTopic());
        editor.putLong("session_time_" + count, session.getTimeTaken());
        editor.putInt("session_score_" + count, session.getQuizScore());
        editor.putInt(SESSION_COUNT, count + 1);
        editor.apply();
    }

    public void updateLastSessionScore(int score) {
        int count = prefs.getInt(SESSION_COUNT, 0);
        if (count > 0) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("session_score_" + (count - 1), score);
            editor.apply();
        }
    }

    public int getCompletedSessionsCount() {
        return prefs.getInt(SESSION_COUNT, 0);
    }

    public List<LearningSession> getSessionHistory() {
        List<LearningSession> history = new ArrayList<>();
        int count = prefs.getInt(SESSION_COUNT, 0);
        for (int i = 0; i < count; i++) {
            String topic = prefs.getString("session_topic_" + i, "");
            long time = prefs.getLong("session_time_" + i, 0);
            int score = prefs.getInt("session_score_" + i, 0);
            history.add(new LearningSession(topic, time, score));
        }
        return history;
    }
}