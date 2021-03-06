/**
 * Copyright 2016-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazon.pay.impl;

import com.amazon.pay.Config;
import com.amazon.pay.response.model.Environment;
import com.amazon.pay.response.parser.ResponseData;
import com.amazon.pay.types.Region;
import com.amazon.pay.types.ServiceConstants;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.sun.org.apache.xpath.internal.SourceTree;
import org.apache.commons.codec.binary.Base64;

public class Util {

    private static PayLogUtil payLogUtil = new PayLogUtil();

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String JAVA_VERSION = System.getProperty("java.version");
    public static final String OS_NAME = System.getProperty("os.name");
    public static final String OS_VERSION = System.getProperty("os.version");

    /**
     * Helper method to calculate base64 encoded signature using specified secret key
     *
     */
    public static String getSignature(String stringToSign, String secretKey) throws IllegalStateException, InvalidKeyException, NoSuchAlgorithmException, UnsupportedEncodingException {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256"));
        byte[] signature = mac.doFinal(stringToSign.getBytes("UTF-8"));
        String signatureBase64 = new String(Base64.encodeBase64(signature), "UTF-8");
        return signatureBase64;
    }

    public static String getTimestamp() {
        final Date date = new Date();
        final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ssz";
        final SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
        final TimeZone utc = TimeZone.getTimeZone("UTC");
        sdf.setTimeZone(utc);
        String timeStamp = sdf.format(date);
        return timeStamp.replace("UTC", "Z");
    }

    /**
     * This method uses HttpURLConnection instance to make requests.
     *
     * @param method The HTTP method (GET,POST,PUT,etc.).
     * @param url The URL
     * @param urlParameters URL Parameters
     * @param headers Header key-value pairs
     * @return ResponseData
     * @throws IOException
     */
    public static ResponseData httpSendRequest(String method, String url, String urlParameters, Map<String,String> headers) throws IOException {

        payLogUtil.logMessage("Request:\nURL=" + url + "\nPOST Data=" + urlParameters);

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        if (headers != null && !headers.isEmpty()) {
            for (String key : headers.keySet()) {
                con.setRequestProperty(key, headers.get(key));
            }
        }
        con.setDoOutput(true);
        con.setRequestMethod(method);
        if (urlParameters != null) {
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
        }
        int responseCode = con.getResponseCode();

        BufferedReader in;
        if (responseCode != 200) {
            in = new BufferedReader(new InputStreamReader(con.getErrorStream(), "UTF-8"));
        } else {
            in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        }
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine).append(LINE_SEPARATOR);
        }
        in.close();
        return new ResponseData(responseCode, response.toString());
    }


    /**
     * This method uses PayConfig to set proxy settings and uses
     * HttpURLConnection instance to make requests.
     *
     * @param method The HTTP method (GET,POST,PUT,etc.).
     * @param url The URL
     * @param urlParameters URL Parameters
     * @param config client configuration container
     * @return ResponseData
     * @throws IOException
     */
    public static ResponseData httpSendRequest(String method, String url, String urlParameters, Map<String,String> headers, PayConfig config) throws IOException {

        Map<String,String> headerMap = new HashMap<String,String>();

        if (config != null) {

            final String applicationName = config.getApplicationName();
            final String applicationVersion = config.getApplicationVersion();
            StringBuilder userAgent = new StringBuilder(ServiceConstants.GITHUB_SDK_NAME + "/" + ServiceConstants.APPLICATION_LIBRARY_VERSION);

            if ((applicationName != null && !applicationName.trim().isEmpty()) && (applicationVersion != null && !applicationVersion.trim().isEmpty())) {
                userAgent.append(" (" + applicationName + "/" + applicationVersion + "; ");
            } else if (applicationVersion != null && !applicationVersion.trim().isEmpty()) {
                userAgent.append(" (" + applicationVersion + "; ");
            } else if (applicationName != null && !applicationName.trim().isEmpty()) {
                userAgent.append(" (" + applicationName + "; ");
            } else {
                userAgent.append(" (");
            }

            userAgent.append("Java/" + JAVA_VERSION + "; " + OS_NAME + "/" + OS_VERSION + ")");
            headerMap.put("User-Agent", userAgent.toString());

            if (config.getProxyHost() != null) {
                Properties systemSettings = System.getProperties();
                systemSettings.put("proxySet", "true");
                systemSettings.put("http.proxyHost", config.getProxyHost());
                systemSettings.put("http.proxyPort", config.getProxyPort());
                if (config.getProxyUsername() != null && config.getProxyPassword() != null) {
                    String password = config.getProxyUsername() + ":" + config.getProxyPassword();
                    byte[] encodedPassword = Base64.encodeBase64(password.getBytes());
                    if (encodedPassword != null) {
                        headerMap.put("Proxy-Authorization", new String(encodedPassword));
                    }
                }
            }
        }

        ResponseData response = Util.httpSendRequest(method, url, urlParameters, headerMap);
        return response;
    }

    /**
     * Performs additional processing on top of the URLEncoder.encode function to
     * make the string encoding conform to RFC3986
     * @throws java.io.UnsupportedEncodingException
     */
    public static String urlEncode(String str) throws UnsupportedEncodingException {
        String val = (str == null) ? "" : str;
        String encoded = URLEncoder.encode(val, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
        return encoded;
    }

    /**
     * Helper method to URL encode all parameter values in a Map
     * @throws java.io.UnsupportedEncodingException
     */
    public static void urlEncodeAPIParams(Map<String, String> apiParameters) throws UnsupportedEncodingException {
        for (Map.Entry<String, String> entry : apiParameters.entrySet()) {
            entry.setValue(urlEncode(entry.getValue()));
        }
    }

    /**
     * Helper method to convert JSON data to Object specified using GSON
     *
     */
    public static <T> T convertJsonToObject(String jsonData, Class<T> clazz) {
        Gson gson = new Gson();
        T object =  gson.fromJson(jsonData, clazz);
        return object;
    }

    /**
     * Helper method to convert specified parameter map to URL string
     * separated by ampersand
     *
     */
    public static String convertParameterMapToString(Map<String, String> params) {
        StringBuilder parameterString = new StringBuilder();
        Iterator<Map.Entry<String, String>> pairs = params.entrySet().iterator();
        while (pairs.hasNext()) {
            Map.Entry<String, String> pair = pairs.next();
            if (pair.getValue() != null) {
                parameterString.append(pair.getKey() + "=" + pair.getValue());
            } else {
                parameterString.append(pair.getKey() + "=");
            }
            if (pairs.hasNext()) {
                parameterString.append("&");
            }
        }
        return parameterString.toString();
    }

    /**
     * Helper method to get Service URL endpoint including service version name
     * @deprecated This method does not handle Service URL overrides.
     *             Please use getServiceURLEndpoint(Config config) method instead.
     */
    @Deprecated
    public static String getServiceURLEndpoint(Region region, Environment environment) {
        return ServiceConstants.mwsEndpointMappings.get(region) + getServiceVersionName(environment);
    }

    /**
     * Helper method to get Service URL endpoint including service version name
     */
    public static String getServiceURLEndpoint(Config config) {
        if (config.getOverrideServiceURL() != null) {
            return config.getOverrideServiceURL()
                    + getServiceVersionName(config.getEnvironment());
        } else {
            return ServiceConstants.mwsEndpointMappings.get(config.getRegion())
                    + getServiceVersionName(config.getEnvironment());
        }
    }


    public static String getServiceVersionName(Environment environment) {
        String mwsServiceAPIVersionName;
        if (environment == Environment.SANDBOX) {
            mwsServiceAPIVersionName = "/" + "OffAmazonPayments_Sandbox" + "/" + ServiceConstants.AMAZON_PAY_API_VERSION;
        }
        else {
            mwsServiceAPIVersionName = "/" + "OffAmazonPayments" + "/" + ServiceConstants.AMAZON_PAY_API_VERSION;
        }
        return mwsServiceAPIVersionName;
    }

}
