package com.example.hiringfetchapi;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    //backing list of lines (headers + items)
    private final List<String> displayList = new ArrayList<>();
    private SectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // wire up RecyclerView
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SectionAdapter(displayList);
        rv.setAdapter(adapter);

        fetchAndShow();
    }

    private void fetchAndShow() {
        OkHttpClient client = new OkHttpClient();
        Request req = new Request.Builder()
                .url("https://hiring.fetch.com/hiring.json")
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //  show a quick Toast on error
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this,
                                        "Network error: " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(MainActivity.this,
                                            "Server error: " + response.code(),
                                            Toast.LENGTH_SHORT)
                                    .show()
                    );
                    return;
                }

                String json = response.body().string();
                //  parse into a plain array
                Item[] items = new Gson().fromJson(json, Item[].class);

                // filter out blank or null names
                List<Item> good = new ArrayList<>();
                for (Item it : items) {
                    if (it.name != null && !it.name.trim().isEmpty()) {
                        good.add(it);
                    }
                }

                //  sort by listId, then name
                Item[] arr = good.toArray(new Item[0]);
                Arrays.sort(arr, (a, b) -> {
                    if (a.listId != b.listId) {
                        return a.listId - b.listId;
                    }
                    return a.name.compareTo(b.name);
                });

                // build  displayList with headers
                displayList.clear();
                int current = -1;
                for (Item it : arr) {
                    if (it.listId != current) {
                        current = it.listId;
                        displayList.add("List " + current + ":");
                    }
                    displayList.add("• " + it.name);
                }

                // tell adapter on main thread
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            }
        });
    }
}
