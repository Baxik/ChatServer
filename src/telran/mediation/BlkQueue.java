package telran.mediation;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlkQueue<T> implements IBlkQueue<T> {
	private Lock mutex = new ReentrantLock();
	private Condition senderWaitingCondition = mutex.newCondition();
	private Condition receiverWaitingCondition = mutex.newCondition();
	private LinkedList<T> queue = new LinkedList<>();
	private int maxSize;

	public BlkQueue(int maxSize) {
		this.maxSize = maxSize;
	}

	@Override
	public void push(T message) {
		mutex.lock();
		try {
			while (queue.size() >= maxSize) {
				try {
					senderWaitingCondition.await();
				} catch (InterruptedException e) {
					System.out.println("thread was interrupted");
				}
			}
			queue.add(message);
			receiverWaitingCondition.signal();
		} finally {
			mutex.unlock();
		}
	}

	@Override
	public T pop() {
		mutex.lock();
		try {
			while (queue.isEmpty()) {
				try {
					receiverWaitingCondition.await();
				} catch (InterruptedException e) {
					System.out.println("thread was interrupted");
				}
			}
			T msg = queue.poll();
			senderWaitingCondition.signal();
			return msg;
		} finally {
			mutex.unlock();
		}
	}
}