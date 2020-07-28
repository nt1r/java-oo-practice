package com.twu.models.user;

import com.twu.models.DataContainer;
import com.twu.models.news.News;
import com.twu.models.news.SuperNews;

public class AdminUser extends User {
    private String password;

    public AdminUser(String name, String password) {
        this.name = name;
        this.password = password;
    }

    /**
     * Login with password.
     */
    @Override
    public boolean login() {
        // check if password is correct
        // TODO: 2020/7/27 check password
        return true;
    }

    private void addSuperNews(SuperNews superNews) {
        DataContainer.getInstance().getNewsList().add(superNews);
    }

    private News createSuperNewsWithContent(String newsContent) {
        return new SuperNews(newsContent);
    }
}
