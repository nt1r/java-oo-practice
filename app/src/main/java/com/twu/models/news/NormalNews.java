package com.twu.models.news;

public class NormalNews extends News {
    private double voteRate = 1.0d;

    public NormalNews(String content) {
        super(content);
    }

    protected NormalNews(int id, String content, int hotScore, boolean paidByUser, int paidPrice, int paidRank) {
        super(id, content, hotScore, paidByUser, paidPrice, paidRank);
    }

    @Override
    public void voted(int times) {
        // System.out.println("Normal news voted for " + times + " times.");
        super.increaseHotScore(times, this.voteRate);
    }

    @Override
    public News clone() {
        return new NormalNews(id, content, hotScore, paidByUser, paidPrice, paidRank);
    }
}
