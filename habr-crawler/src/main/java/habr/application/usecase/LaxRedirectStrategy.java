package habr.application.usecase;

import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class LaxRedirectStrategy implements RedirectStrategy {

    @Override
    public boolean isRedirected(org.apache.http.HttpRequest httpRequest, org.apache.http.HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException {
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        return statusCode == 301 || statusCode == 302 ||
                statusCode == 307 || statusCode == 308;
    }

    @Override
    public HttpUriRequest getRedirect(org.apache.http.HttpRequest httpRequest, org.apache.http.HttpResponse httpResponse, HttpContext httpContext) throws ProtocolException {
        String location = httpResponse.getFirstHeader("Location").getValue();
        // Создаем новый запрос с оригинальным методом
        return new HttpGet(location); // Всегда используем GET для редиректов
        }
}