package com.ford.turbo.aposb.common.authsupport.mutualauth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProxySelector;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLContext;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;
import org.apache.http.Header;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MutualAuthHelper {

    private MutualAuthHelper() {

    }

    static HttpClient initHttpClient(CredentialsSource backendCredentialsSource, CredentialsSource hostnameCredentialsSource) {
        try {
            InputStream keyIn = backendCredentialsSource.getJavaKeyStore();
            char[] keystorePassword = backendCredentialsSource.getKeystorePassword();
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

            trustStore.load(keyIn, keystorePassword);
            log.debug("Loaded keystore");

            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(trustStore, null) // trusted certificates for validating remote server
                    .loadKeyMaterial(trustStore, keystorePassword) // our client cert and private key
                    .build();

            SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslcontext,
                    HostnameVerifierFactory.createCustomHostnameVerifier(hostnameCredentialsSource));

            return HttpClientBuilder.create()
            		.setRoutePlanner(new SystemDefaultRoutePlanner(null, ProxySelector.getDefault()))  // use System properties for proxy configuratioin
                    .setSSLSocketFactory(socketFactory)
                    .addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor())
                    .addInterceptorLast(new RequestLoggingInterceptor())
                    .addInterceptorLast(new ResponseLoggingInterceptor())
                    .build();

        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | IOException | CertificateException | UnrecoverableKeyException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static class RequestLoggingInterceptor implements HttpRequestInterceptor {
        @Override
        public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
            StringBuilder sb = new StringBuilder(1000);
            if (request instanceof HttpUriRequest) {
                HttpUriRequest uriRequest = (HttpUriRequest) request;
                sb.append(uriRequest.getMethod())
                        .append(" ")
                        .append(uriRequest.getURI())
                        .append(" ");
            }
            Header[] allHeaders = request.getAllHeaders();
            for(Header header : allHeaders) {
            	if(!"Cookie".equalsIgnoreCase(header.getName())) {
            		sb.append(header);
            	}
            }
            log.info(sb.toString());

            if (log.isDebugEnabled() && request instanceof HttpEntityEnclosingRequest) {
                HttpEntityEnclosingRequest requestWithBody = (HttpEntityEnclosingRequest) request;
                ByteArrayOutputStream buf = new ByteArrayOutputStream(5000);
                requestWithBody.getEntity().writeTo(buf);
                log.debug(buf.toString());
            }
        }
    }

    private static class ResponseLoggingInterceptor implements HttpResponseInterceptor {

        @Override
        public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
            StringBuilder sb = new StringBuilder(1000);
            sb.append(response.getStatusLine().getStatusCode());
            
            Header[] allHeaders = response.getAllHeaders();
            for(Header header : allHeaders) {
            	if(!"Set-Cookie".equalsIgnoreCase(header.getName())) {
            		sb.append(header);
            	}
            }
            log.info(sb.toString());
        }

    }
}
