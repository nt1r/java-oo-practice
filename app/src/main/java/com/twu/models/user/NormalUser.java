package com.twu.models.user;

import android.system.ErrnoException;

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
    public boolean login() {
        // just return true :D
        return true;
    }

    /* getters and setters */

    public int getVoteCount() {
        return voteCount;
    }
    /* getters and setters */

    public void vote(News news, int voteTimes) {
        if (voteCount < voteTimes) {
            throw new RuntimeException("Vote times invalid.");
        }
        voteCount -= voteTimes;
        news.voted(voteTimes);
    }

    public void bid(News news, int rank, int price) {
        News onRankedNews = DataContainer.getInstance().getNewsList().get(rank - 1);
        // the news has not been paid in this rank
        if (!onRankedNews.getPaidByUser()) {
            news.setPaidByUser(true);
            news.setPaidRank(rank);
            news.setPaidPrice(price);
            // DataContainer.getInstance().sortNews();
        } else if (onRankedNews.getPaidPrice() < price) {
            // the rank has been paid
            news.setPaidByUser(true);
            news.setPaidRank(rank);
            news.setPaidPrice(price);
            DataContainer.getInstance().getNewsList().remove(onRankedNews);
        } else {
            throw new Error("Unknown error, bid failed.");
        }
    }
}
