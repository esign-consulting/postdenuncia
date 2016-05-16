package br.com.esign.postdenuncia.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.esign.postdenuncia.util.JsonUtil;
import org.apache.commons.lang.StringUtils;

/**
 * Generic servlet
 */
@SuppressWarnings("serial")
public class GenericServlet extends HttpServlet {

    protected Logger logger = LogManager.getLogger(getClass());

    /**
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void logParameters(HttpServletRequest request) {
        logger.debug(request.getRequestURI());
        Enumeration<String> enumeration = request.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String parameterName = enumeration.nextElement();
            String parameter = request.getParameter(parameterName);
            if (!StringUtils.isBlank(parameter)) {
                logger.debug(parameterName + ": " + parameter);
            }
        }
    }

    protected String decode(String encoded) throws UnsupportedEncodingException {
        return (encoded == null) ? null : URLDecoder.decode(encoded, "utf-8");
    }

    protected String getValueAsString(Part part) throws IOException {
        if (part == null) {
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(part.getInputStream(), "utf-8"));
        StringBuilder value = new StringBuilder();
        char[] buffer = new char[1024];
        for (int length = 0; (length = reader.read(buffer)) > 0;) {
            value.append(buffer, 0, length);
        }
        return value.toString();
    }

    protected byte[] getValueAsBytes(Part part) throws IOException {
        byte[] bytes;
        try (InputStream stream = part.getInputStream()) {
            bytes = IOUtils.toByteArray(stream);
        }
        return bytes;
    }

    protected void textResponse(HttpServletResponse response, String text) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        response.setHeader("Cache-Control", "no-cache");
        response.getWriter().write(text);
    }

    protected void jsonResponse(HttpServletResponse response, Object obj) throws IOException {
        jsonResponse(response, obj, false);
    }

    protected void jsonResponse(HttpServletResponse response, Object obj, boolean forceLazyLoading) throws IOException {
        String json = JsonUtil.getObjectMapper(forceLazyLoading).writeValueAsString(obj);
        jsonResponse(response, json);
    }

    protected void jsonResponse(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        OutputStream out = response.getOutputStream();
        out.write(json.getBytes("utf-8"));
        out.flush();
    }

    protected void jpgResponse(HttpServletResponse response, String filename) throws IOException {
        File file = new File(filename);
        response.setContentType("image/jpg");
        response.setContentLength((int) file.length());
        OutputStream out;
        try (InputStream in = new FileInputStream(file)) {
            out = response.getOutputStream();
            IOUtils.copy(in, out);
        }
        out.flush();
    }

    public class Response<T> {

        private List<Error> error;
        private List<T> success;
        private List<Result> result;

        public Response() {
        }

        public Response(Exception e) {
            addException(e);
        }

        public Response(Error e) {
            addError(e);
        }

        public Response(T e) {
            addEntity(e);
        }

        public Response(List<T> success) {
            setSuccess(success);
        }

        public final void addException(Exception e) {
            addError(new Error(e));
        }

        public final void addError(Error e) {
            setError(new ArrayList<>());
            error.add(e);
        }

        public final void addEntity(T e) {
            setSuccess(new ArrayList<>());
            success.add(e);
        }

        public List<Error> getError() {
            return error;
        }

        public void setError(List<Error> error) {
            this.error = error;
            setResultStatus("error");
        }

        public List<T> getSuccess() {
            return success;
        }

        public final void setSuccess(List<T> success) {
            this.success = success;
            setResultStatus("success");
        }

        public List<Result> getResult() {
            return result;
        }

        public void setResult(List<Result> result) {
            this.result = result;
        }

        private void setResultStatus(String status) {
            result = new ArrayList<>();
            result.add(new Result(status));
        }

        public class Result {

            private String status;

            public Result(String status) {
                this.status = status;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

        }

    }

    public class Error {

        private String message;
        private String stacktrace;

        public Error(Exception e) {
            this.message = e.getMessage();
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            this.stacktrace = writer.toString();
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getStacktrace() {
            return stacktrace;
        }

        public void setStacktrace(String stacktrace) {
            this.stacktrace = stacktrace;
        }

    }

}