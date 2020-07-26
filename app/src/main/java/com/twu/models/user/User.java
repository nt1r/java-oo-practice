package com.twu.models.user;

import com.twu.models.DataContainer;
import com.twu.models.interfaces.AddNewsInterface;
import com.twu.models.interfaces.LoginInterface;
import com.twu.models.interfaces.ViewRankingInterface;
import com.twu.models.news.News;
import com.twu.models.news.NormalNews;

public abstract class User implements LoginInterface, ViewRankingInterface, AddNewsInterface {
    protected String name;

    @Override
    public void viewRanking() {

    }

    /**
     * Add a news to {@code DataContainer}.
     *
     * @param news target news
     */
    @Override
    public void addNews(News news) {
        DataContainer.getInstance().getNewsList().add(news);
    }

    private News createNormalNewsWithContent(String newsContent) {
        return new NormalNews(newsContent);
    }
}
