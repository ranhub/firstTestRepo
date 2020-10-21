package com.ford.turbo.aposb.common.authsupport;

import com.ford.turbo.aposb.common.authsupport.environment.CredentialsSource;

import java.io.IOException;

public class CredentialsSourceHelper {

    public static String simpleValuesJson = "{\n" +
                "      \"user-provided\": [\n" +
                "        {\n" +
                "          \"name\": \"PingCreds\",\n" +
                "          \"label\": \"user-provided\",\n" +
                "          \"tags\": [],\n" +
                "          \"credentials\": {\n" +
                "            \"certAndKey\": \"VGhpcyBpcyBhIHRlc3Qgb2YgdXNpbmcgYSBjZXJ0aWZpY2F0ZSBmcm9tIGEgdXNlciBwcm92aWRlZCBzZXJ2aWNlIGluIGNm\",\n" +
                "            \"keystorePassword\": \"xyz\",\n" +
                "            \"baseUri\": \"https://testbaseurl\"\n" +
                "          },\n" +
                "          \"syslog_drain_url\": \"\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }";

    public static String simpleValuesDisabledJson = "{\n" +
            "      \"user-provided\": [\n" +
            "        {\n" +
            "          \"name\": \"PingCredsDisabled\",\n" +
            "          \"label\": \"user-provided\",\n" +
            "          \"tags\": [],\n" +
            "          \"credentials\": {\n" +
            "            \"certAndKey\": \"VGhpcyBpcyBhIHRlc3Qgb2YgdXNpbmcgYSBjZXJ0aWZpY2F0ZSBmcm9tIGEgdXNlciBwcm92aWRlZCBzZXJ2aWNlIGluIGNm\",\n" +
            "            \"keystorePassword\": \"xyz\",\n" +
            "            \"baseUri\": \"https://testbaseurl\",\n" +
            "            \"disabled\": true\n" +
            "          },\n" +
            "          \"syslog_drain_url\": \"\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }";
    
    public static String simpleValuesNotDisabledJson = "{\n" +
            "      \"user-provided\": [\n" +
            "        {\n" +
            "          \"name\": \"PingCredsNotDisabled\",\n" +
            "          \"label\": \"user-provided\",\n" +
            "          \"tags\": [],\n" +
            "          \"credentials\": {\n" +
            "            \"certAndKey\": \"VGhpcyBpcyBhIHRlc3Qgb2YgdXNpbmcgYSBjZXJ0aWZpY2F0ZSBmcm9tIGEgdXNlciBwcm92aWRlZCBzZXJ2aWNlIGluIGNm\",\n" +
            "            \"keystorePassword\": \"xyz\",\n" +
            "            \"baseUri\": \"https://testbaseurl\",\n" +
            "            \"disabled\": \"sdsss\"\n" +
            "          },\n" +
            "          \"syslog_drain_url\": \"\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }";
    
