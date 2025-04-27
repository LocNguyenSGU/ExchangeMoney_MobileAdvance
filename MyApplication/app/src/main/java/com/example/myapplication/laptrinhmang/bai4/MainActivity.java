package com.example.myapplication.laptrinhmang.bai4;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private OkHttpClient client;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bai4_activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        client = new OkHttpClient();

        fetchPosts();
    }

    private void fetchPosts() {
        Request request = new Request.Builder()
                .url("https://jsonplaceholder.typicode.com/posts")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) return;

                String json = response.body().string();
                Type listType = new TypeToken<List<Post>>() {}.getType();
                List<Post> postList = new Gson().fromJson(json, listType);

                mainHandler.post(() -> {
                    postAdapter = new PostAdapter(postList);
                    recyclerView.setAdapter(postAdapter);
                });
            }
        });
    }
}
