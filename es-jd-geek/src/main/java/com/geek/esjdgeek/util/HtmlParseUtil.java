package com.geek.esjdgeek.util;

import com.geek.esjdgeek.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author geek
 */
@Component
public class HtmlParseUtil {

    public static void main(String[] args) throws IOException {
        new HtmlParseUtil().parseJD("python").forEach(System.out::println);
    }

//    public static void main(String[] args) throws IOException {
//        // 获取请求。
////        String url  = "https://search.jd.com/Search?keyword=Java&enc=utf-8&wq=Java&pvid=c871636462c74583b7272bd6073a0a20";
//        String url = "https://search.jd.com/Search?keyword=Java";
//
//        // 解析网页。就是 Document 对象。
//        Document document = Jsoup.parse(new URL(url), 30000);
//        // js。
//        Element element = document.getElementById("J_goodsList");
////        System.out.println(element);
//        // 获取所有的 li 元素。
//        Elements elements = element.getElementsByTag("li");
//        // 获取元素中的内容。
//        for (Element el : elements) {
//            // <img width="220" height="220" data-img="1" src="//img10.360buyimg.com/n7/jfs/t1/26339/8/10661/124305/5c8af829E4470835f/99742c91174d3d7a.jpg" data-lazy-img="done" source-data-lazy-img="">
//            // 懒加载。
//            String img = el.getElementsByTag("img").eq(0).attr("src");
//            String price = el.getElementsByClass("p-price").eq(0).text();
//            String title = el.getElementsByClass("p-name").eq(0).text();
//
//            System.out.println("～　～　～　～　～　～　～");
//
//            System.out.println(img);
//            System.out.println(price);
//            System.out.println(title);
//        }
//    }

    public List<Content> parseJD(String keywords) throws IOException {
        // 获取请求。
        String url = "https://search.jd.com/Search?keyword=" + keywords;

        // 解析网页。就是 Document 对象。
        Document document = Jsoup.parse(new URL(url), 30000);
        // js。
        Element element = document.getElementById("J_goodsList");
        // 获取所有的 li 元素。
        Elements elements = element.getElementsByTag("li");

        List<Content> goodsList = new ArrayList<>();

        // 获取元素中的内容。
        for (Element el : elements) {
            String img = el.getElementsByTag("img").eq(0).attr("src");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();

            Content content = new Content();
            content.setImg(img);
            content.setPrice(price);
            content.setTitle(title);
            goodsList.add(content);
        }
        return goodsList;
    }

}
