package com.twu.models;

import com.twu.models.news.News;
import com.twu.models.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class DataContainer {
    private static DataContainer instance = null;
    private User user;
    private List<News> newsList = new ArrayList<>();
    private List<String> menuItemNameList = new ArrayList<>();
    private int idGenerator = 1;

    private DataContainer() {
    }

    /**
     * Singleton.
     *
     * @return the instance of singleton
     */
    public static DataContainer getInstance() {
        if (instance == null) {
            instance = new DataContainer();
        }

        return instance;
    }

    /* getters and setters */
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<News> getNewsList() {
        return newsList;
    }

    public void setNewsList(ArrayList<News> newsList) {
        this.newsList = newsList;
    }

    public int getIdGenerator() {
        return idGenerator++;
    }

    public List<String> getMenuItemNameList() {
        return menuItemNameList;
    }

    public void setMenuItemNameList(List<String> menuItemNameList) {
        this.menuItemNameList = menuItemNameList;
    }
    /* getters and setters */

    public void sortNews() {
        // deep copy first
        ArrayList<News> paidNewsList = new ArrayList<>();
        ArrayList<News> unPaidNewsList = new ArrayList<>();
        for (News news: newsList) {
            if (news.getPaidByUser()) {
                paidNewsList.add(news.clone());
            } else {
                unPaidNewsList.add(news.clone());
            }
        }

        // sort unpaid news by hot scores in descending order
        unPaidNewsList.sort(new Comparator<News>() {
            @Override
            public int compare(News news1, News news2) {
                return news2.getHotScore() - news1.getHotScore();
            }
        });

        // copy paid news
        ArrayList<Integer> paidRankList = new ArrayList<>();
        if (!paidNewsList.isEmpty()) {
            for (News news: paidNewsList) {
                // insert to the last position
                if (news.getPaidRank() > newsList.size()) {
                    paidRankList.add(newsList.size() - 1);
                    newsList.set(newsList.size() - 1, news);
                    // update rank
                    newsList.get(newsList.size() - 1).setPaidRank(newsList.size() - 1);
                } else {
                    paidRankList.add(news.getPaidRank() - 1);
                    newsList.set(news.getPaidRank() - 1, news);
                }
            }
        }

        // copy sorted unpaid news
        if (!unPaidNewsList.isEmpty()) {
            int insertIndex = 0;
            for (int i = 0; i < newsList.size(); ++i) {
                if (!paidRankList.contains(i)) {
                    newsList.set(i, unPaidNewsList.get(insertIndex++));
                }
            }
        }
    }

    public void printForDebug() {
        int index = 1;
        for (News news: newsList) {
            String output = String.format(Locale.CHINESE, "======%d======\n" +
                    "news name: %s\n" +
                    "hot score: %d\n" +
                    "paid rank: %d",
                    index++,
                    news.getContent(),
                    news.getHotScore(),
                    news.getPaidRank());
            System.out.println(output);
        }
    }

    public List<String> getAllNewsContents() {
        List<String> allNewsContents = new ArrayList<>();
        for (News news: newsList) {
            allNewsContents.add(news.getContent());
        }
        return allNewsContents;
    }
}