    public static String validCertStructureJson = "{\n" +
                "      \"user-provided\": [\n" +
                "        {\n" +
                "          \"name\": \"PingCreds\",\n" +
                "          \"label\": \"user-provided\",\n" +
                "          \"tags\": [],\n" +
                "          \"credentials\": {\n" +
                "            \"certAndKey\": \"" +
                "/u3+7QAAAAIAAAABAAAAAQAFbXlrZXkAAAFRgyolAAAAAZAwggGMMA4GCisGAQQBKgIRAQEFAASCAXgDQ1ttoiGsVd4DoBZNX28alpM" +
                "LVDRk+wKmEDPIxVhLNs+y33s3eHr20jvr5twmCSacX2klrFP2PqSTbloMHhWPziVqGp303+tzHYy7BF2vEeExoCg9ImiSOA2O3e10n3" +
                "SyffPf+PdCVxRkCW55TJwKXToumt3jnYjIhZidDB86K93GxV4QUGY4WK+Dy7yPmgaqFkslLc47jI7rBl4J9S+sjxMkg1hKVTox8wDBo" +
                "MO0wBhcGbbZTWOnDgkQjf8dznubONZpYARPXS36FSJvcsbkWpgl17ehsp86XwcljWkSz6VzrnabuhkQe3EPxISeqZcay8tHUHwSCmUM" +
                "sfhixvcXMI8JY4crjBwclrxsHbXn6e57TUXZI73gpprWQErN5y0i8E5kOwfzFzQd+uhnMsssv01hYMkuwNW0noVL9L6Q7nL/WG6QJH5" +
                "5N8gMHBMNlT4YJVZQ+Xbtsx3roQDAi5T5oZHkRh7w+qB/q95zj74sRbRzS1j0/B/aAAAAAQAFWC41MDkAAANCMIIDPjCCAvygAwIBAg" +
                "IEWCl21TALBgcqhkjOOAQDBQAwcTELMAkGA1UEBhMCQ0ExEDAOBgNVBAgTB09udGFyaW8xEDAOBgNVBAcTB1Rvcm9udG8xEDAOBgNVB" +
                "AoTB1Bpdm90YWwxEDAOBgNVBAsTB1Bpdm90YWwxGjAYBgNVBAMTEU1hcmsgSGV0aGVyaW5ndG9uMB4XDTE1MTIwODE5NTU0N1oXDTE2" +
                "MDMwNzE5NTU0N1owcTELMAkGA1UEBhMCQ0ExEDAOBgNVBAgTB09udGFyaW8xEDAOBgNVBAcTB1Rvcm9udG8xEDAOBgNVBAoTB1Bpdm9" +
                "0YWwxEDAOBgNVBAsTB1Bpdm90YWwxGjAYBgNVBAMTEU1hcmsgSGV0aGVyaW5ndG9uMIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4" +
                "EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1" +
                "/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+Gg" +
                "hdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnW" +
                "RbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGAWXsJwzphSh2PqsVxF6aoZAP4ipcU2a" +
                "NSxP5bFu6XZ1GO7DqvQwUwxqMOJCuRK9fm19dGlYr65OPNDIIGNdzHnqAPufE0fgRTwKeUNPzy9I8iAbdxLB3t5sdnZZ0RwaLxdKiKy" +
                "ffEaVi7ZWGePkK0cua6igwIV+PXiz4Gjd+IgfajITAfMB0GA1UdDgQWBBS++X3+KLJIpg1Qoq2ap8j0LYUmITALBgcqhkjOOAQDBQAD" +
                "LwAwLAIUCQDPecfRs39I55BiS9Cr8zSxl34CFFZ0ULZbnJsn32SzNtZpAsO2T4Pnn0RreQyoHYWGkpaxrH09aaWjiOk=\",\n" +
                "            \"keystorePassword\": \"samplepassword\",\n" +
                "            \"baseUri\": \"https://testbaseurl\"\n" +
                "          },\n" +
                "          \"syslog_drain_url\": \"\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }";

