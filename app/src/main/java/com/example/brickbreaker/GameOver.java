package com.example.brickbreaker;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameOver extends AppCompatActivity {
    TextView tvPoints;
    ImageView ivNewHighest;
    TextView highScore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        ivNewHighest = findViewById(R.id.ivNewHighest);
        tvPoints = findViewById(R.id.tvPoints);
        highScore = findViewById((R.id.highScore));

        int points = getIntent().getExtras().getInt("points");
        if(points == 240) {
            ivNewHighest.setVisibility(View.VISIBLE);
        }

        tvPoints.setText("" + points);

        //region http get high score request
        RequestQueue volleyQueue = Volley.newRequestQueue(GameOver.this);
        String url = "http://192.168.56.1:4000/api/scores";

        Integer[] scoresArray = new Integer[3];

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonScores = (JSONArray) response.get("scores");
                            for (int i = 0; i < jsonScores.length(); i++) {
                                JSONObject scoreObject = jsonScores.getJSONObject(i);
                                scoresArray[i] = scoreObject.optInt("score");
                            }
                            Arrays.sort(scoresArray, Collections.reverseOrder());
                            highScore.setText(scoresArray[0].toString());

                        } catch (JSONException e) {
                            Log.e("json exception: ", e.toString());
                        }
                    }
                },
                (Response.ErrorListener) error -> {
                    Log.e("loadHighScore", error.toString());
                }
        );
        volleyQueue.add(jsonObjectRequest);
        //endregion
    }

    public void restart(View view) {
        Intent intent = new Intent(GameOver.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void exit(View view) {
        finish();
    }

}
