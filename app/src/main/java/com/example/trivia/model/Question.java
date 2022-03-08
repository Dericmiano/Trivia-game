package com.example.trivia.model;

public class Question {
    private String answer;
    private Boolean answerTrue;

    public Question(String answer, Boolean answerTrue) {
        this.answer = answer;
        this.answerTrue = answerTrue;
    }

    public Question() {
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Boolean isAnswerTrue() {
        return answerTrue;
    }

    public void setAnswerTrue(Boolean answerTrue) {
        this.answerTrue = answerTrue;
    }

    @Override
    public String toString() {
        return "Question{" +
                "answer='" + answer + '\'' +
                ", answerTrue=" + answerTrue +
                '}';
    }


}