    public static String UPScontainingRabbitService = "{  \n" +
            "   \"user-provided\":[  \n" +
            "      {  \n" +
            "         \"name\":\"PingCreds\",\n" +
            "         \"label\":\"user-provided\",\n" +
            "         \"tags\":[  \n" +
            "\n" +
            "         ],\n" +
            "         \"credentials\":{  \n" +
            "            \"keystorePassword\":\"Xtremer1\",\n" +
            "            \"baseUri\":\"https://testbaseurl\",\n" +
            "            \"certAndKey\":\"testing\"\n" +
            "         },\n" +
            "         \"syslog_drain_url\":\"\"\n" +
            "      }\n" +
            "   ],\n" +
            "   \"p-rabbitmq\":[  \n" +
            "      {  \n" +
            "         \"name\":\"hystrix-rabbitmq\",\n" +
            "         \"label\":\"p-rabbitmq\",\n" +
            "         \"tags\":[  \n" +
            "            \"rabbitmq\",\n" +
            "            \"messaging\",\n" +
            "            \"message-queue\",\n" +
            "            \"amqp\",\n" +
            "            \"stomp\",\n" +
            "            \"mqtt\",\n" +
            "            \"pivotal\"\n" +
            "         ],\n" +
            "         \"plan\":\"standard\",\n" +
            "         \"credentials\":{  \n" +
            "            \"http_api_uris\":[  \n" +
            "               \"https://eb2b8570-28f0-40d8-ade8-e5349dfb3824:qhg8ueph3c826954as8f3phv9a@pivotal-rabbitmq.cl-dev01.cf.ford.com/api/\"\n" +
            "            ],\n" +
            "            \"ssl\":false,\n" +
            "            \"dashboard_url\":\"https://pivotal-rabbitmq.cl-dev01.cf.ford.com/#/login/eb2b8570-28f0-40d8-ade8-e5349dfb3824/qhg8ueph3c826954as8f3phv9a\",\n" +
            "            \"password\":\"qhg8ueph3c826954as8f3phv9a\",\n" +
            "            \"protocols\":{  \n" +
            "               \"management\":{  \n" +
            "                  \"path\":\"/api/\",\n" +
            "                  \"ssl\":false,\n" +
            "                  \"hosts\":[  \n" +
            "                     \"10.0.16.98\"\n" +
            "                  ],\n" +
            "                  \"password\":\"qhg8ueph3c826954as8f3phv9a\",\n" +
            "                  \"username\":\"eb2b8570-28f0-40d8-ade8-e5349dfb3824\",\n" +
            "                  \"port\":15672,\n" +
            "                  \"host\":\"10.0.16.98\",\n" +
            "                  \"uri\":\"http://eb2b8570-28f0-40d8-ade8-e5349dfb3824:qhg8ueph3c826954as8f3phv9a@10.0.16.98:15672/api/\",\n" +
            "                  \"uris\":[  \n" +
            "                     \"http://eb2b8570-28f0-40d8-ade8-e5349dfb3824:qhg8ueph3c826954as8f3phv9a@10.0.16.98:15672/api/\"\n" +
            "                  ]\n" +
            "               },\n" +
            "               \"amqp\":{  \n" +
            "                  \"vhost\":\"6905224e-c8ba-44cb-8063-230b492592d4\",\n" +
            "                  \"username\":\"eb2b8570-28f0-40d8-ade8-e5349dfb3824\",\n" +
            "                  \"password\":\"qhg8ueph3c826954as8f3phv9a\",\n" +
            "                  \"port\":5672,\n" +
            "                  \"host\":\"10.0.16.98\",\n" +
            "                  \"hosts\":[  \n" +
            "                     \"10.0.16.98\"\n" +
            "                  ],\n" +
            "                  \"ssl\":false,\n" +
            "                  \"uri\":\"amqp://eb2b8570-28f0-40d8-ade8-e5349dfb3824:qhg8ueph3c826954as8f3phv9a@10.0.16.98:5672/6905224e-c8ba-44cb-8063-230b492592d4\",\n" +
            "                  \"uris\":[  \n" +
            "                     \"amqp://eb2b8570-28f0-40d8-ade8-e5349dfb3824:qhg8ueph3c826954as8f3phv9a@10.0.16.98:5672/6905224e-c8ba-44cb-8063-230b492592d4\"\n" +
            "                  ]\n" +
            "               },\n" +
            "               \"mqtt\":{  \n" +
            "                  \"username\":\"6905224e-c8ba-44cb-8063-230b492592d4:eb2b8570-28f0-40d8-ade8-e5349dfb3824\",\n" +
            "                  \"password\":\"qhg8ueph3c826954as8f3phv9a\",\n" +
            "                  \"port\":1883,\n" +
            "                  \"host\":\"10.0.16.98\",\n" +
            "                  \"hosts\":[  \n" +
            "                     \"10.0.16.98\"\n" +
            "                  ],\n" +
            "                  \"ssl\":false,\n" +
            "                  \"uri\":\"mqtt://6905224e-c8ba-44cb-8063-230b492592d4%3Aeb2b8570-28f0-40d8-ade8-e5349dfb3824:qhg8ueph3c826954as8f3phv9a@10.0.16.98:1883\",\n" +
            "                  \"uris\":[  \n" +
            "                     \"mqtt://6905224e-c8ba-44cb-8063-230b492592d4%3Aeb2b8570-28f0-40d8-ade8-e5349dfb3824:qhg8ueph3c826954as8f3phv9a@10.0.16.98:1883\"\n" +
            "                  ]\n" +
            "               },\n" +
            "               \"stomp\":{  \n" +
            "                  \"vhost\":\"6905224e-c8ba-44cb-8063-230b492592d4\",\n" +
            "                  \"username\":\"eb2b8570-28f0-40d8-ade8-e5349dfb3824\",\n" +
            "                  \"password\":\"qhg8ueph3c826954as8f3phv9a\",\n" +
            "                  \"port\":61613,\n" +
            "                  \"host\":\"10.0.16.98\",\n" +
            "                  \"hosts\":[  \n" +
            "                     \"10.0.16.98\"\n" +
            "                  ],\n" +
            "                  \"ssl\":false,\n" +
            "                  \"uri\":\"stomp://eb2b8570-28f0-40d8-ade8-e5349dfb3824:qhg8ueph3c826954as8f3phv9a@10.0.16.98:61613\",\n" +
            "                  \"uris\":[  \n" +
            "                     \"stomp://eb2b8570-28f0-40d8-ade8-e5349dfb3824:qhg8ueph3c826954as8f3phv9a@10.0.16.98:61613\"\n" +
            "                  ]\n" +
            "               }\n" +
            "            },\n" +
            "            \"username\":\"eb2b8570-28f0-40d8-ade8-e5349dfb3824\",\n" +
            "            \"hostname\":\"10.0.16.98\",\n" +
            "            \"hostnames\":[  \n" +
            "               \"10.0.16.98\"\n" +
            "            ],\n" +
            "            \"vhost\":\"6905224e-c8ba-44cb-8063-230b492592d4\",\n" +
            "            \"http_api_uri\":\"https://eb2b8570-28f0-40d8-ade8-e5349dfb3824:qhg8ueph3c826954as8f3phv9a@pivotal-rabbitmq.cl-dev01.cf.ford.com/api/\",\n" +
            "            \"uri\":\"amqp://eb2b8570-28f0-40d8-ade8-e5349dfb3824:qhg8ueph3c826954as8f3phv9a@10.0.16.98/6905224e-c8ba-44cb-8063-230b492592d4\",\n" +
            "            \"uris\":[  \n" +
            "               \"amqp://eb2b8570-28f0-40d8-ade8-e5349dfb3824:qhg8ueph3c826954as8f3phv9a@10.0.16.98/6905224e-c8ba-44cb-8063-230b492592d4\"\n" +
            "            ]\n" +
            "         }\n" +
            "      }\n" +
            "   ]\n" +
            "}";

