package com.xingkaichun.helloworldblockchain.netcore.util;

import com.google.gson.Gson;
import com.xingkaichun.helloworldblockchain.setting.GlobalSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络工具类
 *
 * @author 邢开春 xingkaichun@qq.com
 */
public class NetUtil {

    private final static Logger logger = LoggerFactory.getLogger(NetUtil.class);

    private static Gson gson = new Gson();

    public static String getHtml(String stringUrl,Object requestBody) throws IOException {
        OutputStreamWriter out = null;
        BufferedReader br = null;
        try {
            URL url = new URL(stringUrl);// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("GET"); // 设置请求方式
            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.connect();
            out = new OutputStreamWriter(connection.getOutputStream(), GlobalSetting.GLOBAL_CHARSET);
            out.append(gson.toJson(requestBody));
            out.flush();
            out.close();

            // 读取响应
            InputStream is;
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }
            StringBuilder data = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(is, GlobalSetting.GLOBAL_CHARSET));
            String line;
            while ( (line = br.readLine()) != null) {
                data.append(line);
            }
            return data.toString();
        } finally {
            try {
                if(out != null){
                    out.close();
                }
            } catch (IOException e) {
                logger.error("IO关闭异常",e);
            }
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                logger.error("IO关闭异常",e);
            }
        }
    }

}
