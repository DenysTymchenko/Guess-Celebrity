package com.example.guesscelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private int random;
    private String HTML;
    private String[] names = new String[100];
    private String[] photos = new String[100];
    private final String url = "https://www.imdb.com/list/ls052283250/";
    private int score = 0;
    private TextView textViewScore;
    private EditText editTextTextPersonName;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewScore = findViewById(R.id.textViewScore);
        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        imageView = findViewById(R.id.imageView);

        GetHTML getHTML = new GetHTML();
        try {
            HTML = getHTML.execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Creation();
    }

    public void Creation() {
        textViewScore.setText(String.format(getString(R.string.textViewScore_text), score));
        int i = 0;

        Pattern photoPattern = Pattern.compile("height=\"209\"\n" + "src=\"(.*?)\"\n" + "width=\"140\" />");
        Matcher photoMatcher = photoPattern.matcher(HTML);
        while (photoMatcher.find()) {
            photos[i] = photoMatcher.group(1);
            i++;
        }

        Pattern namePattern = Pattern.compile("<img alt=\"(.*?)\"\n" + "height=\"209\"");
        Matcher nameMatcher = namePattern.matcher(HTML);
        i = 0;
        while (nameMatcher.find()) {
            names[i] = nameMatcher.group(1);
            i++;
        }

        random = (int) (Math.random() * 99 + 1);
        getImage getImage = new getImage();
        try {
            imageView.setImageBitmap(getImage.execute(photos[random]).get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void Guess(View view) {
        if (editTextTextPersonName.getText().toString().equals(names[random])) {
            score++;
            Toast.makeText(getApplicationContext(), R.string.right_guess_text, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), R.string.wrong_guess_text, Toast.LENGTH_SHORT).show();
        }
        Creation();
    }

    protected static class GetHTML extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder stringBuilder = new StringBuilder();
            URL url = null;
            HttpURLConnection httpURLConnection = null;

            try {
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();

                while (line != null) {
                    stringBuilder.append(line).append("\n");
                    line = bufferedReader.readLine();
                }

                return stringBuilder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }
    }

    protected static class getImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }
    }
}