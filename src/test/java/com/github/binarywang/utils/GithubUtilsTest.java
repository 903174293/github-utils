package com.github.binarywang.utils;

import static org.testng.Assert.assertTrue;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Binary Wang
 */
public class GithubUtilsTest {

    @DataProvider
    public Object[][] data() {
        return new Object[][] { { "abc" }, { "def" } };
    }

    @Test(dataProvider = "data")
    public void testGetJoinedDate(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }

        System.err.println(id + "   " + GithubUtils.getJoinedDate(id));
    }

    @Test(dataProvider = "data")
    public void testGetStarList(String id) {
        String must = "/wechat-group/weixin-java-tools";

        List<String> list = GithubUtils.getStarList(id);
        if (list.size() == 0) {
            throw new RuntimeException("this guy stars nothing");
        }

        assertTrue(list.contains(must),
            "star a lot, but not star the required one");
    }

}
