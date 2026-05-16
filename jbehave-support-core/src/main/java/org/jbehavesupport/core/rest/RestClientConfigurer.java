package org.jbehavesupport.core.rest;

import java.util.Map;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestClient;

/**
 * This class provides convenient api for customization of {@link RestClient},
 * it is used by {@link RestServiceHandler}.
 * <p>
 * Example:
 * <pre>
 * public class MyAppRestServiceHandler extends RestServiceHandler {
 *
 *     &#064;Override
 *     protected void initRestClient(RestClientConfigurer restClientConfigurer) {
 *         restClientConfigurer
 *             .basicAuthorization(myUsername, myPassword)
 *             .header(this::headers);
 *     }
 *
 * }
 * </pre>
 */
@RequiredArgsConstructor
public class RestClientConfigurer {

    private final RestClient.Builder builder;

    public final RestClientConfigurer interceptor(ClientHttpRequestInterceptor interceptor) {
        builder.requestInterceptors(list -> list.add(interceptor));
        return this;
    }

    public final RestClientConfigurer header(Supplier<Map<String, String>> headerProvider) {
        return interceptor((req, body, execution) -> {
            headerProvider.get().forEach((k, v) -> req.getHeaders().add(k, v));
            return execution.execute(req, body);
        });
    }

    public final RestClientConfigurer basicAuthorization(String username, String password) {
        return interceptor(new BasicAuthenticationInterceptor(username, password));
    }
}
