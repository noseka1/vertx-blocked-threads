import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.logging.SLF4JLogDelegateFactory;

public class VertxBlockedThreads {

	public static void main(String[] args) {
	    System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory.class.getName());
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new FibonacciServer());
	}
}

class FibonacciServer extends AbstractVerticle {

	@Override
	public void start(Future<Void> fut) {
		vertx.createHttpServer().requestHandler(new FibonacciHandler()).listen(8080, result -> {
			if (result.succeeded()) {
				fut.complete();
			} else {
				fut.fail(result.cause());
			}
		});
	}
}

class FibonacciHandler implements Handler<HttpServerRequest> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FibonacciHandler.class);
	private static final int n = 20;

	@Override
	public void handle(HttpServerRequest event) {
		long startTime = System.nanoTime();
		long result = fibonacci(n);
		event.response().end(Long.toString(result));
		long endTime = System.nanoTime();
		LOGGER.info("Processed in " + ((endTime - startTime) / 1_000) + " us");
	}

	long fibonacci(int n) {
		if (n <= 1) {
			return n;
		}
		return fibonacci(n - 1) + fibonacci(n - 2);
	}

}