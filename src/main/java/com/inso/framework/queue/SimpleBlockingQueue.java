package com.inso.framework.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.inso.framework.service.Callback;

public class SimpleBlockingQueue<T> {

	private BlockingQueue<T> queue;
	
	private int capacity;
	private AtomicInteger  currentIndex = new AtomicInteger(0);
	
	private boolean isStart = true;
	
	public SimpleBlockingQueue(int capacity)
	{
		this.capacity = capacity;
		this.queue = new LinkedBlockingQueue<>(capacity);
	}
	
	public void add(T t)
	{
		try {
			queue.put(t);
			currentIndex.incrementAndGet();
		} catch (Exception e) {
		}
	}
	
	public void onCallback(Callback<T> callback)
	{
		Thread thread = new Thread(new Runnable() {
			public void run() {
				while(isStart)
				{
					try {
						T t = queue.take();
						currentIndex.decrementAndGet();
						callback.execute(t);
					} catch (Exception e) {
					}
				}
			}
		});
		thread.start();
	}
	
	public boolean isFull()
	{
		return currentIndex.get() >= capacity;
	}
	
	public static void main(String[] args)
	{
		SimpleBlockingQueue<String> queue = new SimpleBlockingQueue<String>(100);
		
		
		queue.onCallback(new Callback<String>() {
			
			@Override
			public void execute(String o) {
				System.out.println("consumer " + o);
			}
		});
		
		queue.add("aaa");
		queue.add("aaa");
		queue.add("aaa");

	}
	
}
