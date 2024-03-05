package com.frank.threadpool.monitor.agent.beat.client;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author francis
 * @version 2021-07-20
 */
public class SimpleHttpClient {

    /**
     * Execute a GET HTTP request.
     *
     * @param request HTTP request
     * @return the response if the request is successful
     * @throws IOException when connection cannot be established or the connection is interrupted
     */
    public SimpleHttpResponse get(SimpleHttpRequest request) throws IOException {
        if (request == null) {
            return null;
        }
        return request(request.getSocketAddress(),
                RequestMethod.GET, request.getRequestPath(), request.getParams(),
                request.getCharset(), request.getSoTimeout());
    }

    /**
     * Execute a POST HTTP request.
     *
     * @param request HTTP request
     * @return the response if the request is successful
     * @throws IOException when connection cannot be established or the connection is interrupted
     */
    public SimpleHttpResponse post(SimpleHttpRequest request) throws IOException {
        if (request == null) {
            return null;
        }
        return request(request.getSocketAddress(),
                RequestMethod.POST, request.getRequestPath(),
                request.getParams(), request.getCharset(),
                request.getSoTimeout());
    }

    private SimpleHttpResponse request(InetSocketAddress socketAddress,
                                       RequestMethod type, String requestPath,
                                       Map<String, String> paramsMap, Charset charset, int soTimeout) {
        Socket socket = null;
        BufferedWriter writer;
        try {
            socket = new Socket();
            socket.setSoTimeout(soTimeout);
            socket.connect(socketAddress, soTimeout);

            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), charset));
            requestPath = getRequestPath(type, requestPath, paramsMap, charset);
            writer.write(getStatusLine(type, requestPath) + "\r\n");
            writer.write("Content-Type: application/x-www-form-urlencoded; charset=" + charset.name() + "\r\n");
            writer.write("Host: " + socketAddress.getHostName() + "\r\n");
            if (type == RequestMethod.GET) {
                writer.write("Content-Length: 0\r\n");
                writer.write("\r\n");
            } else {
                // POST method.
                String params = encodeRequestParams(paramsMap, charset);
                writer.write("Content-Length: " + params.getBytes(charset).length + "\r\n");
                writer.write("\r\n");
                writer.write(params);
            }
            writer.flush();
//            SimpleHttpResponse response = new SimpleHttpResponseParser().parse(socket.getInputStream());
            socket.close();
            socket = null;
        } catch (Exception ignore) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception ex) {
                    System.out.println("Error when closing " + type + " request to " + socketAddress + " " + ex.getMessage());
                }
            }
        }
        return null;
    }

    private String getRequestPath(RequestMethod type, String requestPath,
                                  Map<String, String> paramsMap, Charset charset) {
        if (type == RequestMethod.GET) {
            if (requestPath.contains("?")) {
                return requestPath + "&" + encodeRequestParams(paramsMap, charset);
            }
            return requestPath + "?" + encodeRequestParams(paramsMap, charset);
        }
        return requestPath;
    }

    private String getStatusLine(RequestMethod type, String requestPath) {
        if (type == RequestMethod.POST) {
            return "POST " + requestPath + " HTTP/1.1";
        }
        return "GET " + requestPath + " HTTP/1.1";
    }

    /**
     * Encode and get the URL request parameters.
     *
     * @param paramsMap pair of parameters
     * @param charset   charset
     * @return encoded request parameters, or empty string ("") if no parameters are provided
     */
    private String encodeRequestParams(Map<String, String> paramsMap, Charset charset) {
        if (paramsMap == null || paramsMap.isEmpty()) {
            return "";
        }
        try {
            StringBuilder paramsBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                paramsBuilder.append(URLEncoder.encode(entry.getKey(), charset.name()))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), charset.name()))
                        .append("&");
            }
            if (paramsBuilder.length() > 0) {
                // Remove the last '&'.
                paramsBuilder.delete(paramsBuilder.length() - 1, paramsBuilder.length());
            }
            return paramsBuilder.toString();
        } catch (Throwable e) {
            System.out.println("Encode request params fail " + e.getMessage());
            return "";
        }
    }

    private enum RequestMethod {
        /**
         * http get method
         */
        GET,
        /**
         * http post method
         */
        POST
    }


}
