package queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class MyQueue<E> implements IQueue<E> {

	private final List<E> list = new ArrayList<>();

	private int capacity = 10;
	private volatile AtomicInteger count = new AtomicInteger(0);
	private int timeOutSeconds = 3000;

	public MyQueue(int capacity, int timeOutSeconds) {
		this.capacity = capacity;
		this.timeOutSeconds = timeOutSeconds;
	}

	@Override
	public void push(E element) {
		synchronized (this) {
			if (count.get() >= capacity) {
				try {
					wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			list.add(element);
			if (count.incrementAndGet() == 1) notify();
		}
	}

	@Override
	public E pop() throws TimeoutException {
		synchronized (this) {
			if (count.get() <= 0) {
				try {
					wait(timeOutSeconds);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			if (count.get() == 0) throw new TimeoutException("Queue is Empty");

			E removedData = list.remove(0);
			if (count.decrementAndGet() < capacity) notify();
			return removedData;
		}
	}

	@Override
	public boolean check() {
		synchronized (this) {
			if (count.get() > 0) {
				System.out.println(Thread.currentThread().getName() + " Queue has Element");
				return true;
			}

			System.out.println(Thread.currentThread().getName() + " Queue is Empty");
			return false;
		}
	}

	public int size() {
		return list.size();
	}

	@Override
	public String toString() {
		return "MyQueue{" +
			"list=" + list +
			'}';
	}
}
