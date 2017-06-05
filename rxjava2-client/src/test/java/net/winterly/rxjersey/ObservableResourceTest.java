package net.winterly.rxjersey;

import io.reactivex.Observable;
import org.junit.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;

import static org.junit.Assert.assertEquals;

public class ObservableResourceTest extends RxJerseyTest {

    @Override
    protected Application configure() {
        return config().register(ServerResource.class);
    }

    @Test
    public void shouldReturnContent() {
        Resource resource = resource(Resource.class);
        String message = resource.echo("hello").blockingFirst();

        assertEquals("hello", message);
    }

    @Test
    public void shouldReturnNoContentOnNull() {
        Resource resource = resource(Resource.class);
        String message = resource.empty().blockingFirst();

        assertEquals("", message);
    }

    @Test(expected = BadRequestException.class)
    public void shouldHandleError() {
        Resource resource = resource(Resource.class);
        String message = resource.error().blockingFirst();

        assertEquals("", message);
    }

    @Path("/observable")
    public interface Resource {

        @GET
        @Path("echo")
        Observable<String> echo(@QueryParam("message") String message);

        @GET
        @Path("empty")
        Observable<String> empty();

        @GET
        @Path("error")
        Observable<String> error();

    }

    @Path("/observable")
    public static class ServerResource {

        @GET
        @Path("echo")
        public String echo(@QueryParam("message") String message) {
            return message;
        }

        @GET
        @Path("empty")
        public String empty() {
            return null;
        }

        @GET
        @Path("error")
        public String error() {
            throw new BadRequestException();
        }

    }
}