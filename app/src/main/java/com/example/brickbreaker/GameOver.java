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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameOver extends AppCompatActivity {
    TextView tvPoints;
    ImageView ivNewHighest;
    TextView tvhighScore;
    TextView tvhighScoreLabel;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        ivNewHighest = findViewById(R.id.ivNewHighest);
        tvPoints = findViewById(R.id.tvPoints);
        tvhighScore = findViewById((R.id.tvhighScore));
        tvhighScoreLabel = findViewById((R.id.tvhighScoreLabel));


        int points = getIntent().getExtras().getInt("points");
        if(points == 240) {
            ivNewHighest.setVisibility(View.VISIBLE);
        }

        tvPoints.setText("" + points);

        //region http get high score request
        RequestQueue volleyQueue = Volley.newRequestQueue(GameOver.this);
        String url = "http://192.168.56.1:4000/api/scores";

        List<Integer> scoresList = new ArrayList<Integer>();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonScores = (JSONArray) response.get("scores");
                            for (int i = 0; i < jsonScores.length(); i++) {
                                JSONObject scoreObject = jsonScores.getJSONObject(i);
                                scoresList.add(scoreObject.optInt("score"));
                            }
                            Collections.sort(scoresList);
                            Collections.reverse(scoresList);
                            Integer maxScore = scoresList.get(0);
                            if (points > maxScore) {
                                maxScore = points;
                                tvhighScoreLabel.setText("New High Score: ");
                                sendJsonPostRequest(maxScore);
                            }
                            tvhighScore.setText(maxScore.toString());

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

    private void sendJsonPostRequest(Integer newHighScore){
        String url = "http://192.168.56.1:4000/api/scores";
        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("id", 4);
            jsonParams.put("name", "Test User");
            jsonParams.put("score", newHighScore);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonParams,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("http post", response.toString());
                        }
                    },
                    (Response.ErrorListener) error -> {
                        Log.d("post params", jsonParams.toString());
                        Log.e("postNewHighScore", error.toString());
                        error.printStackTrace();
                    });
            Volley.newRequestQueue(getApplicationContext()).add(request);
        } catch(JSONException ex){
            Log.e("json post exception", ex.toString());
        }
    }
}
