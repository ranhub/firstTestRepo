package servicebooking;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import com.ford.turbo.aposb.common.basemodels.controller.exception.ControllerExceptionHandler;
import com.ford.turbo.aposb.common.interceptors.HttpTraceInterceptor;
import com.ford.turbo.servicebooking.ServiceBookingRestTemplate;
import com.ford.turbo.servicebooking.mutualauth.HostnameVerifierFactory;
import com.ford.turbo.servicebooking.mutualauth.MutualAuthRestTemplate;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.ssl.SSLContexts;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.net.ssl.SSLContext;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProxySelector;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableSwagger2
public class ServiceBookingConfiguration {
	
	@Value("${security.user.name}")
    private String username;

    @Value("${security.user.password}")
    private String password;
    
    @Value("${com.ford.turbo.servicebooking.resttemplate.connectionRequestTimeout}")
    private int connectionRequestTimeout;
    
    @Value("${com.ford.turbo.servicebooking.resttemplate.connectTimeout}")
    private int connectTimeout;
    
    @Value("${com.ford.turbo.servicebooking.resttemplate.readTimeout}")
    private int readTimeout;
    
    @Value("${com.ford.turbo.servicebooking.resttemplate.connection.ttl}")
    private int connectionTTL;
    
    @Value("${com.ford.turbo.servicebooking.connection.pooling.datapower.maxTotal}")
    private int dataPowerMaxTotal;
    
    @Value("${com.ford.turbo.servicebooking.connection.pooling.ngsdn.maxTotal}")
    private int ngsdnMaxTotal;
    
    @Value("${com.ford.turbo.servicebooking.connection.pooling.vinlookup.maxTotal}")
    private int vinLookupMaxTotal;
    @Value("${com.ford.turbo.servicebooking.connection.pooling.maxTotal}")
    private int maxTotal;
    
    @Bean
    public Docket swaggerSpringMvcPlugin(@Value("${com.ford.msl.api-doc-title}") String title,
    		                             @Value("${com.ford.msl.api-doc-description}") String description,
                                         @Value("${com.ford.msl.api-doc-created-by}") String createdBy,
                                         @Value("${spring.application.version}") String version) throws IOException {

        Contact contact = new Contact(createdBy, null, null);
        ApiInfo apiInfo = new ApiInfo(title, description, version, null, contact, null, null);

        HashSet<String> protocols = new HashSet<>();
        protocols.add(SwaggerDefinition.Scheme.HTTPS.name());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .protocols(protocols)
                .useDefaultResponseMessages(false)
                .directModelSubstitute(XMLGregorianCalendar.class, String.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ford.turbo.servicebooking"))
                .paths(PathSelectors.any())
                .build();
    }

    @Bean
    @Qualifier("MSL_APPLICATION_BACKENDS")
    public CredentialsSource getMslApplicationBackends() throws IOException {
        return new CredentialsSource("MSL_APPLICATION_BACKENDS");
    }

    @Bean
    @Qualifier("OSB_DATAPOWER")
    public CredentialsSource osbDataPowerSource() throws IOException {
        return new CredentialsSource("OSB_DATAPOWER");
    }
 
    @Bean
    @Qualifier("NGSDN")
    public CredentialsSource ngsdnSource() throws IOException {
        return new CredentialsSource("NGSDN");
    }
    
    @Bean
    @Qualifier("OSB_DATAPOWER")
    public MutualAuthRestTemplate mutualAuthRestTemplate(@Qualifier("OSB_DATAPOWER") CredentialsSource credentialsSource, HttpTraceInterceptor httpTraceInterceptor) {

    	MutualAuthRestTemplate mutualAuthRestTemplate = new MutualAuthRestTemplate(credentialsSource);
    	HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = createRequestFactory(credentialsSource, dataPowerMaxTotal, connectionTTL);

        clientHttpRequestFactory.setReadTimeout(readTimeout);
        clientHttpRequestFactory.setConnectTimeout(connectTimeout);
        clientHttpRequestFactory.setConnectionRequestTimeout(connectionRequestTimeout);

        mutualAuthRestTemplate.setInterceptors(Arrays.asList(httpTraceInterceptor));
        mutualAuthRestTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory));
        
