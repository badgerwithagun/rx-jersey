import io.reactivex.Maybe;
import org.junit.Test;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import static org.junit.Assert.assertEquals;

public class MaybeResourceTest extends RxJerseyTest {

    @Test
    public void shouldReturnContent() {
        Resource resource = target(Resource.class);
        String message = resource.echo("hello").blockingGet();

        assertEquals("hello", message);
    }

    @Test
    public void shouldReturnNoContentOnNull() {
        Resource resource = target(Resource.class);
        String message = resource.empty().blockingGet();

        assertEquals("", message);
    }

    @Test(expected = BadRequestException.class)
    public void shouldHandleError() {
        Resource resource = target(Resource.class);
        String message = resource.error().blockingGet();

        assertEquals("", message);
    }

    @Path("/endpoint")
    public interface Resource {

        @GET
        @Path("echo")
        Maybe<String> echo(@QueryParam("message") String message);

        @GET
        @Path("empty")
        Maybe<String> empty();

        @GET
        @Path("error")
        Maybe<String> error();
    }
}
