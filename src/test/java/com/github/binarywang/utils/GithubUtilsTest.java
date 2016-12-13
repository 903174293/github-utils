package com.github.binarywang.utils;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.CollectionUtils;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * @author Binary Wang
 */
public class GithubUtilsTest {
    private long startTime = System.currentTimeMillis();

    @DataProvider
    public Object[][] data() {
        return new Object[][] { { "binarywang" } };
    }


    @Test(dataProvider = "data")
    public void testCheckAccoutType(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }

        System.out.println((System.currentTimeMillis() - this.startTime)
            + " Processing  " + id);

        GithubAccountType accoutType = GithubUtils.checkAccoutType(id);

        if (accoutType == GithubAccountType.NoneExist) {
            System.err.println(id + " not exist");
            fail(id + " not exist");
        }
    }

    @Test(dataProvider = "data")
    public void testGetJoinedDate(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }

        System.err.println(id + "   " + GithubUtils.getJoinedDate(id));
    }

    @Test(dataProvider = "data")
    public void testGetUserStarList(String id) {
        List<String> list = GithubUtils.getUserStarList(id);
        System.err.println(list);

        if (list.size() == 0) {
            throw new RuntimeException("this guy stars nothing");
        }

        String must = "/wechat-group/weixin-java-tools";
        assertTrue(list.contains(must),
            "star a lot, but not star the required one");
    }

    @DataProvider
    public Object[][] repoList() {
        return new Object[][] {
            { "/Wechat-Group/weixin-mp-demo" },
            { "/Wechat-Group/weixin-java-tools-springmvc" },
            { "/wechat-group/weixin-java-tools" },
            { "/chanjarster/weixin-java-tools" } };
    }

    @Test(dataProvider = "repoList")
    public void testGetRepoStarList(String repoName) {
        List<String> list = GithubUtils.getRepoStarList(repoName);

        if (list.size() == 0) {
            throw new RuntimeException("there is no stargazer");
        }

        System.out.println("repo:\n " + repoName);
        System.out.println("star list:\n    " + list);
    }

    @Test
    public void testCompareRepoStarList() {
        List<String> wechatGroup = GithubUtils
            .getRepoStarList("/wechat-group/weixin-java-tools");

        List<String> chanjarster = GithubUtils
            .getRepoStarList("/chanjarster/weixin-java-tools");

        List<String> common = Lists.newArrayList(wechatGroup);
        common.retainAll(chanjarster);
        System.out.println("common:\n   " + common);

        List<String> onlyInWechat = Lists.newArrayList(wechatGroup);
        onlyInWechat.removeAll(chanjarster);
        System.out.println(
            "more in wechat group:\n    " + onlyInWechat);
    }
}
