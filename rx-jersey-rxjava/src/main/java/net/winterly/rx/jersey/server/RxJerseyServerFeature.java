package net.winterly.rx.jersey.server;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.spi.internal.ResourceMethodDispatcher;
import org.glassfish.jersey.server.spi.internal.ResourceMethodInvocationHandlerProvider;

import javax.inject.Singleton;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

/**
 * Feature implementation to configure RxJava support for resources <br>
 * Registers {@link RxBodyWriter} and {@link RxInvocationHandlerProvider}
 */
public final class RxJerseyServerFeature implements Feature {

    @Override
    public boolean configure(FeatureContext context) {
        context.register(RxBodyWriter.class);
        context.register(new Binder());
        return true;
    }

    private static class Binder extends AbstractBinder {

        @Override
        protected void configure() {
            bind(RxInvocationHandlerProvider.class)
                    .to(ResourceMethodInvocationHandlerProvider.class)
                    .in(Singleton.class);

            bind(SingleMethodDispatcher.Provider.class)
                    .to(ResourceMethodDispatcher.Provider.class)
                    .in(Singleton.class)
                    .ranked(1);
        }
    }
}