        return mutualAuthRestTemplate;
    }
    
    private static HttpComponentsClientHttpRequestFactory createRequestFactory(CredentialsSource credentialsSource, int maxTotal, int connectionTTL) {
   	 try {
   		 
            InputStream keyIn = credentialsSource.getJavaKeyStore();
            char[] keystorePassword = credentialsSource.getKeystorePassword();
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

            trustStore.load(keyIn, keystorePassword);

            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(trustStore, null) // trusted certificates for validating remote server
                    .loadKeyMaterial(trustStore, keystorePassword) // our client cert and private key
                    .build();

           SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslcontext,
                    HostnameVerifierFactory.createCustomHostnameVerifier(null));

           return	new HttpComponentsClientHttpRequestFactory(
           				HttpClientBuilder
           				.create()
           				.useSystemProperties()
           				.setRoutePlanner(new SystemDefaultRoutePlanner(null, ProxySelector.getDefault()))  // use System properties for proxy configuratioin
           				.setSSLSocketFactory(socketFactory)
           				.addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor())
           				.evictExpiredConnections()
           				.setMaxConnPerRoute(maxTotal)
           				.setMaxConnTotal(maxTotal)
           				.setConnectionTimeToLive(connectionTTL, TimeUnit.MINUTES)
           				.evictIdleConnections(connectionTTL, TimeUnit.MINUTES)
           				.build()
           				);
                
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | IOException | CertificateException | UnrecoverableKeyException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public ControllerExceptionHandler advice() {
        return new ControllerExceptionHandler();
    }
    
    @Bean
    @Qualifier("APOSB")
    public CredentialsSource apOSBCredentialSource() throws IOException {
    	return new CredentialsSource("APOSB");
    }
    
    @Bean
    @Qualifier("MSL_APPLICATION_ID")
    public CredentialsSource mslApplicationIdCredentialSource() throws IOException {
    	return new CredentialsSource("MSL_APPLICATION_ID");
    }
    
    
    @Bean
    @Qualifier("AUTO_NAVI")
    public CredentialsSource getAutoNavi() throws IOException {
        return new CredentialsSource("AUTO_NAVI");
    }
        
    @Bean
    @Qualifier("AZURE_AD_CLIENT_APOSB")
    public CredentialsSource azureADAPOSBCredentialsSource() throws IOException {
    	return new CredentialsSource("AZURE_AD_CLIENT_APOSB");
    }

	@Bean
    @Qualifier("CORS_ALLOWED_ORIGINS")
	public CredentialsSource corsAllowedOrigins() throws IOException {
    	return new CredentialsSource("CORS_ALLOWED_ORIGINS");
    }
	
	@Bean
    @Qualifier("PUBLIC_APP_ID_MAPPINGS")
	public CredentialsSource publicAppIdMappings() throws IOException {
    	return new CredentialsSource("PUBLIC_APP_ID_MAPPINGS");
    }
	
	@Bean
	@Qualifier("NGSDN_REST_TEMPLATE")
	public RestTemplate getNGSDNRestTemplate(HttpTraceInterceptor httpTraceInterceptor) {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
				HttpClientBuilder
    	        .create()
    	        .useSystemProperties()
    	        .evictIdleConnections(connectionTTL, TimeUnit.MINUTES)
    	        .setConnectionManager(getPoolingHttpClientConnectionManager(connectionTTL, ngsdnMaxTotal))
    	        .setConnectionTimeToLive(connectionTTL, TimeUnit.MINUTES)
    	        .build());
		
    	requestFactory.setConnectionRequestTimeout(connectionRequestTimeout);
    	requestFactory.setConnectTimeout(connectTimeout);
    	requestFactory.setReadTimeout(readTimeout);
    	
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		restTemplate.setInterceptors(Collections.singletonList(httpTraceInterceptor));
		return restTemplate;
	}
	
	@Bean
	@Qualifier("VINLOOKUP_REST_TEMPLATE")
	public RestTemplate getVinLookupRestTemplate(HttpTraceInterceptor httpTraceInterceptor) {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
				HttpClientBuilder
    	        .create()
    	        .useSystemProperties()
    	        .evictIdleConnections(connectionTTL, TimeUnit.MINUTES)
    	        .setConnectionManager(getPoolingHttpClientConnectionManager(connectionTTL, vinLookupMaxTotal))
    	        .setConnectionTimeToLive(connectionTTL, TimeUnit.MINUTES)
    	        .build());
		
    	requestFactory.setConnectionRequestTimeout(connectionRequestTimeout);
    	requestFactory.setConnectTimeout(connectTimeout);
    	requestFactory.setReadTimeout(readTimeout);
    	
    	RestTemplate restTemplate = new RestTemplate(requestFactory);
		restTemplate.setInterceptors(Collections.singletonList(httpTraceInterceptor));
		return restTemplate;
	}
	
	@Bean(name="serviceBookingRestTemplate")
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public ServiceBookingRestTemplate serviceBookingRestTemplate(HttpTraceInterceptor httpTraceInterceptor) {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
				HttpClientBuilder
    	        .create()
    	        .useSystemProperties()
    	        .evictIdleConnections(connectionTTL, TimeUnit.MINUTES)
    	        .setConnectionManager(getPoolingHttpClientConnectionManager(connectionTTL, maxTotal))
    	        .setConnectionTimeToLive(connectionTTL, TimeUnit.MINUTES)
    	        .build());
		
    	requestFactory.setConnectionRequestTimeout(connectionRequestTimeout);
    	requestFactory.setConnectTimeout(connectTimeout);
    	requestFactory.setReadTimeout(readTimeout);
    	
    	ServiceBookingRestTemplate restTemplate = new ServiceBookingRestTemplate(requestFactory);
		restTemplate.setInterceptors(Collections.singletonList(httpTraceInterceptor));
		return restTemplate;
	}
	
    public PoolingHttpClientConnectionManager getPoolingHttpClientConnectionManager(int connectionTTL, int maxTotal) {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(connectionTTL,
				TimeUnit.MINUTES);
		connectionManager.setMaxTotal(maxTotal);
		connectionManager.setDefaultMaxPerRoute(maxTotal);
		return connectionManager;
    }
}