    public static String missingBaseUri = "{\n" +
            "      \"user-provided\": [\n" +
            "        {\n" +
            "          \"name\": \"PingCreds\",\n" +
            "          \"label\": \"user-provided\",\n" +
            "          \"tags\": [],\n" +
            "          \"credentials\": {\n" +
            "            \"certAndKey\": \"" +
            "/u3+7QAAAAIAAAABAAAAAQAFbXlrZXkAAAFRgyolAAAAAZAwggGMMA4GCisGAQQBKgIRAQEFAASCAXgDQ1ttoiGsVd4DoBZNX28alpM" +
            "LVDRk+wKmEDPIxVhLNs+y33s3eHr20jvr5twmCSacX2klrFP2PqSTbloMHhWPziVqGp303+tzHYy7BF2vEeExoCg9ImiSOA2O3e10n3" +
            "SyffPf+PdCVxRkCW55TJwKXToumt3jnYjIhZidDB86K93GxV4QUGY4WK+Dy7yPmgaqFkslLc47jI7rBl4J9S+sjxMkg1hKVTox8wDBo" +
            "MO0wBhcGbbZTWOnDgkQjf8dznubONZpYARPXS36FSJvcsbkWpgl17ehsp86XwcljWkSz6VzrnabuhkQe3EPxISeqZcay8tHUHwSCmUM" +
            "sfhixvcXMI8JY4crjBwclrxsHbXn6e57TUXZI73gpprWQErN5y0i8E5kOwfzFzQd+uhnMsssv01hYMkuwNW0noVL9L6Q7nL/WG6QJH5" +
            "5N8gMHBMNlT4YJVZQ+Xbtsx3roQDAi5T5oZHkRh7w+qB/q95zj74sRbRzS1j0/B/aAAAAAQAFWC41MDkAAANCMIIDPjCCAvygAwIBAg" +
            "IEWCl21TALBgcqhkjOOAQDBQAwcTELMAkGA1UEBhMCQ0ExEDAOBgNVBAgTB09udGFyaW8xEDAOBgNVBAcTB1Rvcm9udG8xEDAOBgNVB" +
            "AoTB1Bpdm90YWwxEDAOBgNVBAsTB1Bpdm90YWwxGjAYBgNVBAMTEU1hcmsgSGV0aGVyaW5ndG9uMB4XDTE1MTIwODE5NTU0N1oXDTE2" +
            "MDMwNzE5NTU0N1owcTELMAkGA1UEBhMCQ0ExEDAOBgNVBAgTB09udGFyaW8xEDAOBgNVBAcTB1Rvcm9udG8xEDAOBgNVBAoTB1Bpdm9" +
            "0YWwxEDAOBgNVBAsTB1Bpdm90YWwxGjAYBgNVBAMTEU1hcmsgSGV0aGVyaW5ndG9uMIIBtzCCASwGByqGSM44BAEwggEfAoGBAP1/U4" +
            "EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1" +
            "/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+Gg" +
            "hdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnW" +
            "RbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYQAAoGAWXsJwzphSh2PqsVxF6aoZAP4ipcU2a" +
            "NSxP5bFu6XZ1GO7DqvQwUwxqMOJCuRK9fm19dGlYr65OPNDIIGNdzHnqAPufE0fgRTwKeUNPzy9I8iAbdxLB3t5sdnZZ0RwaLxdKiKy" +
            "ffEaVi7ZWGePkK0cua6igwIV+PXiz4Gjd+IgfajITAfMB0GA1UdDgQWBBS++X3+KLJIpg1Qoq2ap8j0LYUmITALBgcqhkjOOAQDBQAD" +
            "LwAwLAIUCQDPecfRs39I55BiS9Cr8zSxl34CFFZ0ULZbnJsn32SzNtZpAsO2T4Pnn0RreQyoHYWGkpaxrH09aaWjiOk=\",\n" +
            "            \"keystorePassword\": \"samplepassword\"\n" +
            "          },\n" +
            "          \"syslog_drain_url\": \"\"\n" +
            "        }\n" +
            "      ]\n" +
            "    }";

