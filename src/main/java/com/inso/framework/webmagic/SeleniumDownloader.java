//package com.inso.framework.webmagic;
//
//
//import com.inso.framework.log.Log;
//import com.inso.framework.log.LogFactory;
//import org.openqa.selenium.By;
//import org.openqa.selenium.Cookie;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import us.codecraft.webmagic.Page;
//import us.codecraft.webmagic.Request;
//import us.codecraft.webmagic.Site;
//import us.codecraft.webmagic.Task;
//import us.codecraft.webmagic.downloader.Downloader;
//import us.codecraft.webmagic.selector.PlainText;
//
//import java.io.Closeable;
//import java.io.IOException;
//import java.util.Map;
//
//public class SeleniumDownloader implements Downloader, Closeable {
//
//    private volatile WebDriverPool webDriverPool;
//
//    private Log logger=  LogFactory.getLog(this.getClass());
//
//    private int sleepTime = 0;
//
//    private int poolSize = 1;
//
//    private static final String DRIVER_PHANTOMJS = "phantomjs";
//
//    /**
//     * 新建
//     *
//     * @param chromeDriverPath chromeDriverPath
//     */
//    public SeleniumDownloader(String chromeDriverPath) {
//        System.getProperties().setProperty("webdriver.chrome.driver",
//                chromeDriverPath);
//    }
//
//    /**
//     * Constructor without any filed. Construct PhantomJS browser
//     */
//    public SeleniumDownloader() {
//        // System.setProperty("phantomjs.binary.path",
//        // "/Users/Bingo/Downloads/phantomjs-1.9.7-macosx/bin/phantomjs");
//    }
//
//    /**
//     * set sleep time to wait until load success
//     *
//     * @param sleepTime sleepTime
//     * @return this
//     */
//    public SeleniumDownloader setSleepTime(int sleepTime) {
//        this.sleepTime = sleepTime;
//        return this;
//    }
//
//    @Override
//    public Page download(Request request, Task task) {
//        checkInit();
//        WebDriver webDriver;
//        try {
//            webDriver = webDriverPool.get();
//        } catch (InterruptedException e) {
//            logger.warn("interrupted", e);
//            return null;
//        }
//        logger.info("downloading page " + request.getUrl());
//        webDriver.get(request.getUrl());
//        try {
//            Thread.sleep(sleepTime);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        WebDriver.Options manage = webDriver.manage();
//        Site site = task.getSite();
//        if (site.getCookies() != null) {
//            for (Map.Entry<String, String> cookieEntry : site.getCookies()
//                    .entrySet()) {
//                Cookie cookie = new Cookie(cookieEntry.getKey(),
//                        cookieEntry.getValue());
//                manage.addCookie(cookie);
//            }
//        }
//
//        /*
//         * TODO You can add mouse event or other processes
//         *
//         */
//
//        WebElement webElement = webDriver.findElement(By.xpath("/html"));
//        String content = webElement.getAttribute("outerHTML");
//        Page page = new Page();
//        page.setRawText(content);
////        page.setHtml(new Html(content, request.getUrl()));
//        page.setUrl(new PlainText(request.getUrl()));
//        page.setRequest(request);
//        webDriverPool.returnToPool(webDriver);
//        return page;
//    }
//
//    @Override
//    public void setThread(int threadNum) {
//
//    }
//
//    private void checkInit() {
//        if (webDriverPool == null) {
//            synchronized (this) {
//                webDriverPool = new WebDriverPool(poolSize);
//            }
//        }
//    }
//
//    @Override
//    public void close() throws IOException {
//
//    }
//}
