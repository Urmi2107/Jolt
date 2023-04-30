package example.util;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.List;
import java.util.Map;

public class JoltUtil {
    public <T> T getEntity(String spec, Class<T> clazz, Object inputJson) {
        
        List chainrSpecJSON = JsonUtils.classpathToList(spec);
        Chainr chainr = Chainr.fromSpec(chainrSpecJSON);
        Object transformedOutput = chainr.transform(inputJson);
        System.out.println("transformedOutput" + transformedOutput);
        T entity = null;
        try {
            Gson gson = new Gson();
            String jsonString = gson.toJson(transformedOutput);
            entity = gson.fromJson(jsonString, clazz);
        }
        catch(Exception e) {
            System.out.println("Error while transforming :" + e.getMessage());
        }
        return entity;
    }

    public Map<String, Object> invokeRest(String url) {
        Map<String, Object> responseBody = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            System.out.println("response" + response);

            String response1 = response.body().string();
            System.out.println("response1111111 :  " + response1);
            Gson gson = new Gson();
            
            responseBody = gson.fromJson(response1, Map.class);
            System.out.println("responseBody" + responseBody);
        }
        catch(Exception e) {
            System.out.println("error is :"+e.getMessage());
        }
        return responseBody;
    }
}
