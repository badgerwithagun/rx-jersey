package net.winterly.rxjersey.client.inject;

import net.winterly.rxjersey.client.ClientMethodInvoker;
import net.winterly.rxjersey.client.WebResourceFactory;
import org.glassfish.jersey.internal.inject.Injectee;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.InjectionResolver;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Type;
import java.net.URI;

import static java.lang.String.format;

/**
 * Injection resolver for RxWebTarget and Proxy client instances based on value of {@link Remote} annotation
 *
 * @see Remote
 */
@Singleton
public class RemoteResolver implements InjectionResolver<Remote> {

    public static final String RX_JERSEY_CLIENT_NAME = "net.winterly.rxjersey.client.inject.RxJerseyClient";

    @Inject
    private InjectionManager injectionManager;

    @Inject
    private Provider<ClientMethodInvoker> clientMethodInvoker;

    @Inject
    @Named(RX_JERSEY_CLIENT_NAME)
    private Provider<Client> client;

    private static URI merge(String value, UriInfo uriInfo) {
        URI target = URI.create(value);
        if (target.getHost() == null) {
            target = UriBuilder.fromUri(uriInfo.getBaseUri()).uri(target).build();
        }

        return target;
    }

    /**
     * @return RxWebTarget or Proxy client of specified injectee interface
     * @throws IllegalStateException if uri is not correct or there is no sufficient injection resolved
     */
    @Override
    public Object resolve(Injectee injectee) {
        Remote remote = injectee.getParent().getAnnotation(Remote.class);
        UriInfo uriInfo = injectionManager.getInstance(UriInfo.class);

        URI target = merge(remote.value(), uriInfo);
        WebTarget webTarget = client.get().target(target);
        Type type = injectee.getRequiredType();

        if (type instanceof Class) {
            Class<?> required = (Class) type;

            if (WebTarget.class.isAssignableFrom(required)) {
                return webTarget;
            }

            if (required.isInterface()) {
                return WebResourceFactory.newResource(required, webTarget, clientMethodInvoker.get());
            }
        }

        throw new IllegalStateException(format("Can't find proper injection for %s", type));
    }

    @Override
    public boolean isConstructorParameterIndicator() {
        return true;
    }

    @Override
    public boolean isMethodParameterIndicator() {
        return false;
    }

    @Override
    public Class<Remote> getAnnotation() {
        return Remote.class;
    }
}
