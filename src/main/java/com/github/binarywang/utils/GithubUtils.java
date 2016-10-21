package com.github.binarywang.utils;

import static io.restassured.RestAssured.given;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.testng.collections.Lists;

import io.restassured.RestAssured;
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
        String html = htmlPath.getString("html");
        if (html.contains("Not Found") || html.contains("Page not found")) {
            throw new RuntimeException("非法GitHub Id");
        }
    }

    public static List<String> getStarList(String id) {
        if (StringUtils.isBlank(id)) {
            return Collections.EMPTY_LIST;
        }

        XmlPath htmlPath = given().when().get("/{id}?tab=stars", id.trim())
            .then().extract().htmlPath();
        //.log().everything() 

        checkValidation(htmlPath);

        Object object = htmlPath.get("**.findAll "
            + "{ it.@class == 'd-table-cell col-7 pr-3 v-align-top' }"
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
}
