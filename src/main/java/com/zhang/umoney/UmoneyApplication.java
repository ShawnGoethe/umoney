package com.zhang.umoney;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import jd.union.open.category.goods.get.request.CategoryReq;
import jd.union.open.category.goods.get.request.UnionOpenCategoryGoodsGetRequest;
import jd.union.open.category.goods.get.response.UnionOpenCategoryGoodsGetResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.MessageDigest;
import java.time.Duration;
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
    // Time out value of getting promotion goods
//    private final static int PROMOTION_GOODS_TIMEOUT = 5000;
//    // http client
//    private HttpClient client = HttpClient.newBuilder()
//            .connectTimeout(Duration.ofMillis(PROMOTION_GOODS_TIMEOUT))
//            .followRedirects(HttpClient.Redirect.NORMAL)
//            .build();


    public static void main(String[] args) throws JdException {
//    	SpringApplication.run(UmoneyApplication.class, args);
        JdClient client=new DefaultJdClient(SERVER_URL,APP_TOKEN,APP_KEY,APP_SECRET);
        UnionOpenCategoryGoodsGetRequest request=new UnionOpenCategoryGoodsGetRequest();
        CategoryReq req=new CategoryReq();
        request.setReq(req);
        UnionOpenCategoryGoodsGetResponse response=client.execute(request);

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
        map.put("param_json", paramJson);
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

