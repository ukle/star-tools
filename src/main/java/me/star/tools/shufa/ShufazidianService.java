package me.star.tools.shufa;

import me.star.tools.utils.HttpClientUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Star Chou
 * @create 2021/12/26 13:26
 */
@Service
public class ShufazidianService {

    /**
     * 爬取书法字典返回的图片，加载到本地文件，生成完整的html，前端刷新页面
     *
     * @param requestDto
     */
    public String loadHtml(ShufazidianRequestDto requestDto) throws IOException {
        //请求书法字典网址，返回内容
        List<String> contents = requestToSFZD(requestDto);
        if (contents.isEmpty()) {
            return "";
        }
        //做内容清洗
        contents = filterExpect(contents, requestDto.getSort());

        return String.join("", contents);
        //将内容添加到目标文件
//        appendToFile(contents);
    }

    /**
     * 单个content是单独的html页面
     * 要根据页面做解析，过滤掉自己不要的内容
     *
     * @param contents
     * @return 清洗之后的网页内容
     */
    private List<String> filterExpect(List<String> contents, String sort) {
        List<String> targetList = new ArrayList<>();
        for (String content : contents) {
            Document document = Jsoup.parse(content);
            Elements woo = document.getElementsByClass("woo");
            if (woo.size() > 0) {
                Element topOne = woo.get(0);
                if (topOne.toString().contains("扫一扫下载本站APP")) {
                    woo.remove(0);
                }
            }

            //如果条数大于6条，进行过滤，只针对楷书和行书
            if (sort.equals("9") || sort.equals("8")) {
                if (woo.size() > 6) {
                    woo = filterByRules(woo);
                }
            }
            targetList.add(woo.toString());
        }
        return targetList;
    }

    /**
     * 根据自己定义的规则进行数据过滤
     *
     * @param woo
     * @return
     */
    private Elements filterByRules(Elements woo) {
        Elements targetElements = new Elements();
        //根据作者过滤一遍
        for (String artist : topArtist()) {
            for (Element element : woo) {
                String ele = element.toString();
                if (ele.contains(artist)
                        && !ele.contains("title=\"" + artist + "\"")
                        && !ele.contains("碑")
                        && !ele.contains("墓")
                        && !ele.contains("请点击放大看")) {
                    targetElements.add(element);
                }
            }
        }

        if (targetElements.size() < 3) {
            for (String artist : secondArtist()) {
                int temp = 0;
                for (Element element : woo) {
                    String ele = element.toString();
                    if (ele.contains(artist)
                            && !ele.contains("title=\"" + artist + "\"")
                            && !ele.contains("碑")
                            && !ele.contains("请点击放大看")) {
                        temp++;
                        targetElements.add(element);
                        if (temp >= 3 || targetElements.size() > 5) {
                            break;
                        }
                    }
                }
            }
        }

        //todo 使用之后再优化
        return targetElements;
    }

    /**
     * 将句子分割为单个字，多次请求书法字典网址，返回字体内容
     *
     * @param requestDto
     * @return
     */
    private List<String> requestToSFZD(ShufazidianRequestDto requestDto) {
        String url = "http://www.shufazidian.com";
        String words = requestDto.getWords();
        if (words.isBlank()) {
            return new ArrayList<>();
        }
        String sort = requestDto.getSort();
        String[] split = words.split("");
        List<String> list = new ArrayList<>(split.length);
        for (String word : split) {
            String html = HttpClientUtil.postParams(url, Map.of("wd", word, "sort", sort));
            list.add(html);
        }
        return list;
    }

    public static List<String> topArtist() {
        return List.of("钟绍京", "国诠", "智永");
    }

    public static List<String> secondArtist() {
        return List.of("赵孟頫", "陆柬之", "褚遂良", "赵构", "蔡襄", "王羲之", "王献之");
    }
}
