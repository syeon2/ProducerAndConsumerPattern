package producer;

import java.util.ArrayList;
import java.util.List;

import queue.IQueue;

public class MyProducer<E> extends Thread implements IProducer<E> {

	private final IQueue<E> queue;
	private List<E> list = new ArrayList<>();

	public MyProducer(IQueue<E> queue) {
		this.queue = queue;
	}

	public MyProducer(IQueue<E> queue, List<E> list) {
		this.queue = queue;
		this.list = list;
	}

	@Override
	public void send(E element) {
		queue.push(element);
	}

	@Override
	public void run() {
		list.forEach(queue::push);
	}
}