    public static CredentialsSource getCredentialsSourceWithSimpleValues(String name) throws IOException {
        return new CredentialsSource(name) {
            @Override
            protected String getVCAPServicesEnvValue() {
                return simpleValuesJson;
            }
        };
    }
    
    public static CredentialsSource getCredentialsSourceWithSimpleValuesDisabled(String name) throws IOException {
    	return new CredentialsSource(name) {
    		@Override
    		protected String getVCAPServicesEnvValue() {
    			return simpleValuesDisabledJson;
    		}
    	};
    }
    
    public static CredentialsSource getCredentialsSourceWithSimpleValuesNotDisabled(String name) throws IOException {
    	return new CredentialsSource(name) {
    		@Override
    		protected String getVCAPServicesEnvValue() {
    			return simpleValuesNotDisabledJson;
    		}
    	};
    }

    public static CredentialsSource givenValidCredentialsSource() throws IOException {
        return new CredentialsSource("PingCreds") {
            @Override
            protected String getVCAPServicesEnvValue() {
                return validCertStructureJson;
            }
        };
    }

    public static CredentialsSource givenCredentialsWithMissingBaseUri() throws IOException {
        return new CredentialsSource("PingCreds") {
            @Override
            protected String getVCAPServicesEnvValue() {
                return missingBaseUri;
            }
        };
    }

    public static CredentialsSource givenCredentialsWithOtherServices() throws IOException {
        return new CredentialsSource("PingCreds") {
            @Override
            protected String getVCAPServicesEnvValue() {
                return UPScontainingRabbitService;
            }
        };
    }

}
