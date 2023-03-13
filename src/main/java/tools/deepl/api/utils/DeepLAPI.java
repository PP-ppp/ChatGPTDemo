package tools.deepl.api.utils;
import java.net.*;
import java.io.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class DeepLAPI {
	private static final String api_key = "05f9827d-de6c-690b-1505-6cc29c3b25e9:fx";
	private static final String api_url = "https://api-free.deepl.com/v2/translate";

    public static void main(String[] args) throws Exception {
        String textToTranslate = "Hello, world!";
        String response = translate(textToTranslate,"ZH");
        System.out.println(response);
    }
    public static String translate(String textToTranslate) {
    	return translate(textToTranslate,"EN");
    }
    public static String translate(String textToTranslate, String targetLang) {
    	String text = null; 
    	try {
            String requestBody = buildRequestBody(api_key, textToTranslate, targetLang);
            text = post(api_url, requestBody);
        } catch (Exception e) {
        	e.printStackTrace();
        	text = e.getCause() + ":" + e.getMessage();
		}
    	return text;
    }
    

    private static String buildRequestBody(String apiKey, String textToTranslate, String targetLang) throws Exception {
        String salt = Long.toString(System.currentTimeMillis());
        String signature = calculateSignature(apiKey, textToTranslate, salt);
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("auth_key", apiKey);
        parameters.put("text", textToTranslate);
        parameters.put("target_lang", targetLang);
        parameters.put("salt", salt);
        parameters.put("signature", signature);
        return buildQueryString(parameters);
    }

    private static String calculateSignature(String apiKey, String textToTranslate, String salt) throws Exception {
        String data = apiKey + textToTranslate + salt + "your_secret_key_here";
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKeySpec secretKeySpec = new SecretKeySpec("your_secret_key_here".getBytes(), "HmacSHA1");
        mac.init(secretKeySpec);
        byte[] signatureBytes = mac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    private static String buildQueryString(Map<String, String> parameters) throws Exception {
        StringBuilder queryStringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String key = URLEncoder.encode(entry.getKey(), "UTF-8");
            String value = URLEncoder.encode(entry.getValue(), "UTF-8");
            queryStringBuilder.append(key);
            queryStringBuilder.append("=");
            queryStringBuilder.append(value);
            queryStringBuilder.append("&");
        }
        String queryString = queryStringBuilder.toString();
        return queryString.substring(0, queryString.length() - 1);
    }

    private static String post(String apiUrl, String requestBody) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(requestBody);
        outputStream.flush();
        outputStream.close();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder responseBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            responseBuilder.append(line);
        }
        bufferedReader.close();
        JSONObject json = new JSONObject(responseBuilder.toString());
        JSONArray translationsArray = json.getJSONArray("translations");
        JSONObject translationObject = translationsArray.getJSONObject(0);
        return translationObject.getString("text");
    }
}
