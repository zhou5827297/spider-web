package com.spider.servlet;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptResult;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.UrlUtils;
import com.spider.proxy.ProxyBean;
import com.spider.proxy.ProxyManage;
import com.spider.proxy.impl.ProxyRemoteManage;
import com.spider.util.HtmlUtil;
import com.spider.util.WebClientPool;
import common.base.util.DateUtils;
import common.base.util.FileUtil;
import common.base.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

/**
 * 执行爬虫浏览器的请求
 */
public class SpiderBrowerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static Logger LOG = LogManager.getLogger(SpiderBrowerServlet.class);

    private static final ProxyManage PROXYMANAGE = new ProxyRemoteManage();

    private static final WebClientPool WEBCLIENTPOOL = WebClientPool.getInstance();


    public SpiderBrowerServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebClient webClient = null;

        String url = request.getParameter("url");
        if (StringUtil.isEmpty(url)) {
            return;
        }
        boolean proxySwitch = "true".equals(request.getParameter("proxySwitch")) ? true : false;
        boolean javaEnable = "true".equals(request.getParameter("javaEnable")) ? true : false;
        boolean cookieEnable = "true".equals(request.getParameter("cookieEnable")) ? true : false;
        boolean filterEnable = "true".equals(request.getParameter("filterEnable")) ? true : false;
        String js = request.getParameter("js");
        String content = "";
        String encoding = "UTF-8";
        for (int i = 0; i < 10; i++) {
            try {
                if (webClient == null) {
                    webClient = WEBCLIENTPOOL.getClient();
                    webClient.getCookieManager().setCookiesEnabled(cookieEnable);
                }
                WebRequest webRequest = new WebRequest(UrlUtils.toUrlUnsafe(url));
                webRequest.setCharset("UTF-8");
                if (proxySwitch) {
                    ProxyBean proxyBean = PROXYMANAGE.getProxyBean();
                    webRequest.setProxyHost(proxyBean.getIp());
                    webRequest.setProxyPort(proxyBean.getPort());
                }
                webClient.getOptions().setJavaScriptEnabled(javaEnable);
                Page rootPage = webClient.getPage(webRequest);

                if (rootPage == null) {

                } else {
                    encoding = rootPage.getWebResponse().getContentCharset();
                    if (StringUtil.notEmpty(js)) {
                        if (rootPage.isHtmlPage() && StringUtil.notEmpty(js)) {
                            HtmlPage page = (HtmlPage) rootPage;
                            webClient.getOptions().setJavaScriptEnabled(true);
                            ScriptResult scriptResult = page.executeJavaScript(js);
                            if (scriptResult != null && scriptResult.getJavaScriptResult() != null) {
                                content = scriptResult.getJavaScriptResult().toString();
                                if (filterEnable) {
                                    content = HtmlUtil.filter(content, url);
                                }
                            }
                        }
                    } else {
                        content = rootPage.getWebResponse().getContentAsString();
                    }
                    break;
                }
            } catch (Exception ex) {
                content = ex.getMessage();
            }
        }
        if (webClient != null) {
            try {
                webClient.closeAllWindows();
                WEBCLIENTPOOL.returnClient(webClient);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (StringUtil.isEmpty(js)) {
            String path = "/data/subscribe/html/";
            if (new File(path).exists() == false) {
                new File(path).mkdir();
            }
            int random = new Random().nextInt(100000);
            String fileName = DateUtils.getDateStr("yyyyMMddHHmmss") + "-" + random + ".html";
            FileUtil.write(new File(path + fileName), content, Charset.forName("UTF-8"), false);
            content = fileName;
        }

        response.setContentType("text/html;");
        response.setCharacterEncoding(encoding);

        response.getWriter().write(content);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
