package producer;

public interface IProducer<E> extends Runnable {

	void send(E element);
}
