package com.example.myapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class QuizFragment extends Fragment {
    private String topic;
    private String videoTitle;
    private String videoDescription;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int correctAnswers = 0;
    private static final int TOTAL_QUESTIONS = 10;
    private static final int PASS_THRESHOLD = 8;

    private static class Question {
        String questionText;
        List<String> options;
        String correctAnswer;

        Question(String questionText, List<String> options, String correctAnswer) {
            this.questionText = questionText;
            this.options = options;
            this.correctAnswer = correctAnswer;
        }
    }

    public QuizFragment(String topic) {
        this.topic = topic;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        TextView questionText = view.findViewById(R.id.question_text);
        RadioGroup optionsGroup = view.findViewById(R.id.options_group);
        Button submitButton = view.findViewById(R.id.submit_button);

        // Get data from arguments
        Bundle args = getArguments();
        videoTitle = args != null ? args.getString("videoTitle", "No Title") : "No Title";
        videoDescription = args != null ? args.getString("videoDescription", "No Description") : "No Description";

        // Generate 10 questions
        questions = generateQuestions();

        // Display first question
        displayQuestion(questionText, optionsGroup);

        submitButton.setOnClickListener(v -> {
            int selectedId = optionsGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {
                String selectedAnswer = ((RadioButton) view.findViewById(selectedId)).getText().toString();
                Question currentQuestion = questions.get(currentQuestionIndex);
                boolean isCorrect = selectedAnswer.equals(currentQuestion.correctAnswer);
                if (isCorrect) correctAnswers++;
                Toast.makeText(getActivity(), isCorrect ? "Correct!" : "Wrong! Correct answer: " + currentQuestion.correctAnswer, Toast.LENGTH_SHORT).show();

                currentQuestionIndex++;
                if (currentQuestionIndex < TOTAL_QUESTIONS) {
                    displayQuestion(questionText, optionsGroup);
                } else {
                    String result = correctAnswers >= PASS_THRESHOLD ? "Pass! (" + correctAnswers + "/" + TOTAL_QUESTIONS + ")" : "Fail! (" + correctAnswers + "/" + TOTAL_QUESTIONS + ")";
                    Toast.makeText(getActivity(), "Quiz completed! " + result, Toast.LENGTH_LONG).show();
                    getParentFragmentManager().popBackStack();
                }
            } else {
                Toast.makeText(getActivity(), "Select an answer", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private List<Question> generateQuestions() {
        List<Question> questionList = new ArrayList<>();
        List<String> words = Arrays.asList(videoDescription.split("\\s+")); // Split description into words
        List<String> dummyTopics = Arrays.asList("Machine Learning", "Web Development", "Database Systems", "Networking");

        // Q1: Main topic from title
        List<String> q1Options = new ArrayList<>(dummyTopics);
        q1Options.add(topic);
        Collections.shuffle(q1Options);
        questionList.add(new Question(
                "What is the main topic of the video titled '" + videoTitle + "'?",
                q1Options,
                topic
        ));

        // Q2: First keyword from description
        String keyword1 = words.size() > 0 ? words.get(0) : "programming";
        List<String> q2Options = Arrays.asList("Yes", "No", "Maybe", "Not sure");
        Collections.shuffle(q2Options);
        questionList.add(new Question(
                "Does the video mention '" + keyword1 + "' in its description?",
                new ArrayList<>(q2Options),
                "Yes"
        ));

        // Q3: Video purpose
        List<String> q3Options = Arrays.asList("Tutorial", "Entertainment", "News", "Review");
        Collections.shuffle(q3Options);
        questionList.add(new Question(
                "What is the likely purpose of the video '" + videoTitle + "'?",
                q3Options,
                "Tutorial"
        ));

        // Q4: Second keyword
        String keyword2 = words.size() > 1 ? words.get(1) : "code";
        List<String> q4Options = Arrays.asList(keyword2, "Random", "Unrelated", "None");
        Collections.shuffle(q4Options);
        questionList.add(new Question(
                "Which term is most likely emphasized in '" + videoTitle + "'?",
                q4Options,
                keyword2
        ));

        // Q5: Topic component
        List<String> q5Options = new ArrayList<>(dummyTopics);
        q5Options.add(topic.split(" ")[0]);
        Collections.shuffle(q5Options);
        questionList.add(new Question(
                "Which concept is central to '" + videoTitle + "'?",
                q5Options,
                topic.split(" ")[0]
        ));

        // Q6: Video length assumption
        List<String> q6Options = Arrays.asList("5-10 minutes", "10-20 minutes", "20-30 minutes", "Over 30 minutes");
        Collections.shuffle(q6Options);
        questionList.add(new Question(
                "What is a typical length for a '" + topic + "' tutorial video?",
                q6Options,
                "10-20 minutes"
        ));

        // Q7: Third keyword
        String keyword3 = words.size() > 2 ? words.get(2) : "learn";
        List<String> q7Options = Arrays.asList("True", "False", "Unclear", "Not mentioned");
        Collections.shuffle(q7Options);
        questionList.add(new Question(
                "Is '" + keyword3 + "' a focus of the video based on its description?",
                q7Options,
                "True"
        ));

        // Q8: Subject taught
        List<String> q8Options = Arrays.asList(topic, "Cooking", "Music Theory", "Physics");
        Collections.shuffle(q8Options);
        questionList.add(new Question(
                "Which subject does '" + videoTitle + "' teach?",
                q8Options,
                topic
        ));

        // Q9: Description length
        List<String> q9Options = Arrays.asList("Short", "Medium", "Long", "None");
        Collections.shuffle(q9Options);
        questionList.add(new Question(
                "How would you describe the length of the video description?",
                q9Options,
                videoDescription.length() > 50 ? "Medium" : "Short"
        ));

        // Q10: Learning outcome
        List<String> q10Options = Arrays.asList(
                "Understand " + topic,
                "Build a website",
                "Cook a meal",
                "Play an instrument"
        );
        Collections.shuffle(q10Options);
        questionList.add(new Question(
                "What might you learn from watching '" + videoTitle + "'?",
                q10Options,
                "Understand " + topic
        ));

        return questionList;
    }

    private void displayQuestion(TextView questionText, RadioGroup optionsGroup) {
        Question q = questions.get(currentQuestionIndex);
        questionText.setText("Question " + (currentQuestionIndex + 1) + "/" + TOTAL_QUESTIONS + ": " + q.questionText);
        ((RadioButton) optionsGroup.getChildAt(0)).setText(q.options.get(0));
        ((RadioButton) optionsGroup.getChildAt(1)).setText(q.options.get(1));
        ((RadioButton) optionsGroup.getChildAt(2)).setText(q.options.get(2));
        ((RadioButton) optionsGroup.getChildAt(3)).setText(q.options.get(3));
        optionsGroup.clearCheck();
    }
}