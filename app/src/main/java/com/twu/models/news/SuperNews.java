package com.twu.models.news;

import androidx.annotation.NonNull;

public class SuperNews extends News {
    private double voteRate = 2.0d;

    public SuperNews(String content) {
        super(content);
    }

    protected SuperNews(int id, String content, int hotScore, boolean paidByUser, int paidPrice, int paidRank) {
        super(id, content, hotScore, paidByUser, paidPrice, paidRank);
    }

    @Override
    public void voted(int times) {
        super.increaseHotScore(times, this.voteRate);
    }

    @Override
    public News clone() {
        return new SuperNews(id, content, hotScore, paidByUser, paidPrice, paidRank);
    }
}
