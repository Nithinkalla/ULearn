package com.example.myapplication;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class LearningFragment extends Fragment {
    private static final String TAG = "LearningFragment";
    private long startTime;
    private TextView contentText, wikiLink, youtubeLink, libraryLink, timeEstimate, codeText;
    private final OkHttpClient client = new OkHttpClient();
    private static final String[] TOPICS = {
            "Introduction to Programming", "Control Structures", "Functions in Programming",
            "Object-Oriented Programming", "Advanced OOP", "Data Structures",
            "Algorithms", "File Handling", "Exception Handling", "Multithreading","Basic Codes"
    };
    private static final String YOUTUBE_API_KEY = "AIzaSyBvLKaM8QIanL3_AGMemEhbHvs9uTWMGaI"; // Replace with your key
    private String selectedTopic = TOPICS[0];
    private String youtubeUrl = "";
    private String videoTitle = "";
    private String videoDescription = "";

    // Static code snippets (replace with your own)
    private static final Map<String, String> CODE = new HashMap<>();
    // Static file URLs for download (replace with your actual URLs)
    private static final Map<String, String> FILE_URLS = new HashMap<>();
    static {
        CODE.put("Introduction to Programming", "public class HelloWorld {\n    public static void main(String[] args) {\n        System.out.println(\"Hello, World!\");\n    }\n}");
        FILE_URLS.put("Introduction to Programming", "https://drive.google.com/file/d/1S35iAL8THxnS_koDU3Z4pqjzpsnPp8xV/view?usp=sharing");
        CODE.put("Control Structures", "int x = 5;\nif (x > 0) {\n    System.out.println(\"Positive\");\n}");
        FILE_URLS.put("Control Structures", "https://drive.google.com/file/d/1JTz1blOOZCJcLFKnbM3immuAg-F51E2Z/view?usp=sharing");
        CODE.put("Functions in Programming", "Functions are reusable blocks of code that take input, perform operations, and return output.");
        FILE_URLS.put("Functions in Programming", "https://drive.google.com/file/d/1JTz1blOOZCJcLFKnbM3immuAg-F51E2Z/view?usp=sharing");
        CODE.put("Object-Oriented Programming", "Object-Oriented Programming (OOP) is a programming paradigm that organizes code into objects containing data (attributes) and behavior (methods).");
        FILE_URLS.put("Object-Oriented Programming", "https://drive.google.com/file/d/1JTz1blOOZCJcLFKnbM3immuAg-F51E2Z/view?usp=sharing");
        CODE.put("Advanced OOP", "Advanced OOP includes concepts like inheritance, polymorphism, encapsulation, and abstraction to enhance code reusability, flexibility, and security.");
        FILE_URLS.put("Advanced OOP", "https://drive.google.com/file/d/1JTz1blOOZCJcLFKnbM3immuAg-F51E2Z/view?usp=sharing");
        CODE.put("Data Structures", "Data structures are ways to organize, store, and manage data efficiently for easy access and modification.");
        FILE_URLS.put("Data Structures", "https://drive.google.com/file/d/1JTz1blOOZCJcLFKnbM3immuAg-F51E2Z/view?usp=sharing");
        CODE.put("Algorithms", "Algorithms are step-by-step procedures or rules for solving a specific problem efficiently.");
        FILE_URLS.put("Algorithms", "https://drive.google.com/file/d/1nO0DNa1Y9tLXrTYY4ZXmDbLIO_DCwtLt/view?usp=sharing");
        CODE.put("File Handling", "File handling allows reading, writing, and managing files to store and retrieve data persistently.");
        FILE_URLS.put("File Handling", "https://drive.google.com/file/d/1JTz1blOOZCJcLFKnbM3immuAg-F51E2Z/view?usp=sharing");
        CODE.put("Exception Handling", "Exception handling is a mechanism to detect, handle, and manage runtime errors gracefully without crashing the program.");
        FILE_URLS.put("Exception Handling", "https://drive.google.com/file/d/1JTz1blOOZCJcLFKnbM3immuAg-F51E2Z/view?usp=sharing");
        CODE.put("Multithreading", "Multithreading allows multiple threads to run concurrently, improving performance and responsiveness in programs.");
        FILE_URLS.put("Multithreading", "https://drive.google.com/file/d/1JTz1blOOZCJcLFKnbM3immuAg-F51E2Z/view?usp=sharing");
        CODE.put("Basic Codes", "Basic Codes");
        FILE_URLS.put("File Handling", "https://drive.google.com/file/d/1_txooAIekcJ-rKSHxG65t5guSuFsc6kf/view?usp=sharing");



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Creating LearningFragment view");
        View view = inflater.inflate(R.layout.fragment_learning, container, false);

        contentText = view.findViewById(R.id.learning_content);
        wikiLink = view.findViewById(R.id.wiki_link);
        youtubeLink = view.findViewById(R.id.youtube_link);
        libraryLink = view.findViewById(R.id.library_link);
        timeEstimate = view.findViewById(R.id.time_estimate);
        codeText = view.findViewById(R.id.code_text);
        Button downloadButton = view.findViewById(R.id.download_button);
        Button finishButton = view.findViewById(R.id.finish_button);
        Spinner topicSpinner = view.findViewById(R.id.topic_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, TOPICS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topicSpinner.setAdapter(adapter);
        topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTopic = TOPICS[position];
                Log.d(TAG, "Selected topic: " + selectedTopic);
                fetchLearningContent(selectedTopic);
                updateCodeAndDownload();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        timeEstimate.setText("Estimated Time: Loading...");
        startTime = System.currentTimeMillis();
        wikiLink.setMovementMethod(LinkMovementMethod.getInstance());
        youtubeLink.setMovementMethod(LinkMovementMethod.getInstance());
        libraryLink.setMovementMethod(LinkMovementMethod.getInstance());
        fetchLearningContent(selectedTopic);
        updateCodeAndDownload();

        downloadButton.setOnClickListener(v -> {
            String fileUrl = FILE_URLS.getOrDefault(selectedTopic, "");
            if (!fileUrl.isEmpty()) {
                downloadFile(fileUrl, selectedTopic.replace(" ", "_") + ".pdf");
                Toast.makeText(getActivity(), "Downloading " + selectedTopic + " notes...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "No file available for " + selectedTopic, Toast.LENGTH_SHORT).show();
            }
        });

        finishButton.setOnClickListener(v -> {
            Log.d(TAG, "Finish button clicked");
            long timeTaken = (System.currentTimeMillis() - startTime) / 1000;
            Log.d(TAG, "Time taken: " + timeTaken + " seconds");

            try {
                QuizFragment quizFragment = new QuizFragment(selectedTopic);
                Bundle args = new Bundle();
                args.putString("videoTitle", videoTitle);
                args.putString("videoDescription", videoDescription);
                args.putString("code", CODE.getOrDefault(selectedTopic, ""));
                quizFragment.setArguments(args);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, quizFragment)
                        .addToBackStack(null)
                        .commit();
                Log.d(TAG, "Fragment transaction committed");
            } catch (Exception e) {
                Log.e(TAG, "Error in fragment transition: " + e.getMessage(), e);
                Toast.makeText(getActivity(), "Error loading quiz: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void fetchLearningContent(String topic) {
        StringBuilder content = new StringBuilder();

        String wikiUrl = "https://en.wikipedia.org/api/rest_v1/page/summary/" + topic.replace(" ", "_");
        Request wikiRequest = new Request.Builder().url(wikiUrl).build();
        client.newCall(wikiRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                appendContent(content, "Wikipedia: Failed to load - " + e.getMessage());
                setWikiLink(topic, "");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        if (json.has("extract")) {
                            String extract = json.getString("extract");
                            appendContent(content, "Wikipedia: " + extract);
                            setWikiLink(topic, "https://en.wikipedia.org/wiki/" + topic.replace(" ", "_"));
                        }
                    } catch (Exception e) {
                        appendContent(content, "Wikipedia: Error parsing - " + e.getMessage());
                    }
                }
                response.close();
            }
        });

        String youtubeSearchUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + topic.replace(" ", "+") + "+tutorial&type=video&key=" + YOUTUBE_API_KEY;
        Request youtubeSearchRequest = new Request.Builder().url(youtubeSearchUrl).build();
        client.newCall(youtubeSearchRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                appendContent(content, "YouTube: Failed to load - " + e.getMessage());
                setYouTubeLink("");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        JSONArray items = json.getJSONArray("items");
                        if (items.length() > 0) {
                            JSONObject item = items.getJSONObject(0);
                            String videoId = item.getJSONObject("id").getString("videoId");
                            youtubeUrl = "https://www.youtube.com/watch?v=" + videoId;
                            videoTitle = item.getJSONObject("snippet").getString("title");
                            videoDescription = item.getJSONObject("snippet").getString("description");
                            appendContent(content, "YouTube: " + videoTitle);
                            setYouTubeLink(youtubeUrl);
                            fetchVideoDuration(videoId);
                        }
                    } catch (Exception e) {
                        appendContent(content, "YouTube: Error parsing - " + e.getMessage());
                    }
                }
                response.close();
            }
        });

        String openLibraryUrl = "https://openlibrary.org/search.json?q=" + topic.replace(" ", "+") + "+programming";
        Request olRequest = new Request.Builder().url(openLibraryUrl).build();
        client.newCall(olRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                appendContent(content, "Open Library: Failed to load - " + e.getMessage());
                setLibraryLink("");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        JSONArray docs = json.getJSONArray("docs");
                        if (docs.length() > 0) {
                            String key = docs.getJSONObject(0).getString("key");
                            String title = docs.getJSONObject(0).getString("title");
                            appendContent(content, "Open Library: '" + title + "'");
                            setLibraryLink("https://openlibrary.org" + key);
                        }
                    } catch (Exception e) {
                        appendContent(content, "Open Library: Error parsing - " + e.getMessage());
                    }
                }
                response.close();
            }
        });
    }

    private void fetchVideoDuration(String videoId) {
        String youtubeVideoUrl = "https://www.googleapis.com/youtube/v3/videos?part=contentDetails&id=" + videoId + "&key=" + YOUTUBE_API_KEY;
        Request request = new Request.Builder().url(youtubeVideoUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> timeEstimate.setText("Estimated Time: Unknown"));
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        JSONArray items = json.getJSONArray("items");
                        if (items.length() > 0) {
                            String duration = items.getJSONObject(0).getJSONObject("contentDetails").getString("duration");
                            int minutes = parseDurationToMinutes(duration);
                            int totalTime = minutes + 30;
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() -> timeEstimate.setText("Estimated Time: " + totalTime + " minutes"));
                            }
                        }
                    } catch (Exception e) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> timeEstimate.setText("Estimated Time: Unknown"));
                        }
                    }
                }
                response.close();
            }
        });
    }

    private int parseDurationToMinutes(String duration) {
        Pattern pattern = Pattern.compile("PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?");
        Matcher matcher = pattern.matcher(duration);
        if (matcher.matches()) {
            int hours = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : 0;
            int minutes = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0;
            int seconds = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : 0;
            return (hours * 60) + minutes + (seconds / 60);
        }
        return 0;
    }

    private void appendContent(StringBuilder content, String text) {
        if (getActivity() != null) {
            content.append(text).append("\n\n");
            getActivity().runOnUiThread(() -> contentText.setText(content.toString()));
        }
    }

    private void setWikiLink(String topic, String url) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> wikiLink.setText(url.isEmpty() ? "Wikipedia: No link available" : android.text.Html.fromHtml("<a href=\"" + url + "\">Read more on Wikipedia</a>")));
        }
    }

    private void setYouTubeLink(String url) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> youtubeLink.setText(url.isEmpty() ? "YouTube: No link available" : android.text.Html.fromHtml("<a href=\"" + url + "\">Watch on YouTube</a>")));
        }
    }

    private void setLibraryLink(String url) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> libraryLink.setText(url.isEmpty() ? "Open Library: No link available" : android.text.Html.fromHtml("<a href=\"" + url + "\">Read more on Open Library</a>")));
        }
    }

    private void updateCodeAndDownload() {
        String code = CODE.getOrDefault(selectedTopic, "No code available for this topic.");
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> codeText.setText("Code:\n" + code));
        }
    }

    private void downloadFile(String url, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setTitle(fileName)
                .setDescription("Downloading " + selectedTopic + " notes")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, fileName)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true);

        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
    }
}