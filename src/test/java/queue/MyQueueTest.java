package queue;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import consumer.IConsumer;
import consumer.MyConsumer;
import producer.IProducer;
import producer.MyProducer;

class MyQueueTest {

	private MyQueue<Integer> queue;
	private final int CATACITY = 10;
	private final int WAITINGTIME = 1000;

	@BeforeEach
	public void beforeEach() {
		queue = new MyQueue<>(CATACITY, WAITINGTIME);
	}

	@Test
	@DisplayName("Push Element in Queue")
	public void testPushOne() {
		// given
		int num = 1;

		// when
		queue.push(num);

		//then
		assertThat(queue.size()).isEqualTo(1);
	}

	@Test
	@DisplayName("Push one to ten in Queue")
	public void testPushOneToTen() {
		// given
		int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

		// when
		Arrays.stream(nums).forEach(queue::push);

		// then
		assertThat(queue.size()).isEqualTo(10);
	}

	@Test
	@DisplayName("Pop element in Queue")
	public void testPopElement() {
		// given
		int num = 1;
		queue.push(num);

		// when
		int popedNum;
		try {
			popedNum = queue.pop();
		} catch (TimeoutException e) {
			throw new RuntimeException(e);
		}

		// then
		assertThat(popedNum).isEqualTo(num);
		assertThat(queue.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Check that elements exists in the queue")
	public void testCheck() {
		// given
		int num = 1;
		queue.push(num);

		// when
		boolean check = queue.check();

		// then
		assertThat(check).isTrue();
	}


	@Test
	@DisplayName("Get waiting State when pushing for full queue with capacity(10)")
	public void testGetWaitingStateOverTheCapacity() throws InterruptedException {
		// given
		int[] nums = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
		Thread thread = new Thread(() -> {
			Arrays.stream(nums).forEach(n -> {
				try {
					queue.push(n);
				} catch (RuntimeException e) {
					System.out.println(e.getMessage());
				}
			});
		});

		// when
		thread.start();
		Thread.sleep(500);
		thread.interrupt();

		// then
		assertThat(thread.isInterrupted()).isTrue();
		assertThat(queue.size()).isEqualTo(CATACITY);
	}

	@Test
	@DisplayName("Get waiting state when consuming from empty queue")
	public void testGetWaitingStateEmptyTheCapacity() throws InterruptedException {
		// given
		Thread thread = new Thread(() -> {
			try {
				queue.pop();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		});

		// when
		thread.start();
		Thread.sleep(WAITINGTIME - 500);
		thread.interrupt();

		// then
		assertThat(thread.isInterrupted()).isTrue();
		assertThat(queue.size()).isEqualTo(0);
	}

	@Test
	@DisplayName("Thread terminates after 3 seconds in waiting state")
	public void testTerminateAfterThreeSeconds() throws InterruptedException {
		// given
		Thread thread = new Thread(() -> {
			try {
				queue.pop();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		});

		// when
		thread.start();
		Thread.sleep(WAITINGTIME + 1000);
		thread.interrupt();

		// then
		assertThat(thread.isInterrupted()).isFalse();
	}

	@Test
	@DisplayName("Although Producer sends element continuously, Consumer pops all element")
	public void testConsumerPopAllElement() throws InterruptedException {
		// given
		IProducer<Integer> producer = new MyProducer<>(queue);
		IConsumer<Integer> consumer = new MyConsumer<>(queue);

		Thread producerThread = new Thread(producer) {
			@Override
			public void run() {
				for (int i = 0; i < 15; i++) {
					producer.send(i);
				}
			}
		};
		Thread consumerThread = new Thread(consumer);

		// when
		producerThread.start();
		consumerThread.start();

		Thread.sleep(WAITINGTIME + 1000);

		// then
		assertThat(queue.size()).isEqualTo(0);
	}
}