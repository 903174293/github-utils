package com.github.binarywang.utils;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.testng.collections.Lists;

import io.restassured.RestAssured;
import io.restassured.internal.path.xml.NodeImpl;
import io.restassured.path.xml.XmlPath;

/**
 * @author Binary Wang
 */
public class GithubUtils {
    static {
        RestAssured.baseURI = "https://github.com";
    }

    public static String getJoinedDate(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        XmlPath htmlPath = given().when().get("/{id}", id.trim()).then().extract()
            .htmlPath();
        checkValidation(htmlPath);
        String date = htmlPath
            .getString("**.find { it.@class == 'join-date' }.@datetime");
        //.log().everything() 
        return date;
    }

    private static void checkValidation(XmlPath htmlPath) {
        GithubAccountType accoutType = checkAccoutType(htmlPath);
        if (accoutType == GithubAccountType.NoneExist) {
            throw new RuntimeException("非法 GitHub Id");
        }

        if (accoutType == GithubAccountType.Org) {
            throw new RuntimeException("This is a GitHub Organization");
        }
    }

    public static GithubAccountType checkAccoutType(String id) {
        XmlPath htmlPath = given().when().get("/{id}", id.trim()).then()
            .extract().htmlPath();
        return checkAccoutType(htmlPath);
    }

    private static GithubAccountType checkAccoutType(XmlPath htmlPath) {
        String html = htmlPath.getString("html");
        if (html.contains("Not Found") || html.contains("Page not found")) {
            return GithubAccountType.NoneExist;
        }

        String orgName = htmlPath
            .getString("**.find { it.@class == 'org-name lh-condensed' }");

        if (orgName != null) {
            return GithubAccountType.Org;
        }

        return GithubAccountType.Personal;
    }

    public static List<String> getUserStarList(String id) {
        if (StringUtils.isBlank(id)) {
            return Collections.EMPTY_LIST;
        }

        XmlPath htmlPath = given().when().get("/{id}?tab=stars", id.trim())
            .then().extract().htmlPath();
        //.log().everything() 

        checkValidation(htmlPath);

        Object object = htmlPath.get("**.findAll "
            + "{ it.@class == 'd-inline-block mb-1' }"
            + ".h3.a.@href");
        System.err.println(id + "==>\n   " + object);

        if (object instanceof String) {
            return Lists.newArrayList(object.toString());
        }

        if (object instanceof ArrayList) {
            List<String> list = (ArrayList<String>) object;
            if (list.size() == 0) {
                throw new RuntimeException("this guy stars nothing");
            }

            return list;
        }

        return null;
    }


    private static List<String> getRepoStarListByPage(String repoName,
            int page) {
        XmlPath htmlPath = given().when()
            .get(repoName.trim() + "/stargazers?page={page}", page)
            .then().extract().htmlPath();

        Object object = htmlPath.get("**.findAll "
            + "{ it.@class == 'follow-list-name' }.span.a.@href");
        System.err
            .println(repoName + " ---> Page " + page + " ==>\n   " + object);

        if (object instanceof String) {
            return Lists.newArrayList(object.toString());
        }

        if (object instanceof ArrayList) {
            List<String> list = (ArrayList<String>) object;
            if (list.size() == 0) {
                throw new RuntimeException("there is no stargazer");
            }

            Object pagination = htmlPath
                .get("**.findAll " + "{ it.@class == 'pagination' }");
            if (pagination == null || (pagination instanceof ArrayList
                && ((ArrayList<?>) pagination).size() == 0)) {
                //没有分页，就这一页。
                return list;
            }

            //有分页
            NodeImpl nodes = (NodeImpl) pagination;
            NodeImpl spanNode = (NodeImpl) nodes.get("span");
            if (spanNode != null) {
                String spanValue = (String) spanNode.getValue();
                if (spanValue.equals("Next")) {
                    //说明没有下一页了，直接返回
                    return list;
                }
            } else {
                //spanNode如果为空，则说明分页里面Previous和Next都是链接，意味着有下一页
            }

            //将下一页的结果合并入本页
            list.addAll(getRepoStarListByPage(repoName, page + 1));

            return list;
        }

        return null;
    }

    public static List<String> getRepoStarList(String repoName) {
        if (StringUtils.isBlank(repoName)) {
            return Collections.EMPTY_LIST;
        }
        
        return getRepoStarListByPage(repoName, 1);
    }
}
