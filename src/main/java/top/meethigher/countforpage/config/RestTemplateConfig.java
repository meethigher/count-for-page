package top.meethigher.countforpage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
//        return new RestTemplate();
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SSL factory = new SSL();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(15000);//单位为ms
        return factory;
    }


    /**
     * SSL相关
     */
    private static class SSL extends SimpleClientHttpRequestFactory {

        @Override
        protected void prepareConnection(HttpURLConnection connection, String httpMethod)
                throws IOException {
            if (connection instanceof HttpsURLConnection) {
                prepareHttpsConnection((HttpsURLConnection) connection);
            }
            super.prepareConnection(connection, httpMethod);
        }

        private void prepareHttpsConnection(HttpsURLConnection connection) {
            connection.setHostnameVerifier(new SkipHostnameVerifier());
            try {
                connection.setSSLSocketFactory(createSslSocketFactory());
            } catch (Exception ex) {
                // Ignore
            }
        }

        private SSLSocketFactory createSslSocketFactory() throws Exception {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{new SkipX509TrustManager()},
                    new SecureRandom());
            return context.getSocketFactory();
        }

        private class SkipHostnameVerifier implements HostnameVerifier {

            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }

        }

        private static class SkipX509TrustManager implements X509TrustManager {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

        }
    }


}
