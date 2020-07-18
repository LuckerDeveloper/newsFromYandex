package com.git;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private List<String> newsList = new ArrayList<>();
    private Map<String, String> newsMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new ArrayAdapter( this, android.R.layout.simple_list_item_1, newsList );
        ListView listView= findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(newsMap.get(newsList.get(position))));
                startActivity(intent);
            }
        });
        execute();
    }

    public void ClickMe(View v) {
        execute();
    }

    private static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void execute(){
        if (isOnline(this)){
            new MyTask().execute();
        } else {
            Toast.makeText(this, "Отсутствует подключение к интернету", Toast.LENGTH_LONG).show();
        }
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            newsMap.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String url = "https://yandex.ru/";
                Document document = Jsoup.connect(url).get();
                Elements listNews = document.select("div#news_panel_news.news__panel.mix-tabber-slide2__panel");

                for (Element element : listNews.select("a")) {
                    String link = element.attr("href"); //ссылка
                    String news = element.text(); //новость
                    newsMap.put(news, link);
                }
            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            newsList.clear();
            newsList.addAll(newsMap.keySet());
            adapter.notifyDataSetChanged();
        }
    }


}
