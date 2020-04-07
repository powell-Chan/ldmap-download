package com.comleader.ldmapdownload.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * @ClassName HttpClientConfig
 * @Description: HttpClient配置类
 * @Author zhanghang
 * @Date 2020/4/2
 * @Version V1.0
 **/
@Configuration
@PropertySource(value = {"classpath:config/httpclient.properties"}, encoding = "UTF-8")
public class HttpClientConfig {

    @Value("${http.maxTotal}")
    private Integer maxTotal;//最大连接数

    @Value("${http.defaultMaxPerRoute}")
    private Integer defaultMaxPerRoute;//支持并发数


    @Value("${http.connectTimeout}")
    private Integer connectTimeout;//连接的超时时间


    @Value("${http.connectionRequestTimeout}")
    private Integer connectionRequestTimeout;//从连接池获取连接的超时时间

    @Value("${http.socketTimeout}")
    private Integer socketTimeout;//数据传输超时时间

    @Value("${http.staleConnectionCheckEnabled}")
    private boolean staleConnectionCheckEnabled;//提交请求前测试连接是否可用

    private static SSLContext sslContext = null;


    static {

        try {

            sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

//信任所有

                @Override

                public boolean isTrusted(X509Certificate[] xcs, String string) {

                    return true;

                }

            }).build();

        } catch (KeyStoreException ex) {

            //logger.error(ex.getMessage(), ex);

            ex.printStackTrace();

        } catch (NoSuchAlgorithmException ex) {

            //logger.error(ex.getMessage(), ex);

            ex.printStackTrace();

        } catch (KeyManagementException ex) {

            //logger.error(ex.getMessage(), ex);

            ex.printStackTrace();

        }

    }


    /**
     * 实例化一个连接池管理器,并且设置最大连接数，支持并发数
     *
     * @return
     */
    @Bean(name = "httpClientConnectionManager")
    public PoolingHttpClientConnectionManager getHttpClientConnectionManager() {

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()

                .register("http", new PlainConnectionSocketFactory())

                .register("https", new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER))

                .build();

        PoolingHttpClientConnectionManager httpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        httpClientConnectionManager.setMaxTotal(maxTotal);

        httpClientConnectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);

        return httpClientConnectionManager;

    }


    /**
     * 实例化连接池，设置连接池管理器。
     * <p>
     * 这里需要以参数形式注入上面实例化的连接池管理器
     *
     * @param httpClientConnectionManager
     * @return
     */
    @Bean(name = "httpClientBuilder")
    public HttpClientBuilder getHttpClientBuilder(@Qualifier("httpClientConnectionManager") PoolingHttpClientConnectionManager httpClientConnectionManager) {

        HttpClientBuilder builder = HttpClientBuilder.create();

        builder.setConnectionManager(httpClientConnectionManager);

        return builder;

    }


    /**
     * 注入连接池，用于获取httpClient
     *
     * @param httpClientBuilder
     * @return
     */
    @Bean
    public CloseableHttpClient getCloseableHttpClient(@Qualifier("httpClientBuilder") HttpClientBuilder httpClientBuilder) {

        CloseableHttpClient httpClient = httpClientBuilder.build();

        return httpClient;

    }

    /**
     * Builder是RequestConfig的一个内部类
     * <p>
     * 通过RequestConfig的custom方法来获取到一个Builder对象
     * <p>
     * 设置builder的连接信息
     * <p>
     * 这里还可以设置proxy，cookieSpec等属性。有需要的话可以在此设置
     *
     * @return
     */
    @Bean(name = "builder")
    public RequestConfig.Builder getBuilder() {

        RequestConfig.Builder builder = RequestConfig.custom();

        return builder.setConnectTimeout(connectTimeout).

                setConnectionRequestTimeout(connectionRequestTimeout).

                setSocketTimeout(socketTimeout).setRedirectsEnabled(true);

    }


    /**
     * 使用builder创建一个RequestConfig对象
     *
     * @param builder
     * @return
     */
    @Bean
    public RequestConfig getRequestConfig(@Qualifier("builder") RequestConfig.Builder builder) {

        RequestConfig requestConfig = builder.build();

        return requestConfig;

    }

}
