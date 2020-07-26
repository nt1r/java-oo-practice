package com.twu.models.news;

import com.twu.models.DataContainer;

public abstract class News {
    protected int id;
    protected String content;
    protected int hotScore = 0;
    protected boolean paidByUser = false;
    protected int paidPrice = 0;

    // start from 1
    protected int paidRank = 0;

    public News(String content) {
        this.id = DataContainer.getInstance().getIdGenerator();
        this.content = content;
    }

    public News(int id, String content, int hotScore, boolean paidByUser, int paidPrice, int paidRank) {
        this.id = id;
        this.content = content;
        this.hotScore = hotScore;
        this.paidByUser = paidByUser;
        this.paidPrice = paidPrice;
        this.paidRank = paidRank;
    }

    public abstract void voted(int times);

    protected void increaseHotScore(int voteTimes, double voteRate) {
        this.hotScore += voteTimes * voteRate;
        System.out.println(hotScore);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof News && this.content.equals(((News) obj).content);
    }

    @Override
    public int hashCode() {
        return this.content.hashCode();
    }

    @Override
    public abstract News clone();

    /* getters and setters */
    public int getHotScore() {
        return hotScore;
    }

    public int getPaidRank() {
        return paidRank;
    }

    public boolean getPaidByUser() {
        return paidByUser;
    }

    public int getPaidPrice() {
        return paidPrice;
    }

    public String getContent() {
        return content;
    }

    public void setPaidByUser(boolean paidByUser) {
        this.paidByUser = paidByUser;
    }

    public void setPaidPrice(int paidPrice) {
        this.paidPrice = paidPrice;
    }

    public void setPaidRank(int paidRank) {
        this.paidRank = paidRank;
    }
    /* getters and setters */
}
