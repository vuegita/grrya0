package com.inso.framework.queue;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.inso.framework.utils.ThreadUtils;

/**
 * write blocking, read unblocking
 * @author Administrator
 *
 * @param <E>
 */
public class WriteBlockingQueue<E> implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	
	private final AtomicInteger count = new AtomicInteger();
	private final ReentrantLock putLock = new ReentrantLock();
	private final Condition notFull = putLock.newCondition();
	
	private int capacity;
	private ConcurrentLinkedQueue<E> concurrentQueue;
	
	public WriteBlockingQueue(int capacity)
	{
		this.capacity = capacity;
		this.concurrentQueue = new ConcurrentLinkedQueue<E>();
	}
	
	public void put(E e) throws InterruptedException
	{
		putLock.lockInterruptibly();
		try{
			int c = -1;
			while (count.get() == capacity) {
                notFull.await();
            }
			concurrentQueue.add(e);
			count.incrementAndGet();
			if (c + 1 < capacity)
                notFull.signal();
		} finally {
			putLock.unlock();
		}
	}
	
	private void signal()
	{
		putLock.lock();
		try{
			notFull.signal();
		}finally
		{
			putLock.unlock();
		}
	}
	
	public E take()
	{
		E e = concurrentQueue.poll();
		if(e != null) {
			count.decrementAndGet();
			signal();
		}
		return e;
	}
	
	public int getSize() {
		return count.get();
	}
	
	public boolean isEmpty()
	{
		return count.get() == 0;
	}

	public static void main(String[] args) throws IOException
	{
		WriteBlockingQueue<String> writeQueue = new WriteBlockingQueue<String>(5);
		boolean start = true;
		for(int i = 0; i < 10; i ++)
		{
			Thread thread = new Thread(new Runnable() {
				
				public void run() {
					try {
						while(start)
						{
							writeQueue.put("test");
							ThreadUtils.sleep(1000);
							System.out.println("put data ");
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			thread.start();
		}
		
		while(start)
		{
			System.out.println("value = " + writeQueue.take() + ", size " + writeQueue.getSize());
			ThreadUtils.sleep(2000);
		}
		
		System.in.read();
	}
	
}
