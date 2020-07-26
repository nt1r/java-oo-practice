package com.twu.models.user;

import com.twu.models.DataContainer;
import com.twu.models.news.News;

public class NormalUser extends User {
    private int voteCount = 10;

    public NormalUser(String name) {
        this.name = name;
    }

    /**
     * Login without password.
     */
    @Override
    public void login() {

    }

    public void vote(News news, int voteTimes) {
        if (voteCount < voteTimes) {
            // TODO: 2020/7/26 vote count not enough
            return;
        }
        voteCount -= voteTimes;
        news.voted(voteTimes);
    }

    public void payForTop(News news, int rank, int price) {
        // the news has not been paid in this rank
        if (news.getPaidRank() == rank && news.getPaidPrice() < price || !news.getPaidByUser()) {
            news.setPaidByUser(true);
            news.setPaidRank(rank);
            news.setPaidPrice(price);
            //DataContainer.getInstance().sortNews();
        } else {
            // TODO: 2020/7/26 pay failed.
        }
    }
}
