package consumer;

import java.util.concurrent.TimeoutException;

import queue.IQueue;

public class MyConsumer<E> extends Thread implements IConsumer<E> {

	private final IQueue<E> queue;

	public MyConsumer(IQueue<E> queue) {
		this.queue = queue;
	}

	@Override
	public void process() throws TimeoutException {
		E poppedData = queue.pop();
		System.out.println("popped element = " + poppedData);
	}

	@Override
	public void run() {
		while (true) {
			try {
				this.process();
			} catch (TimeoutException e) {
				System.out.println(e.getMessage());
				return;
			}
		}
	}
}
