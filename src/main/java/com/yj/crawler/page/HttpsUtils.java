package com.yj.crawler.page;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.util.*;

/**
 * @author wuyang
 * @date 2018/9/6 17:31
 */
public class HttpsUtils {


    public static String doPost(String url,Map<String,Object> params,String charset){
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
            httpClient = new SSLClient();
            httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.addHeader("Request-Type", "Ajax");
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = params.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String,String> elem = (Map.Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(),elem.getValue()));
            }
            if(list.size() > 0){
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,charset);
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result = EntityUtils.toString(resEntity,charset);
                }
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("cueGridId","grid");
        data.put("pageModel","pages.onlineservice.socialinsurance.onlinepublicity.MedicareDrugCatalogQuery");
        data.put("eventNames","grid.dogridquery");
        data.put("radow_parent_data","");
        data.put("rc","{\"ypspm\":{\"type\":\"textfield\",\"label\":\"输入药品名称\",\"REDH\":\"R\",\"data\":\"\"},\"query\":{\"type\":\"button\",\"REDH\":\"E\",\"data\":\"\"},\"clean\":{\"type\":\"button\",\"REDH\":\"E\",\"data\":\"\"},\"grid\":{\"type\":\"grid\",\"data\":\"\",\"label\":{\"id\":\"序号\",\"tym\":\"药品通用名\",\"spm\":\"药品商品名\",\"bzjx\":\"标注剂型\",\"zflb\":\"支付类别\",\"zfbl\":\"医保自付比例 \",\"sfyd\":\"药店可否出售(可/否) \",\"bz\":\"备注\"},\"REDH\":{\"id\":\"D\",\"tym\":\"D\",\"spm\":\"D\",\"bzjx\":\"D\",\"zflb\":\"D\",\"zfbl\":\"D\",\"sfyd\":\"D\",\"bz\":\"D\"},\"selectall\":{},\"colsType\":{\"tym\":\"string\",\"spm\":\"string\",\"bzjx\":\"string\",\"zflb\":\"string\",\"zfbl\":\"string\",\"sfyd\":\"string\",\"bz\":\"string\"},\"isPageGrid\":true,\"limit\":10},\"radow_parent_data\":{\"type\":\"textfield\",\"data\":\"\"}}");
        data.put("limit","1");
        data.put("start","0");

        String result = doPost("https://m.mynj.cn:11096/njwsbs/radowAction.do?method=doEvent&pageModel=pages.onlineservice.socialinsurance.onlinepublicity.MedicareDrugCatalogQuery&eventNames=grid.dogridquery", data,"UTF-8");
        System.out.println(result);
    }
}
