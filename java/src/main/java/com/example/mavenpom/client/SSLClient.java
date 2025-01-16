package com.example.mavenpom.client;

import com.intellij.openapi.diagnostic.Logger;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.io.FileInputStream;
import java.io.File;

public class SSLClient {
    private static final Logger LOG = Logger.getInstance(SSLClient.class);
    private final String certPath;
    private final OkHttpClient client;

    private static final String DEFAULT_CERT_PATH = "certificates/multi-certificate.pem";

    public SSLClient() {
        this(DEFAULT_CERT_PATH);
    }

    public SSLClient(String customCertPath) {
        this.certPath = customCertPath;
        LOG.info("Initializing SSLClient with cert path: " + certPath);
        client = createTrustedClient();
    }

    private OkHttpClient createTrustedClient() {
        try {
            InputStream certInputStream = loadCertificateStream();
            if (certInputStream == null) {
                throw new RuntimeException("Failed to load certificate from: " + certPath);
            }

            try (BufferedInputStream bufferedInputStream = new BufferedInputStream(certInputStream)) {
                // Create a KeyStore containing our trusted CAs
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(null, null);

                // Load certificates from PEM file
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(bufferedInputStream);

                if (certificates.isEmpty()) {
                    throw new RuntimeException("No certificates found in file: " + certPath);
                }

                LOG.info("Number of certificates loaded: " + certificates.size());

                // Add certificates to KeyStore
                int index = 0;
                for (Certificate certificate : certificates) {
                    if (certificate instanceof X509Certificate) {
                        String certificateAlias = "cert" + index++;
                        keyStore.setCertificateEntry(certificateAlias, certificate);
                    }
                }

            // Create a TrustManager that trusts the CAs in our KeyStore
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);

                // Create OkHttpClient with our custom SSLContext
                return new OkHttpClient.Builder()
                        .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManagers[0])
                        .build();

            } catch (IOException e) {
                LOG.error("IO error while processing certificate", e);
                throw new RuntimeException("Error processing certificate: " + e.getMessage(), e);
            } catch (Exception e) {
                LOG.error("Error creating SSL client", e);
                throw new RuntimeException("Error creating SSL client: " + e.getMessage(), e);
            }
        } catch (RuntimeException e) {
            LOG.error("Failed to create SSL client", e);
            throw e;
        }
    }

    private InputStream loadCertificateStream() {
        File file = new File(certPath);
        InputStream certInputStream = null;

        // 1. Try loading as absolute file path first
        if (file.exists() && file.canRead()) {
            try {
                LOG.info("Loading certificate from absolute path: " + certPath);
                return new FileInputStream(file);
            } catch (IOException e) {
                LOG.warn("Failed to load certificate from file: " + certPath, e);
            }
        }

        // 2. Try loading from classpath using ClassLoader
        try {
            certInputStream = SSLClient.class.getClassLoader().getResourceAsStream(certPath);
            if (certInputStream != null) {
                LOG.info("Successfully loaded certificate from classpath: " + certPath);
                return certInputStream;
            }
        } catch (Exception e) {
            LOG.warn("Failed to load certificate using ClassLoader: " + certPath, e);
        }

        // 3. Try with direct resource path
        try {
            certInputStream = SSLClient.class.getResourceAsStream(certPath);
            if (certInputStream != null) {
                LOG.info("Successfully loaded certificate from direct resource path: " + certPath);
                return certInputStream;
            }
        } catch (Exception e) {
            LOG.warn("Failed to load certificate using direct resource path: " + certPath, e);
        }

        // 4. Try without leading slash
        try {
            String altPath = certPath.startsWith("/") ? certPath.substring(1) : certPath;
            certInputStream = SSLClient.class.getClassLoader().getResourceAsStream(altPath);
            if (certInputStream != null) {
                LOG.info("Successfully loaded certificate from alternative path: " + altPath);
                return certInputStream;
            }
        } catch (Exception e) {
            LOG.warn("Failed to load certificate using alternative path: " + certPath, e);
        }

        // If we get here, we couldn't load the certificate
        LOG.error("Failed to load certificate from any location: " + certPath);
        return null;
    }

    public String makeRequest(String targetUrl) throws IOException {
        if (targetUrl == null || targetUrl.isEmpty()) {
            throw new IllegalArgumentException("Target URL cannot be null or empty");
        }

        Request request = new Request.Builder()
                .url(targetUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }
            if (response.body() == null) {
                throw new IOException("Response body is null");
            }
            return response.body().string();
        }
    }
}