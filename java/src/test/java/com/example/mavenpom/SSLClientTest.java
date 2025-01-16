package com.example.mavenpom;

import com.example.mavenpom.client.SSLClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import java.math.BigInteger;
import java.util.Date;
import java.util.Base64;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SSLClientTest {
    private MockWebServer mockWebServer;
    private static final String TEST_RESPONSE = "Hello, SSL!";

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @Order(1)
    void testDefaultConstructor() throws IOException {
        // Configure mock server
        mockWebServer.enqueue(new MockResponse()
                .setBody(TEST_RESPONSE)
                .setResponseCode(200));

        // Create client using default constructor
        SSLClient client = new SSLClient();
        String response = client.makeRequest(mockWebServer.url("/").toString());

        // Verify response
        Assertions.assertEquals(TEST_RESPONSE, response);
    }

    @Test
    @Order(2)
    void testCustomCertificateConstructor() throws IOException {
        // Create a test certificate
        File certFile = createTestCertificate("test-file");

        // Ensure the file exists and is readable
        Assertions.assertTrue(certFile.exists(), "Certificate file should exist");
        Assertions.assertTrue(certFile.canRead(), "Certificate file should be readable");

        // Configure mock server
        mockWebServer.enqueue(new MockResponse()
                .setBody(TEST_RESPONSE)
                .setResponseCode(200));

        // Create client with custom certificate path
        SSLClient client = new SSLClient(certFile.getAbsolutePath());
        String response = client.makeRequest(mockWebServer.url("/").toString());

        // Verify response
        Assertions.assertEquals(TEST_RESPONSE, response);
    }



    @Test
    @Order(3)
    void testServerError() throws IOException {
        // Create client using default constructor
        SSLClient client = new SSLClient();

        // Configure mock server to return 500
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Server Error"));

        Exception exception = Assertions.assertThrows(IOException.class,
                () -> client.makeRequest(mockWebServer.url("/").toString())
        );

        Assertions.assertTrue(exception.getMessage().contains("Unexpected response code: 500"));
    }

    @Test
    @Order(4)
    void testMultipleCertificatesWithCustomPath() throws IOException {
        // Create multiple test certificates in one file
        File certFile = createTestCertificateWithMultiple("test-cert");

        // Ensure the file exists and is readable
        Assertions.assertTrue(certFile.exists(), "Certificate file should exist");
        Assertions.assertTrue(certFile.canRead(), "Certificate file should be readable");

        // Configure mock server
        mockWebServer.enqueue(new MockResponse()
                .setBody(TEST_RESPONSE)
                .setResponseCode(200));

        // Create client with custom certificate path
        SSLClient client = new SSLClient(certFile.getAbsolutePath());
        String response = client.makeRequest(mockWebServer.url("/").toString());

        // Verify response
        Assertions.assertEquals(TEST_RESPONSE, response);
    }

    @Test
    @Order(5)
    void testWithGeneratedX509Certificate() throws IOException, NoSuchAlgorithmException, 
            CertificateException, SignatureException, InvalidKeyException {
        // Generate key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // Generate certificate
        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        X500Principal dnName = new X500Principal("CN=Test Certificate");
        
        certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        certGen.setSubjectDN(dnName);
        certGen.setIssuerDN(dnName);
        certGen.setNotBefore(new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
        certGen.setPublicKey(keyPair.getPublic());
        certGen.setSignatureAlgorithm("SHA256withRSA");

        X509Certificate cert = certGen.generate(keyPair.getPrivate());

        // Save certificate to file
        File certFile = tempDir.resolve("generated-cert.pem").toFile();
        try (FileWriter writer = new FileWriter(certFile)) {
            writer.write("-----BEGIN CERTIFICATE-----\n");
            writer.write(Base64.getEncoder().encodeToString(cert.getEncoded()));
            writer.write("\n-----END CERTIFICATE-----\n");
        }

        // Configure mock server
        mockWebServer.enqueue(new MockResponse()
                .setBody(TEST_RESPONSE)
                .setResponseCode(200));

        // Create client with generated certificate
        SSLClient client = new SSLClient(certFile.getAbsolutePath());
        String response = client.makeRequest(mockWebServer.url("/").toString());

        // Verify response
        Assertions.assertEquals(TEST_RESPONSE, response);
    }

    private File createTestCertificate(String filename) throws IOException {
        File certFile = tempDir.resolve(filename + ".pem").toFile();
        try (FileWriter writer = new FileWriter(certFile)) {
            writer.write("-----BEGIN CERTIFICATE-----\n");
            writer.write("MIIDrzCCApegAwIBAgIQCDvgVpBCRrGhdWrJWZHHSjANBgkqhkiG9w0BAQUFADBh\n");
            writer.write("MQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3\n");
            writer.write("d3cuZGlnaWNlcnQuY29tMSAwHgYDVQQDExdEaWdpQ2VydCBHbG9iYWwgUm9vdCBD\n");
            writer.write("QTAeFw0wNjExMTAwMDAwMDBaFw0zMTExMTAwMDAwMDBaMGExCzAJBgNVBAYTAlVT\n");
            writer.write("MRUwEwYDVQQKEwxEaWdpQ2VydCBJbmMxGTAXBgNVBAsTEHd3dy5kaWdpY2VydC5j\n");
            writer.write("b20xIDAeBgNVBAMTF0RpZ2lDZXJ0IEdsb2JhbCBSb290IENBMIIBIjANBgkqhkiG\n");
            writer.write("9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4jvhEXLeqKTTo1eqUKKPC3eQyaKl7hLOllsB\n");
            writer.write("CSDMAZOnTjC3U/dDxGkAV53ijSLdhwZAAIEJzs4bg7/fzTtxRuLWZscFs3YnFo97\n");
            writer.write("nh6Vfe63SKMI2tavegw5BmV/Sl0fvBf4q77uKNd0f3p4mVmFaG5cIzJLv07A6Fpt\n");
            writer.write("43C/dxC//AH2hdmoRBBYMql1GNXRor5H4idq9Joz+EkIYIvUX7Q6hL+hqkpMfT7P\n");
            writer.write("T19sdl6gSzeRntwi5m3OFBqOasv+zbMUZBfHWymeMr/y7vrTC0LUq7dBMtoM1O/4\n");
            writer.write("gdW7jVg/tRvoSSiicNoxBN33shbyTApOB6jtSj1etX+jkMOvJwIDAQABo2MwYTAO\n");
            writer.write("BgNVHQ8BAf8EBAMCAYYwDwYDVR0TAQH/BAUwAwEB/zAdBgNVHQ4EFgQUA95QNVbR\n");
            writer.write("TLtm8KPiGxvDl7I90VUwHwYDVR0jBBgwFoAUA95QNVbRTLtm8KPiGxvDl7I90VUw\n");
            writer.write("DQYJKoZIhvcNAQEFBQADggEBAMucN6pIExIK+t1EnE9SsPTfrgT1eXkIoyQY/Esr\n");
            writer.write("hMAtudXH/vTBH1jLuG2cenTnmCmrEbXjcKChzUyImZOMkXDiqw8cvpOp/2PV5Adg\n");
            writer.write("06O/nVsJ8dWO41P0jmP6P6fbtGbfYmbW0W5BjfIttep3Sp+dWOIrWcBAI+0tKIJF\n");
            writer.write("PnlUkiaY4IBIqDfv8NZ5YBberOgOzW6sRBc4L0na4UU+Krk2U886UAb3LujEV0ls\n");
            writer.write("YSEY1QSteDwsOoBrp+uvFRTp2InBuThs4pFsiv9kuXclVzDAGySj4dzp30d8tbQk\n");
            writer.write("CAUw7C29C79Fv1C5qfPrmAESrciIxpg0X40KPMbp1ZWVbd4=\n");
            writer.write("-----END CERTIFICATE-----\n");
        }
        return certFile;
    }

    private File createTestCertificateWithMultiple(String filename) throws IOException {
        File certFile = tempDir.resolve(filename + ".pem").toFile();
        try (FileWriter writer = new FileWriter(certFile)) {
            // First certificate
            writer.write("-----BEGIN CERTIFICATE-----\n");
            writer.write("MIIDrzCCApegAwIBAgIQCDvgVpBCRrGhdWrJWZHHSjANBgkqhkiG9w0BAQUFADBh\n");
            writer.write("MQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3\n");
            writer.write("d3cuZGlnaWNlcnQuY29tMSAwHgYDVQQDExdEaWdpQ2VydCBHbG9iYWwgUm9vdCBD\n");
            writer.write("QTAeFw0wNjExMTAwMDAwMDBaFw0zMTExMTAwMDAwMDBaMGExCzAJBgNVBAYTAlVT\n");
            writer.write("MRUwEwYDVQQKEwxEaWdpQ2VydCBJbmMxGTAXBgNVBAsTEHd3dy5kaWdpY2VydC5j\n");
            writer.write("b20xIDAeBgNVBAMTF0RpZ2lDZXJ0IEdsb2JhbCBSb290IENBMIIBIjANBgkqhkiG\n");
            writer.write("9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4jvhEXLeqKTTo1eqUKKPC3eQyaKl7hLOllsB\n");
            writer.write("CSDMAZOnTjC3U/dDxGkAV53ijSLdhwZAAIEJzs4bg7/fzTtxRuLWZscFs3YnFo97\n");
            writer.write("nh6Vfe63SKMI2tavegw5BmV/Sl0fvBf4q77uKNd0f3p4mVmFaG5cIzJLv07A6Fpt\n");
            writer.write("43C/dxC//AH2hdmoRBBYMql1GNXRor5H4idq9Joz+EkIYIvUX7Q6hL+hqkpMfT7P\n");
            writer.write("T19sdl6gSzeRntwi5m3OFBqOasv+zbMUZBfHWymeMr/y7vrTC0LUq7dBMtoM1O/4\n");
            writer.write("gdW7jVg/tRvoSSiicNoxBN33shbyTApOB6jtSj1etX+jkMOvJwIDAQABo2MwYTAO\n");
            writer.write("BgNVHQ8BAf8EBAMCAYYwDwYDVR0TAQH/BAUwAwEB/zAdBgNVHQ4EFgQUA95QNVbR\n");
            writer.write("TLtm8KPiGxvDl7I90VUwHwYDVR0jBBgwFoAUA95QNVbRTLtm8KPiGxvDl7I90VUw\n");
            writer.write("DQYJKoZIhvcNAQEFBQADggEBAMucN6pIExIK+t1EnE9SsPTfrgT1eXkIoyQY/Esr\n");
            writer.write("hMAtudXH/vTBH1jLuG2cenTnmCmrEbXjcKChzUyImZOMkXDiqw8cvpOp/2PV5Adg\n");
            writer.write("06O/nVsJ8dWO41P0jmP6P6fbtGbfYmbW0W5BjfIttep3Sp+dWOIrWcBAI+0tKIJF\n");
            writer.write("PnlUkiaY4IBIqDfv8NZ5YBberOgOzW6sRBc4L0na4UU+Krk2U886UAb3LujEV0ls\n");
            writer.write("YSEY1QSteDwsOoBrp+uvFRTp2InBuThs4pFsiv9kuXclVzDAGySj4dzp30d8tbQk\n");
            writer.write("CAUw7C29C79Fv1C5qfPrmAESrciIxpg0X40KPMbp1ZWVbd4=\n");
            writer.write("-----END CERTIFICATE-----\n\n");

            // Second certificate (using a different root CA cert for variety)
            writer.write("-----BEGIN CERTIFICATE-----\n");
            writer.write("MIIDxTCCAq2gAwIBAgIQAqxcJmoLQJuPC3nyrkYldzANBgkqhkiG9w0BAQUFADBs\n");
            writer.write("MQswCQYDVQQGEwJVUzEVMBMGA1UEChMMRGlnaUNlcnQgSW5jMRkwFwYDVQQLExB3\n");
            writer.write("d3cuZGlnaWNlcnQuY29tMSswKQYDVQQDEyJEaWdpQ2VydCBIaWdoIEFzc3VyYW5j\n");
            writer.write("ZSBFViBSb290IENBMB4XDTA2MTExMDAwMDAwMFoXDTMxMTExMDAwMDAwMFowbDEL\n");
            writer.write("MAkGA1UEBhMCVVMxFTATBgNVBAoTDERpZ2lDZXJ0IEluYzEZMBcGA1UECxMQd3d3\n");
            writer.write("LmRpZ2ljZXJ0LmNvbTErMCkGA1UEAxMiRGlnaUNlcnQgSGlnaCBBc3N1cmFuY2Ug\n");
            writer.write("RVYgUm9vdCBDQTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMbM5XPm\n");
            writer.write("+9S75S0tMqbf5YE/yc0lSbZxKsPVlDRnogocsF9ppkCxxLeyj9CYpKlBWTrT3JTW\n");
            writer.write("PNt0OKRKzE0lgvdKpVMSOO7zSW1xkX5jtqumX8OkhPhPYlG++MXs2ziS4wblCJEM\n");
            writer.write("xChBVfvLWokVfnHoNb9Ncgk9vjo4UFt3MRuNs8ckRZqnrG0AFFoEt7oT61EKmEFB\n");
            writer.write("Ik5lYYeBQVCmeVyJ3hlKV9Uu5l0cUyx+mM0aBhakaHPQNAQTXKFx01p8VdteZOE3\n");
            writer.write("hzBWBOURtCmAEvF5OYiiAhF8J2a3iLd48soKqDirCmTCv2ZdlYTBoSUeh10aUAsg\n");
            writer.write("EsxBu24LUTi4S8sCAwEAAaNjMGEwDgYDVR0PAQH/BAQDAgGGMA8GA1UdEwEB/wQF\n");
            writer.write("MAMBAf8wHQYDVR0OBBYEFLE+w2kD+L9HAdSYJhoIAu9jZCvDMB8GA1UdIwQYMBaA\n");
            writer.write("FLE+w2kD+L9HAdSYJhoIAu9jZCvDMA0GCSqGSIb3DQEBBQUAA4IBAQAcGgaX3Nec\n");
            writer.write("nzyIZgYIVyHbIUf4KmeqvxgydkAQV8GK83rZEWWONfqe/EW1ntlMMUu4kehDLI6z\n");
            writer.write("eM7b41N5cdblIZQB2lWHmiRk9opmzN6cN82oNLFpmyPInngiK3BD41VHMWEZ71jF\n");
            writer.write("hS9OMPagMRYjyOfiZRYzy78aG6A9+MpeizGLYAiJLQwGXFK3xPkKmNEVX58Svnw2\n");
            writer.write("Yzi9RKR/5CYrCsSXaQ3pjOLAEFe4yHYSkVXySGnYvCoCWw9E1CAx2/S6cCZdkGCe\n");
            writer.write("vEsXCS+0yx5DaMkHJ8HSXPfqIbloEpw8nL+e/IBcm2PN7EeqJSdnoDfzAIJ9VNep\n");
            writer.write("+OkuE6N36B9K\n");
            writer.write("-----END CERTIFICATE-----\n");
            writer.flush();
        }
        return certFile;
    }

    private static final String TEST_CERTIFICATE =
            "-----BEGIN CERTIFICATE-----\n" +
                    "MIIDdzCCAl+gAwIBAgIEAgAAuTANBgkqhkiG9w0BAQUFADBaMQswCQYDVQQGEwJJ\n" +
                    // ... rest of the certificate content remains the same ...
                    "-----END CERTIFICATE-----";
}