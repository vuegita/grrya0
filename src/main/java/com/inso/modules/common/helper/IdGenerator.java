package com.inso.modules.common.helper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**
 * ID生成器
 * @author Administrator
 *
 */
public class IdGenerator {
	
	private static final String DATE_FORMAT = "yyyyMMddHHmmssSSS";
	
	private long lastTimestamp = -1L;
	
	/*** 机器最大id- single状态下没有这个 ***/
	private static final long MAX_WORKER_ID = 99;
	/*** 当前机器id ***/
	private long mWorkerId=0;
	private NumberFormat mWorkerNumberFormat = new DecimalFormat("00");
	
	/*** 1毫秒最大生成 9999个 ***/
	private static final long MAX_INDEX = 9600;
	/*** 当前位置 ***/
	private AtomicInteger mIndex = new AtomicInteger(0);
	private NumberFormat mIndexNumberFormat = new DecimalFormat("0000");
	
	private boolean isSingle = true;
	
//	private static final Integer DEFAULT_LRU_VALUE = 1;
//	private LRUCache<String, Integer> mLRUCache = new LRUCache<>(1000);

	private static final Integer mLockValue = 1;
	private BlockingQueue<Integer> mLockQueue = new ArrayBlockingQueue<>(1);
	
	private boolean debug = false;

	private String mDateFormat = DATE_FORMAT;
	
	/**
	 * ID生成器, 
	 * @param workerId 0-99, 分布式环境下，要添加机器码
	 */
	public IdGenerator(long workerId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0, max workerid = " + MAX_WORKER_ID));
        }
        this.mWorkerId = workerId;
    }
    
    public static IdGenerator newSingleWorder()
    {
    	IdGenerator  generator = new IdGenerator(0);
    	generator.isSingle = true;
    	return generator;
    }

	public static IdGenerator newSingleWorder(String dateFormat)
	{
		IdGenerator  generator = new IdGenerator(0);
		generator.isSingle = true;
		generator.mDateFormat = dateFormat;
		return generator;
	}


	public String nextId()
	{
		return nextId(-1);
	}

    public String nextId(int code)
    {
    	long timestamp = getTimeMillis();
    	if(timestamp == lastTimestamp)
    	{
    		timestamp = nextMillis(lastTimestamp);
    	}
    	
    	long currentIndex  = mIndex.incrementAndGet();
    	if(currentIndex  > MAX_INDEX)
    	{
    		addLock();
    		long nowIndex = mIndex.get();
    		if(nowIndex > MAX_INDEX)
			{
    			mIndex.set(1);
			}
    		releaseLock();
    	}
    	
    	StringBuilder buffer = new StringBuilder();

    	String timeString = convertString(new Date(timestamp));
    	buffer.append(timeString);
    	if(!isSingle)
    	{
//    		buffer.append("-");
        	buffer.append(mWorkerNumberFormat.format(mWorkerId));
    	}
    	if(debug)
    	{
    		buffer.append("-");	
    	}

    	if(code >= 10)
		{
			// 业务编码
			buffer.append(code);
		}

    	// 内存自增数字
    	buffer.append(mIndexNumberFormat.format(currentIndex));

    	this.lastTimestamp = timestamp;
    	
    	String id = buffer.toString();
    	return id;
    }
    
    
    private long nextMillis(long lastTimestamp) {
        long timestamp = getTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = getTimeMillis();
        }
        return timestamp;
    }
    
    public String convertString(Date date) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(mDateFormat);
        DateTime dateTime = new DateTime(date);
        return fmt.print(dateTime);
    }
    
    /**
     * 获得系统当前毫秒数
     */
    private static long getTimeMillis() {
        return System.currentTimeMillis();
    }
    
    private void addLock()
    {
    	try {
			mLockQueue.put(mLockValue);
		} catch (InterruptedException e) {
		}
    }
    
    private void releaseLock()
    {
    	try {
			mLockQueue.take();
		} catch (InterruptedException e) {
		}
    }
    
    public static void main(String[] args) throws InterruptedException
    {
    	IdGenerator idWorker = IdGenerator.newSingleWorder();
    	idWorker.debug = true;
    	ExecutorService pool = Executors.newFixedThreadPool(100);
    	
    	int size = 200;
    	CountDownLatch latch = new CountDownLatch(size);
    	
    	Timer timer = new Timer();
    	AtomicInteger count = new AtomicInteger();
    	
    	timer.schedule(new TimerTask() {
			public void run() {
				for(int i = 0; i < size; i ++)
		    	{
		    		pool.execute(new Runnable() {
		    			public void run() {
		    				int index = count.incrementAndGet();
		    				String id = idWorker.nextId();
		    				
		    				if(index % 1000 == 0)
		    				{
		    					System.out.println("current index = " + index);
		    				}
		    				System.out.println(id);
//		    				latch.countDown();
		    			}
		    		});
		    	}
			}
		}, 0, 1000);
    	
//    	for(int i = 0; i < size; i ++)
//    	{
//    		pool.execute(new Runnable() {
//    			public void run() {
//    				int index = count.incrementAndGet();
//    				String id = idWorker.nextId();
//    				
//    				if(index % 1000 == 0)
//    				{
//    					System.out.println("current index = " + index);
//    				}
//    				System.out.println(id);
////    				latch.countDown();
//    			}
//    		});
//    	}
    	
//    	latch.await();
    	System.out.println("=end");
    }

}
