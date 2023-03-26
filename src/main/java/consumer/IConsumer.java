package consumer;

import java.util.concurrent.TimeoutException;

public interface IConsumer<E> extends Runnable {

	void process() throws TimeoutException;
}
