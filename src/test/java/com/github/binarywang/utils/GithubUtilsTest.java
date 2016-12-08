package com.github.binarywang.utils;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.Lists;


/**
 * @author Binary Wang
 */
public class GithubUtilsTest {
    private long startTime = System.currentTimeMillis();

    @DataProvider
    public Object[][] data() {
        return new Object[][] { { "sd" } };
    }


    @Test(dataProvider = "data")
    public void testCheckAccoutType(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }

        System.out.println(
            (System.currentTimeMillis() - this.startTime) + " Processing  "
                + id);

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
