package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia.controller.AppController;
import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.Repository;
import com.example.trivia.databinding.ActivityMainBinding;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;
import com.example.trivia.util.Prefs;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private int currentQuestionIndex = 0;
    private int scoreCounter =0;
    private Score score;
    private Prefs prefs;
    List<Question> questionList;
    private Button buttonShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        score = new Score();
        prefs= new Prefs(MainActivity.this);
        buttonShare = findViewById(R.id.share_button);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareButton();
            }
        });
        currentQuestionIndex = prefs.getState();
        Log.d("high", "onCreate: "+prefs.getHighestScore());
        binding.scoreText.setText(MessageFormat.format("Highest score {0}",
                String.valueOf(prefs.getHighestScore())));

        binding.scoreTextView.setText(MessageFormat.format("Current score: {0}",
                String.valueOf(score.getScore())));
       questionList = new Repository().getQuestions(questionArrayList -> {
           binding.questionTextView.setText(questionArrayList.get(currentQuestionIndex)
                   .getAnswer());
           updateCounter((ArrayList<Question>) questionList);
//                Log.d("TAG1", "onCreate: "+questionArrayList);

       });
        binding.buttonNext.setOnClickListener(v -> {
           getNextQuestion();
        });
        binding.buttonTrue.setOnClickListener(v -> {
            checkAnswer(true);
            updateQuestionMethod();

        });
        binding.buttonFalse.setOnClickListener(v -> {
            checkAnswer(false);
            updateQuestionMethod();

        });
    }
    public void getNextQuestion() {
        currentQuestionIndex = (currentQuestionIndex+1) % questionList.size();
        updateQuestionMethod();
    }
    private void checkAnswer(boolean userChoseCorrect) {
        boolean answer = questionList.get(currentQuestionIndex).isAnswerTrue();
        int snackMessageId = 0;
        if (userChoseCorrect == answer){
            snackMessageId = R.string.correct_answer;
            fadeAnimation();
            addPoints();
//            prefs.saveHighestScore(scoreCounter);
//            Log.d("highest_score", "checkAnswer: "+prefs.getHighestScore());
        }else {
            snackMessageId = R.string.inCorrect;
            shakeAnimation();
            deductPoints();
        }
        Snackbar.make(binding.cardView, snackMessageId, Snackbar.LENGTH_SHORT)
                .show();
    }
    private void deductPoints() {
        scoreCounter = scoreCounter-100;
        if (scoreCounter >0 ){
            score.setScore(scoreCounter);
            binding.scoreTextView.setText(String.format("Current score: %s",
                    String.valueOf(score.getScore())));
        }else {
            scoreCounter = 0;
            score.setScore(scoreCounter);
        }
    }
    private void addPoints() {
        scoreCounter = scoreCounter + 100;
        score.setScore(scoreCounter);
        binding.scoreTextView.setText(MessageFormat.format("Current Score: {0}",
                String.valueOf(score.getScore())));
        Log.d("check", "updateScore: "+score.getScore());
    }
    private void updateCounter(ArrayList<Question> questionArrayList) {
        binding.textViewOutof.setText(MessageFormat.format("Test {0} /{1}",
                currentQuestionIndex, questionArrayList.size()));
    }
    public void fadeAnimation(){
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        binding.cardView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.WHITE);
                getNextQuestion();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void updateQuestionMethod() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        binding.questionTextView.setText(question);
        updateCounter((ArrayList<Question>) questionList);
    }
    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation);
        binding.cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionTextView.setTextColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionTextView.setTextColor(Color.WHITE);
                getNextQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }
    @Override
    protected void onPause() {
        prefs.saveHighestScore(score.getScore());
        prefs.setState(currentQuestionIndex);
        Log.d("state", "onPause: "+prefs.getState());
        Log.d("TAG", "onPause: "+prefs.getHighestScore());
        super.onPause();
    }

    private void shareButton(){
        String message = "my current score is: "+score.getScore()+ " and" +
                "My highest score is"+prefs.getHighestScore();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT,"I am playing Trivia");
        intent.putExtra(Intent.EXTRA_TEXT,message);
        startActivity(intent);


    }
}