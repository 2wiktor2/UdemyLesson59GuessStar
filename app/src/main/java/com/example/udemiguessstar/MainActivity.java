package com.example.udemiguessstar;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button button0;
    Button button1;
    Button button2;
    Button button3;

    ImageView imageViewStar;
    private String url = "http://www.posh24.se/kandisar";

    private ArrayList<String> urls;
    private ArrayList<String> names;
    private ArrayList<Button> buttons;

    private int numberOfQustion;
    private int numberOfRightAnswer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        buttons = new ArrayList<>();
        buttons.add(button0);
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);


        imageViewStar = findViewById(R.id.imageViewStar);
        urls = new ArrayList<>();
        names = new ArrayList<>();
        getContent();
    }

    private void getContent() {
        DounloadContentTask task = new DounloadContentTask();
        try {
            String content = task.execute(url).get();
            String start = "<p class=\"link\">Topp 100 kändisar</p>";
            String finish = "<div class=\"col-xs-12 col-sm-6 col-md-4\">";
            Pattern pattern = Pattern.compile(start + "(.*?)" + finish);
            Matcher matcher = pattern.matcher(content);
            String splitContent = "";
            while (matcher.find()) {
                splitContent = matcher.group(1);
            }
            Log.i("qwerty", splitContent);
            Pattern patternImg = Pattern.compile("<img src=\"(.*?)\"");
            Pattern patternName = Pattern.compile("alt=\"(.*?)\"/>");
            Matcher matcherImg = patternImg.matcher(splitContent);
            Matcher matcherName = patternName.matcher(splitContent);
            while (matcherImg.find()) {
                urls.add(matcherImg.group(1));
            }
            while (matcherName.find()) {
                names.add(matcherName.group(1));
            }
            // Проверка
            for (String s : names) {
                Log.i("qwerty", s);
            }
            for (String s : urls) {
                Log.i("qwerty", s);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void playGame() {
generateQuestion();
DounloadImageTask task = new DounloadImageTask();
        try {
            Bitmap bitmap = task.execute(urls.get(numberOfQustion)).get();
            if (bitmap !=null){
                imageViewStar.setImageBitmap(bitmap);
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // генерирует случайным образом вопрос
    private void generateQuestion() {
    // генерируем номер вопроса от 0 до колличества элементов в массиве
       numberOfQustion = (int) (Math.random() * names.size());
       numberOfRightAnswer = (int) (Math.random() * buttons.size());

    }

    private static class DounloadContentTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return null;
        }
    }

    private static class DounloadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }
}
