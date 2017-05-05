package com.yunfei.whsc.tools;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kborid on 2016/6/13.
 */
public class HtmlParse {

    /*
    * 提取HTML文本中的img
    * 目前img标签标示有3种表达式
    * <img alt="" src="1.jpg"/>   <img alt="" src="1.jpg"></img>     <img alt="" src="1.jpg">
    */
    public static ArrayList<String> getImgList(String content) {
        ArrayList<String> list = new ArrayList();
        //开始匹配content中的<img />标签
        Pattern p_img = Pattern.compile("<(img|IMG)(.*?)(/>|></img>|>)");
        Matcher m_img = p_img.matcher(content);
        boolean result_img = m_img.find();
        if (result_img) {
            while (result_img) {
                //获取到匹配的<img />标签中的内容
                String str_img = m_img.group(2);

                //开始匹配<img />标签中的src
                Pattern p_src = Pattern.compile("(src|SRC)=(\"|\')(.*?)(\"|\')");
                Matcher m_src = p_src.matcher(str_img);
                if (m_src.find()) {
                    String str_src = m_src.group(3);
                    if (!str_src.endsWith(".gif")) {
                        list.add(str_src);
                    }
                }
                //结束匹配<img />标签中的src
                result_img = m_img.find();
            }
        }
        return list;
    }

    public static String replaceUrlImage(String contentStr) {
        Pattern p_img = Pattern.compile("<(img|IMG)(.*?)(/>|></img>|>)");
        Matcher m_img = p_img.matcher(contentStr);
        StringBuffer sb = new StringBuffer();

        boolean hasImage = false;
        boolean ret = m_img.find();
        if (ret) {
            while (ret) {
                //获取到匹配的<img />标签中的内容
                String imgStr = m_img.group(2);

                //开始匹配<img />标签中的src
                Pattern p_src = Pattern.compile("(src|SRC)=(\"|\')(.*?)(\"|\')");
                Matcher m_src = p_src.matcher(imgStr);
                if (m_src.find()) {
                    String srcStr = m_src.group(3);
                    if (!srcStr.endsWith(".gif")) {
                        hasImage = true;
                        m_img.appendReplacement(sb, "");
                    }
                }
                ret = m_img.find();
            }
        } else {
            sb.append(contentStr);
        }

        return hasImage ? sb.toString() : contentStr;
    }

    /**
     * 把html内容转为文本
     *
     * @param html 需要处理的html文本
     * @return
     */
    public static String trimHtml2Txt(String html) {
        html = html.replaceAll("\\<head>[\\s\\S]*?</head>(?i)", "");//去掉head
        html = html.replaceAll("\\<!--[\\s\\S]*?-->", "");//去掉注释
        html = html.replaceAll("\\<![\\s\\S]*?>", "");
        html = html.replaceAll("\\<style[^>]*>[\\s\\S]*?</style>(?i)", "");//去掉style
        html = html.replaceAll("\\<script[^>]*>[\\s\\S]*?</script>(?i)", "");//去掉js
        html = html.replaceAll("\\<w:[^>]+>[\\s\\S]*?</w:[^>]+>(?i)", "");//去掉word标签
        html = html.replaceAll("\\\r\n|\n|\r", " ");//去掉换行
        html = html.replaceAll("\\<br[^>]*>(?i)", "");
        html = html.replaceAll("\\</p>(?i)", "");
        html = html.replaceAll("\\<[^>]+>", "");
        html = html.replaceAll("&nbsp;", "");// 去掉&nbsp;符号
        return html.trim();
    }
}
