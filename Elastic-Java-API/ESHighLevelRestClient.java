package usr.es.op;


import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ESHighLevelRestClient {
    @Value("${bootstrap.eshost}")
    private String eshost;
    @Value("${bootstrap.esapiport}")
    private int esapiport;
    @Value("${bootstrap.esuser}")
    private String esuser;
    @Value("${bootstrap.espwd}")
    private String espwd;
    @Value("${bootstrap.escluster}")
    private String escluster;
    @Value("${bootstrap.esconntimeout}")
    private int esconntimeout;

    RestHighLevelClient client;

    public RestHighLevelClient getRestClient() {
        RestClient lowLevelClient ;
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(esuser, espwd));

        RestClientBuilder builder = RestClient.builder(new HttpHost(eshost, esapiport))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });
        lowLevelClient = builder.build();
        client =  new RestHighLevelClient(builder);
        return client;
    }
}
