package com.yj.crawler.utils;

import com.yj.crawler.page.Page;

import java.io.*;

/**
 * @date: 2018/5/30
 * @author: create by Right_ydd
 * @description: 本类主要是 下载那些已经访问过的文件
 */
public class FileTool {

    private static String dirPath = "d:/";

    /**
     * getMethod.getResponseHeader("Content-Type").getValue()
     * 根据 URL 和网页类型生成需要保存的网页的文件名，去除 URL 中的非文件名字符
     * @param url
     * @param contentType
     * @return
     */
    private static String getFileNameByUrl(String url, String contentType) {
        //去除 http://
        url = url.substring(7);
        //text/html 类型
        if (contentType.indexOf("html") != -1) {
            url = url.replaceAll("[\\?/:*|<>\"]", "_") + ".html";
            return url;
        }
        //如 application/pdf 类型
        else {
            return url.replaceAll("[\\?/:*|<>\"]", "_") + "." +
                    contentType.substring(contentType.lastIndexOf("/") + 1);
        }
    }

    /**
     * 生成目录
     */
    private static void mkdir() {
        if (dirPath == null) {
            dirPath = Class.class.getClass().getResource("/").getPath() + "temp\\";
        }
        File fileDir = new File(dirPath);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
    }

    /**
     * 保存网页字节数组到本地文件，filePath 为要保存的文件的相对地址
     * @param page
     * @return  返回文件保存路径
     * @throws UnsupportedEncodingException
     */
    public static String saveToLocal(Page page) throws IOException {
        mkdir();
        String fileName =  getFileNameByUrl(page.getUrl(), page.getContentType());
        String filePath = dirPath + fileName;
        byte[] data = page.getContent();
        DataOutputStream out = null;
        try {
            out = new DataOutputStream(new FileOutputStream(new File(filePath)));
            for (int i = 0; i < data.length; i++) {
                out.write(data[i]);
            }
            System.out.println(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            out.flush();
            out.close();
        }
        return filePath;
    }

    /**
     * 根据文件的路径读取文件内容
     * @param filePath 绝对路径
     * @return 返回该文件的内容
     * @throws IOException
     */
    public static String readFile(String filePath) throws IOException {
        File file = new File(filePath);
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            stringBuilder = new StringBuilder();
            String s = "";
            while ((s = bufferedReader.readLine()) != null){
                stringBuilder.append(s);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            fileReader.close();
            bufferedReader.close();
        }
        return String.valueOf(stringBuilder);
    }
}
