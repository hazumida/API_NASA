import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        CloseableHttpResponse response = null;
        HttpGet request;
        Post post = null;
        String url = null;
        byte[] bytes = null;

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        try {
            request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=xemTpcbu7ekBFMJYvYmK74w02Yf0GAwHk4XnwgkQ");
            response = httpClient.execute(request);

            ObjectMapper mapper = new ObjectMapper();
            post = mapper.readValue(response.getEntity().getContent(), Post.class);
        } catch (IOException err) {
            System.out.println(err.getMessage());
        } finally {
            try {
                response.close();
            } catch (IOException err) {
                System.out.println(err.getMessage());
            }
        }

        try {
            url = post.getUrl();
            request = new HttpGet(url);
            response = httpClient.execute(request);
            bytes = response.getEntity().getContent().readAllBytes();
        } catch (IOException err) {
            System.out.println(err.getMessage());
        } finally {
            try {
                httpClient.close();
                response.close();
            } catch (IOException err) {
                System.out.println(err.getMessage());
            }
        }

        try (FileOutputStream fos = new FileOutputStream(url.split("/")[url.split("/").length - 1])){
            fos.write(bytes, 0, bytes.length );
        } catch (IOException err) {
            System.out.println(err.getMessage());
        }

    }
}
