package queue;

import java.util.concurrent.TimeoutException;

public interface IQueue<E> {

	void push(E element);

	E pop() throws TimeoutException;

	boolean check();
}
