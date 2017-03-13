package com.spider.util;

import java.net.URL;
import java.util.BitSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gargoylesoftware.htmlunit.util.UrlUtils;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlUtil {
    public static final String trimInner(String s, String tag) {
        Pattern parten_begin = Pattern.compile("<" + tag + "[^>]*>", Pattern.CASE_INSENSITIVE);
        Pattern parten_end = Pattern.compile("</" + tag + ">", Pattern.CASE_INSENSITIVE);
        return trimInner(s, parten_begin, parten_end);
    }

    public static final String trimComment(String s) {
        return trimInner(s, Regs.COMMENT_BEGIN, Regs.COMMENT_END);
    }

    public static final String trimWithoutTag(String s, String tag) {
        Matcher matcher = Regs.TAG.matcher(s);
        int offset = 0;
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(s.substring(offset, matcher.start()).trim());
            String g2 = matcher.group(2);
            if (tag != null && g2.matches(tag)) {
                String g1 = matcher.group(1);
                String g4 = matcher.group(4);
                if (g1.length() == 0) {
                    Matcher m = Regs.SRC.matcher(matcher.group(3));
                    if (m.find()) {
                        sb.append("<" + g2 + m.group() + g4 + ">");
                    } else {
                        sb.append("<" + g2 + g4 + ">");
                    }
                } else {
                    sb.append("<" + g1 + g2 + g4 + ">");
                }
            }
            offset = matcher.end();
        }
        if (offset < s.length()) {
            sb.append(s.substring(offset).trim());
        }
        //log.debug(sb.toString());
        return sb.toString();
    }

    private static final String trimInner(String s, Pattern parten_begin, Pattern parten_end) {
        Matcher matcher_begin = parten_begin.matcher(s);
        Matcher matcher_end = parten_end.matcher(s);
        int offset = 0;
        StringBuilder sb = new StringBuilder();
        boolean find;
        while (find = matcher_begin.find()) {
            int beginStart = matcher_begin.start();
            if (offset > beginStart) {
                continue;
            } else {
                if (offset < beginStart) {
                    sb.append(s.substring(offset, beginStart).trim());
                }
                if (matcher_end.find()) {
                    offset = matcher_end.end();
                } else {
                    offset = s.length();
                    break;
                }
            }
        }
        if (!find) {
            sb.append(s.substring(offset).trim());
        }
        //log.debug(sb.toString());
        return sb.toString();
    }

    public static final String filterContent(String content) {
        String rets = null;
        if (StringUtils.isNotEmpty(content)) {
            rets = content.replaceAll("<script(?:[^<]++|<(?!/script>))*+</script>", "")//去除script标签
                    .replaceAll("<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>", "")//去除style
                    .replaceAll("</?[^/?(br)|(p)|(img)][^><]*>", "")//去掉其他标签除（p、image、br）
                    .replaceAll("&nbsp;", "").replaceAll("&nbsp", "").replaceAll("\\{ProofReader\\}", "").replaceAll("(class|style|width|height|alt|title)\\s*=\\s*('[^']*'|\"[^\"]*\")", "");//去掉样式及其他
        }
        return rets;
    }

    public static final String trimEmptyTag(String s) {
        return s.replace("<p></p>", "").replace("<td></td>", "").replace("<tr></tr>", "").replace("<table></table>", "");
    }

    public static final String filter(String content) {
        content = content.replace("&nbsp;", "").trim();
        content = HtmlUtil.trimInner(content, "script");
        content = HtmlUtil.trimComment(content);
        content = HtmlUtil.trimWithoutTag(content, "p|img|table|tr|td|br");
        content = HtmlUtil.trimEmptyTag(content);
        return content;
    }

    public static final String filter(String content, String baseUri) {
        String text = filter(content);
        text = dealAbsImg(baseUri, text);
        return text;
    }

    /**
     * 把html文本中的图片，相对路径转绝对路径
     */
    public static String dealAbsImg(String baseUri, String html) {
        String content = html;
        try {
            Document doc = Jsoup.parse(html);
            doc.setBaseUri(baseUri);
            Elements imgEles = doc.getElementsByTag("img");
            for (Element imgEle : imgEles) {
                String absUrl = imgEle.attr("abs:src");
                imgEle.attr("src", absUrl);
            }
            content = doc.body().children().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public static void main(String[] args) throws Exception{
        String url =UrlUtils.encodeAnchor("http://www.qhsf.gov.cn/PictureNew/2017310144401.苏荣厅长出席会议并讲话.jpg");
//        String url =UrlUtils.encodeUrl(new URL("http://www.qhsf.gov.cn/PictureNew/2017310144401.苏荣厅长出席会议并讲话.jpg?a=周锴&b=123"),false,"utf-8").toString();
//        byte[] bs = URLCodec.encodeUrl(new BitSet(256),"http://www.qhsf.gov.cn/PictureNew/2017310144401.苏荣厅长出席会议并讲话.jpg?a=周锴&b=123".getBytes());
        System.out.println(url);

        String s = "<table cellspacing=\"0\" cellpadding=\"0\" width=\"908\" border=\"0\" style=\"border-collapse: collapse\" bordercolor=\"#39abeb\">\n" +
                "\t    <tbody><tr>\n" +
                "\t\t    <td align=\"center\">\n" +
                "\t\t        <table cellspacing=\"0\" cellpadding=\"0\" width=\"868\" border=\"0\" style=\"border-collapse: collapse\" bordercolor=\"#39abeb\">\n" +
                "\t                <tbody><tr>\n" +
                "\t\t                <td height=\"86\" align=\"center\" width=\"868\">\n" +
                "\t\t\t\t\t    <font face=\"黑体\" size=\"5\" style=\"line-height: 150%\" color=\"#000000\"><span id=\"ContentPlaceHolder1_docTitle\">省司法厅直属机关党建工作会议召开</span></font></td>\n" +
                "\t\t\t        </tr>\n" +
                "\t\t        </tbody></table>\n" +
                "\t\t    </td>\n" +
                "\t    </tr>\n" +
                "\t    <tr>\n" +
                "\t\t    <td align=\"center\" height=\"30\"><font size=\"2\" color=\"#666666\">青海司法行政网：http://www.qhsf.gov.cn    来源：<span id=\"ContentPlaceHolder1_docFromPartion\">厅直属机关党委</span>   供稿：<span id=\"ContentPlaceHolder1_docAuthor\">厅直属机关党委</span>  发布时间：<span id=\"ContentPlaceHolder1_docDate\">2017/3/10 14:41:46</span></font></td>\n" +
                "\t    </tr>\n" +
                "\t    <tr>\n" +
                "\t\t    <td height=\"12\">　</td>\n" +
                "\t    </tr>\n" +
                "\t    <tr>\n" +
                "\t\t    <td align=\"left\" height=\"1\" background=\"images/dot_table_ww.gif\"></td>\n" +
                "\t    </tr>\n" +
                "\t        <tr>\n" +
                "\t\t    <td height=\"12\" align=\"left\"></td>\n" +
                "\t    </tr>\n" +
                "        <tr>\n" +
                "\t\t    <td align=\"left\"><font face=\"楷体,楷体_gb2312\" style=\"line-height: 150%;FONT-SIZE: 14pt;\"><span id=\"ContentPlaceHolder1_docAuthorSay\">　　</span></font></td>\n" +
                "\t    </tr>\n" +
                "\t    <tr><td align=\"center\"><img src=\"PictureNew/2017310144401.苏荣厅长出席会议并讲话.jpg\" align=\"center\"></td></tr><tr><td height=\"40\" align=\"center\">苏荣厅长出席会议并讲话</td></tr><tr><td align=\"center\"><img src=\"PictureNew/2017310144403.省直机关工委副书记徐利军莅临会议并讲话.jpg\" align=\"center\"></td></tr><tr><td height=\"40\" align=\"center\">省直机关工委副书记徐利军莅临会议并讲话</td></tr><tr><td align=\"center\"><img src=\"PictureNew/2017310144402.jpg\" align=\"center\"></td></tr><tr><td height=\"40\" align=\"center\">会议主席台</td></tr><tr><td align=\"center\"><img src=\"PictureNew/2017310144405.厅政治部主任蔡长春作工作报告.jpg\" align=\"center\"></td></tr><tr><td height=\"40\" align=\"center\">厅政治部主任蔡长春作工作报告</td></tr><tr><td align=\"center\"><img src=\"PictureNew/2017310144404.签订党建目标责任书和精神文明建设责任书.jpg\" align=\"center\"></td></tr><tr><td height=\"40\" align=\"center\">签订党建目标责任书和精神文明建设责任书</td></tr><tr><td align=\"center\"><img src=\"PictureNew/2017310144406.会场.jpg\" align=\"center\"></td></tr><tr><td height=\"40\" align=\"center\">会场</td></tr>\n" +
                "\t        <tr>\n" +
                "\t\t    <td height=\"12\" align=\"left\"></td>\n" +
                "\t    </tr>\n" +
                "\t    <tr>\n" +
                "\t\t    <td style=\"line-height:200%;font-size:16px; margin :0px;text-align:justify;text-justify:inter-ideograph;\"><font class=\"nr14\" id=\"Zoom\"><span id=\"ContentPlaceHolder1_docContent\">　　<span>3月10日上午，省司法厅召开2017年厅直属机关党建工作会议，学习贯彻省直机关党的工作会议、全省司法行政工作会议精神，总结去年厅直机关党建工作,部署今年任务。省直机关工委副书记徐利军莅临会议指导。\n" +
                "<br>　　徐利军指出，一要强化思想教育，自觉加强马克思主义理论学习，增强“四个意识”，坚定“四个自信”；二要强化能力建设，不断提升机关党建科学化水平；三要强化基层基础，为持续推动机关党建提供保障；四要强化责任落实，认真贯彻全面从严治党的各项要求。\n" +
                "<br>　　省司法厅党组书记、厅长苏荣出席会议并讲话。他对2016年厅系统机关党建工作予以充分肯定，并结合个人学习思考，从五个方面对今后厅系统机关党建工作提出要求。一是突出全面从严治党的“根本保证”。各级党组织要把推动全面从严治党向基层延伸作为机关党建工作的中心任务，坚持问题导向，不断开拓创新，探索务实管用的方法措施，努力提升机关党建工作科学化水平。二是从严落实党风廉政建设“两个责任”。要抓龙头，压实主体责任，强化主责意识，坚决杜绝“责任田”撂荒现象；要抓常态，强化监督管理，以“零容忍”的态度严肃查处党员干部违纪违法问题，增强制度刚性约束；要抓问责，严格责任追究，促进两个责任落地。三是统筹抓好党内政治生活、政治生态和政治文化“三个建设”。注重从先进文化中汲取营养，注重严明政治纪律和政治规矩，注重思想建党和制度建党相统一，注重发挥“关键少数”的示范引领作用。四是充分运用党内监督执纪“四种形态”。准确把握内涵，牢牢把握纪律尺度，切实扛起监督执纪的政治责任。五是坚持实施管党治党“五轮驱动”。抓党内思想教育这个根本，抓党内选人用人这个导向，抓严明党的纪律这个关键，抓党的组织生活这个经常性手段，抓继承和创新这两个关键环节，努力把机关党建工作与中央、省委的要求有机结合起来，不断增强机关党建工作的时代感和生命力。\n" +
                "<br>　　省司法厅党组成员、政治部主任、直属机关党委书记蔡长春以《着力提升机关党建工作科学化水平 持续推动全面从严治党向基层延伸》为题作了工作报告，从深入推进“两学一做”学习教育、夯实基层基础、持之以恒正风肃纪、丰富精神文明创建载体、密切联系服务群众五个方面总结了201年厅系统党建工作；从突出思想建党、理顺管理体制、坚持作风养成、培育机关文化四个方面部署了2017年厅系统党建工作任务。\n" +
                "<br>　　会上苏荣、蔡长春同志分别与厅党组班子成员和省监狱管理局、戒毒管理局、律师协会党委主要负责人、厅机关各支部书记签订了党建目标责任书和精神文明建设责任书。厅党组成员、巡视员、副巡视员，监狱管理局、戒毒管理局局长、政委、政治部主任，驻厅纪检组和厅机关全体党员干部、老干部代表共72人参加会议。\n" +
                "<br>　　&nbsp;</span></span></font></td>\n" +
                "\t    </tr>\n" +
                "        <tr>\n" +
                "\t\t    <td align=\"left\" height=\"30\"></td>\n" +
                "\t    </tr>\n" +
                "\t    <tr>\n" +
                "\t\t    <td align=\"right\" height=\"30\"><span id=\"ContentPlaceHolder1_docIssu\"></span></td>\n" +
                "\t    </tr>\n" +
                "\t    <tr>\n" +
                "\t\t    <td align=\"left\" height=\"20\"></td>\n" +
                "\t    </tr>\n" +
                "\t    <tr>\n" +
                "\t\t    <td align=\"left\" height=\"30\">相关链接</td>\n" +
                "\t    </tr>\n" +
                "\t    <tr>\n" +
                "\t\t    <td align=\"left\" height=\"1\" background=\"images/dot_table_ww.gif\"></td>\n" +
                "\t    </tr>\n" +
                "\t    <tr><td height=\"25\" align=\"left\">　<img src=\"images/flag1.gif\"> <a href=\"NewsDetail.aspx?id=83C0F618A1DD9601\"><font color=\"#5b5b5b\">全省社区矫正安置帮教工作推进会召开</font></a></td></tr><tr><td height=\"25\" align=\"left\">　<img src=\"images/flag1.gif\"> <a href=\"NewsDetail.aspx?id=3610552193ED504C\"><font color=\"#5b5b5b\">省司法厅妇女代表队在省直机关庆“三·八”健身操大赛上获优秀奖</font></a></td></tr>\n" +
                "\t    <tr>\n" +
                "\t        <td align=\"left\" height=\"30\"></td>\n" +
                "\t    </tr>\n" +
                "    </tbody></table>";
        s = s.replace("&nbsp;", "").trim();
        s = HtmlUtil.trimInner(s, "script");
        s = HtmlUtil.trimComment(s);
        s = HtmlUtil.trimWithoutTag(s, "p|img|table|tr|td|br");
        s = HtmlUtil.trimEmptyTag(s);
        s = dealAbsImg("http://www.qhsf.gov.cn/NewsDetail.aspx?id=90910CD7747B0A20",s);
        //System.out.println(s);
    }

}
