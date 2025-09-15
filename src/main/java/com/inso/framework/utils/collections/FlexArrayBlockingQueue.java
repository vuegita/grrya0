package com.inso.framework.utils.collections;

import java.util.concurrent.ArrayBlockingQueue;

public class FlexArrayBlockingQueue<T> {
	
	private ArrayBlockingQueue<T> queue;
	
	public FlexArrayBlockingQueue(int capacity)
	{
		this.queue = new ArrayBlockingQueue<T>(capacity);
	}
	
	public void resize(int capacity)
	{
		
	}

}
