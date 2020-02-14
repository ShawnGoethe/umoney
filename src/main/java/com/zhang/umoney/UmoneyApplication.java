package com.zhang.umoney;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@SpringBootApplication
public class UmoneyApplication {
    private final static String SERVER_URL = "https://router.jd.com/api";
    private final static String APP_KEY = "ccf60957523bf9b8b63a4e8dd827a278";
    private final static String APP_SECRET = "0dbeb7c6b37448f6b188e493221e07b7";
    private final static String APP_TOKEN = "";

    public static String md5(String source)
            throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = md.digest(source.getBytes("utf-8"));
        return byte2hex(bytes);
    }

    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    public static boolean areNotEmpty(String[] values) {
        boolean result = true;
        if ((values == null) || (values.length == 0))
            result = false;
        else {
            for (String value : values) {
                result &= !isEmpty(value);
            }
        }
        return result;
    }

    public static boolean isEmpty(String value) {
        int strLen;
        if ((value == null) || ((strLen = value.length()) == 0))
            return true;
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
//    	SpringApplication.run(UmoneyApplication.class, args);
        UmoneyApplication um = new UmoneyApplication();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String timestamp = df.format(new Date());// new Date()为获取当前系统时间
        String sign = um.buildSign(timestamp, "1.0", "md5", "json", "jd.union.open.order.query", "", "", APP_KEY, APP_SECRET);
    }
    //处理http请求  requestUrl为请求地址  requestMethod请求方式，值为"GET"或"POST"
    public static String httpRequest(String requestUrl,String requestMethod,String outputStr){
        StringBuffer buffer=null;
        try{
            URL url=new URL(requestUrl);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod(requestMethod);
            conn.connect();
            //往服务器端写内容 也就是发起http请求需要带的参数
            if(null!=outputStr){
                OutputStream os=conn.getOutputStream();
                os.write(outputStr.getBytes("utf-8"));
                os.close();
            }

            //读取服务器端返回的内容
            InputStream is=conn.getInputStream();
            InputStreamReader isr=new InputStreamReader(is,"utf-8");
            BufferedReader br=new BufferedReader(isr);
            buffer=new StringBuffer();
            String line=null;
            while((line=br.readLine())!=null){
                buffer.append(line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
    }

    @RequestMapping("/index")
    public String getUser() {
        return "hello";
    }

    private String buildSign(String timestamp,
                             String version,
                             String signMethod,
                             String format,
                             String method,
                             String paramJson,
                             String accessToken,
                             String appKey,
                             String appSecret) throws Exception {

        //第一步，按照顺序填充参数
        Map<String, String> map = new TreeMap<>();
        map.put("timestamp", timestamp);
        map.put("v", version);
        map.put("sign_method", signMethod);
        map.put("format", format);
        map.put("method", method);

        //param_json为空的时候需要写成 "{}"
//        map.put("param_json", paramJson);
        map.put("param_json", {});
        map.put("access_token", accessToken);
        map.put("app_key", appKey);
        StringBuilder sb = new StringBuilder(appSecret);

        //按照规则拼成字符串
        for (Map.Entry entry : map.entrySet()) {
            String name = (String) entry.getKey();
            String value = (String) entry.getValue();
            //检测参数是否为空
            if (areNotEmpty(new String[]{name, value})) {
                sb.append(name).append(value);
            }

        }
        sb.append(appSecret);
        return md5(sb.toString());
    }

}

