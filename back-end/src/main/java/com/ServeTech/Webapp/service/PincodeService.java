package com.ServeTech.Webapp.service;

import com.ServeTech.Webapp.dto.response.PincodeApiResponse;
import com.ServeTech.Webapp.entity.Location;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.cert.X509Certificate;
import java.util.Collections;

@Service
public class PincodeService {

    private final RestTemplate restTemplate;

    public PincodeService() {
        // Create an SSL-trusting RestTemplate for dev mode
        // The postalpincode.in API has an expired SSL certificate
        this.restTemplate = createTrustAllRestTemplate();
    }

    public Location fetchLocation(String pincode) {
        String url = "https://api.postalpincode.in/pincode/" + pincode;

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0");
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<PincodeApiResponse[]> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        PincodeApiResponse[].class
                );

        PincodeApiResponse[] body = response.getBody();

        if (body == null || body.length == 0) {
            throw new RuntimeException("No response from API");
        }

        PincodeApiResponse apiResponse = body[0];

        if (!"Success".equalsIgnoreCase(apiResponse.getStatus())) {
            throw new RuntimeException("Invalid Pincode");
        }

        return apiResponse.getPostOffice().get(0);
    }

    /**
     * Creates a RestTemplate that trusts all SSL certificates.
     * DEV MODE ONLY — do NOT use in production.
     */
    private RestTemplate createTrustAllRestTemplate() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            HostnameVerifier allHostsValid = (hostname, session) -> true;

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory() {
                @Override
                protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                    if (connection instanceof HttpsURLConnection httpsConn) {
                        httpsConn.setSSLSocketFactory(sslContext.getSocketFactory());
                        httpsConn.setHostnameVerifier(allHostsValid);
                    }
                    super.prepareConnection(connection, httpMethod);
                }
            };
            factory.setConnectTimeout(5000);
            factory.setReadTimeout(10000);

            return new RestTemplate(factory);
        } catch (Exception e) {
            System.err.println("Failed to create SSL-trusting RestTemplate, using default: " + e.getMessage());
            return new RestTemplate();
        }
    }
}
