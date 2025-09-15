package com.inso.framework.utils;

import com.google.common.collect.Maps;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class RandomUtils {
	
	 private static final Random JVM_RANDOM = new SecureRandom();
//	private static final Random JVM_RANDOM = new JVMRandom();

	// should be possible for JVM_RANDOM?
//	    public static void nextBytes(byte[]) {
//	    public synchronized double nextGaussian();
//	    }

	    /**
	     * <p>Returns the next pseudorandom, uniformly distributed int value
	     * from the Math.random() sequence.</p>
	     * <b>N.B. All values are >= 0.<b>
	     * @return the random int
	     */
	    public static int nextInt() {
	        return nextInt(JVM_RANDOM);
	    }
	    
	    /**
	     * <p>Returns the next pseudorandom, uniformly distributed int value
	     * from the given <code>random</code> sequence.</p>
	     *
	     * @param random the Random sequence generator.
	     * @return the random int
	     */
	    public static int nextInt(Random random) {
	        return random.nextInt();
	    }
	    
	    /**
	     * <p>Returns a pseudorandom, uniformly distributed int value
	     * between <code>0</code> (inclusive) and the specified value
	     * (exclusive), from the Math.random() sequence.</p>
	     *
	     * @param n  the specified exclusive max-value
	     * @return the random int
	     */
	    public static int nextInt(int n) {
	        return nextInt(JVM_RANDOM, n);
	    }
	    
	    /**
	     * <p>Returns a pseudorandom, uniformly distributed int value
	     * between <code>0</code> (inclusive) and the specified value
	     * (exclusive), from the given Random sequence.</p>
	     *
	     * @param random the Random sequence generator.
	     * @param n  the specified exclusive max-value
	     * @return the random int
	     */
	    public static int nextInt(Random random, int n) {
	        // check this cannot return 'n'
	        return random.nextInt(n);
	    }
	    
	    /**
	     * <p>Returns the next pseudorandom, uniformly distributed long value
	     * from the Math.random() sequence.</p>
	     * <b>N.B. All values are >= 0.<b>
	     * @return the random long
	     */
	    public static long nextLong() {
	        return nextLong(JVM_RANDOM);
	    }

	    /**
	     * <p>Returns the next pseudorandom, uniformly distributed long value
	     * from the given Random sequence.</p>
	     *
	     * @param random the Random sequence generator.
	     * @return the random long
	     */
	    public static long nextLong(Random random) {
	        return random.nextLong();
	    }
	    
	    /**
	     * <p>Returns the next pseudorandom, uniformly distributed boolean value
	     * from the Math.random() sequence.</p>
	     *
	     * @return the random boolean
	     */
	    public static boolean nextBoolean() {
	        return nextBoolean(JVM_RANDOM);
	    }

	    /**
	     * <p>Returns the next pseudorandom, uniformly distributed boolean value
	     * from the given random sequence.</p>
	     *
	     * @param random the Random sequence generator.
	     * @return the random boolean
	     */
	    public static boolean nextBoolean(Random random) {
	        return random.nextBoolean();
	    }
	    
	    /**
	     * <p>Returns the next pseudorandom, uniformly distributed float value
	     * between <code>0.0</code> and <code>1.0</code> from the Math.random()
	     * sequence.</p>
	     *
	     * @return the random float
	     */
	    public static float nextFloat() {
	        return nextFloat(JVM_RANDOM);
	    }

	    /**
	     * <p>Returns the next pseudorandom, uniformly distributed float value
	     * between <code>0.0</code> and <code>1.0</code> from the given Random
	     * sequence.</p>
	     *
	     * @param random the Random sequence generator.
	     * @return the random float
	     */
	    public static float nextFloat(Random random) {
	        return random.nextFloat();
	    }
	    
	    /**
	     * <p>Returns the next pseudorandom, uniformly distributed float value
	     * between <code>0.0</code> and <code>1.0</code> from the Math.random()
	     * sequence.</p>
	     *
	     * @return the random double
	     */
	    public static double nextDouble() {
	        return nextDouble(JVM_RANDOM);
	    }

	    /**
	     * <p>Returns the next pseudorandom, uniformly distributed float value
	     * between <code>0.0</code> and <code>1.0</code> from the given Random
	     * sequence.</p>
	     *
	     * @param random the Random sequence generator.
	     * @return the random double
	     */
	    public static double nextDouble(Random random) {
	        return random.nextDouble();
	    }
	
	
	/** 
	 * 随机指定范围内N个不重复的数 
	 * 利用HashSet的特征，只能存放不同的值 
	 * @param min 指定范围最小值 
	 * @param max 指定范围最大值 
	 * @param n 随机数个数 
	 * @param set<Integer> set 随机数结果集
	 */  
	   public static void randomSet(int min, int max, int n, Set<Integer> set) {  
	       if (n > (max - min + 1) || max < min) { 
	    	   throw new RuntimeException("please enter min, max, n");
	       }  
	       for (int i = 0; i < n; i++) {  
	           // 调用Math.random()方法  
	           int num = (int) (Math.random() * (max - min)) + min;  
	           set.add(num);// 将不同的数存入HashSet中  
	       }  
	       int setSize = set.size();  
	       // 如果存入的数小于指定生成的个数，则调用递归再生成剩余个数的随机数，如此循环，直到达到指定大小  
	       if (setSize <= n) {  
	        randomSet(min, max, n - setSize, set);// 递归  
	       }  
	   }  
	   
	   public static void main(String[] args)
	   {
			float totalCOunt = 500.0f;
	   		Map<Integer, Integer> maps = Maps.newTreeMap();
		   	for(int i = 0; i < totalCOunt; i ++)
		   	{
		   		int value = nextInt(10);
		   		Integer count = maps.get(value);
		   		if(count == null)
				{
					count = 1;
				}
		   		else
				{
					count ++;
				}

				maps.put(value, count);
		   	}

		   	for(int i = 0; i < 10; i ++)
			{
				Integer count = maps.get(i);
				if(count == null)
				{
					count = 0;
				}

				float rs = count / totalCOunt;

				System.out.println( i + " = " + rs);

			}

		   	FastJsonHelper.prettyJson(maps);

	   }

}
