package br.com.esign.postdenuncia.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.IOUtils;

public class HtmlUtil {

    private final String DEFAULT_CHARSET_NAME = "ISO-8859-1";

    public MethodResult post(String spec, String params) throws IOException {
        return post(spec, params, null);
    }

    public MethodResult post(String spec, String params, List<String> cookies) throws IOException {
        return post(spec, params, cookies, DEFAULT_CHARSET_NAME);
    }

    public MethodResult post(String spec, String params, List<String> cookies, String charsetName) throws IOException {
        return post(spec, params, cookies, null, charsetName);
    }

    public MethodResult post(String spec, String params, List<String> cookies, Map<String, String> headers) throws IOException {
        return post(spec, params, cookies, headers, DEFAULT_CHARSET_NAME);
    }

    public MethodResult post(String spec, String params, List<String> cookies, Map<String, String> headers, String charsetName) throws IOException {
        URL url = new URL(spec);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        setCookies(conn, cookies);
        setHeaders(conn, headers);
        setParams(conn, params);
        return new MethodResult(conn, charsetName);
    }

    public MethodResult get(String spec, List<String> cookies) throws IOException {
        return get(spec, cookies, DEFAULT_CHARSET_NAME);
    }

    public MethodResult get(String spec, List<String> cookies, String charsetName) throws IOException {
        return get(spec, cookies, null, charsetName);
    }

    public MethodResult get(String spec, List<String> cookies, Map<String, String> headers, String charsetName) throws IOException {
        URL url = new URL(spec);
        URLConnection conn = url.openConnection();
        setCookies(conn, cookies);
        setHeaders(conn, headers);
        return new MethodResult(conn, charsetName);
    }

    public void setCookies(URLConnection conn, List<String> cookies) {
        if (cookies != null && !cookies.isEmpty()) {
            for (String cookie : cookies) {
                String value = cookie.split(";", 2)[0];
                conn.addRequestProperty("Cookie", value);
            }
        }
    }

    public void setHeaders(URLConnection conn, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.addRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    public void setParams(HttpURLConnection conn, String params) throws IOException {
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes(params);
        out.flush();
        out.close();
    }

    public class MethodResult {

        private List<String> cookiesResult;
        private String htmlResult;

        public MethodResult(URLConnection conn) throws IOException {
            this(conn, "ISO-8859-1");
        }

        public MethodResult(URLConnection conn, String charsetName) throws IOException {
            Map<String, List<String>> headerFields = conn.getHeaderFields();
            cookiesResult = headerFields.get("Set-Cookie");

            InputStream in = conn.getInputStream();
            List<String> contentEncoding = headerFields.get("Content-Encoding");
            if (contentEncoding != null && !contentEncoding.isEmpty() && "gzip".equalsIgnoreCase(contentEncoding.get(0))) {
                in = new GZIPInputStream(in);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, charsetName));
            htmlResult = IOUtils.toString(reader);
        }

        public List<String> getCookiesResult() {
            return cookiesResult;
        }

        public String getHtmlResult() {
            return htmlResult;
        }

    }

}