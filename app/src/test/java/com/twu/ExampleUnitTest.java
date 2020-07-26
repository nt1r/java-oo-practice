package com.twu;

import com.twu.models.DataContainer;
import com.twu.models.news.News;
import com.twu.models.news.NormalNews;
import com.twu.models.news.SuperNews;
import com.twu.models.user.NormalUser;
import com.twu.models.user.User;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void newsListTest() {
        NormalUser user1 = new NormalUser("leqi");

        News news1 = new NormalNews("领事馆关闭");
        user1.addNews(news1);

        News news2 = new NormalNews("碎尸案");
        user1.addNews(news2);

        News news3 = new SuperNews("日本演员自杀");
        user1.addNews(news3);

        News news4 = new NormalNews("蓬佩奥演讲");
        user1.addNews(news4);

        News news5 = new NormalNews("蔡英文演习");
        user1.addNews(news5);

        user1.vote(news3, 4);
        user1.vote(news4, 3);
        user1.vote(news5, 2);
        user1.payForTop(news2, 1, 100);

        DataContainer.getInstance().sortNews();
        DataContainer.getInstance().printForDebug();
    }
}