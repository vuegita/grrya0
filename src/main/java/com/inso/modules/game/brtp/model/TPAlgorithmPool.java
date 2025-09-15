package com.inso.modules.game.brtp.model;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.inso.framework.utils.RuntimeUtils;

public class TPAlgorithmPool {

    private static final int DEFAULT_MAX_TOTAL = 2000;


    private GenericObjectPool<TPAlgorithm> mPool;

    private interface MyInternal {
        public TPAlgorithmPool mgr = new TPAlgorithmPool();
    }

    public static TPAlgorithmPool getInstance()
    {
        return MyInternal.mgr;
    }

    public TPAlgorithm getBean()
    {
        try {
            return mPool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void returnBean(TPAlgorithm bean)
    {
        if(bean == null) return;
        mPool.returnObject(bean);
    }

    private TPAlgorithmPool() {

        TPAlgorithmFactory factory = new TPAlgorithmFactory();

        //设置对象池的相关参数
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(DEFAULT_MAX_TOTAL);
        poolConfig.setMaxTotal(DEFAULT_MAX_TOTAL);
        poolConfig.setMinIdle(DEFAULT_MAX_TOTAL);
        poolConfig.setBlockWhenExhausted(true);
        //新建一个对象池,传入对象工厂和配置
        GenericObjectPool<TPAlgorithm> objectPool = new GenericObjectPool<>(factory, poolConfig);
        this.mPool = objectPool;
    }

    public static void main(String[] args) throws Exception
    {
        for(int i = 0; i < 1000; i ++)
        {
            TPAlgorithmPool.getInstance().getBean();
        }
        RuntimeUtils.logMemory();
        System.out.println("load finished");
        System.in.read();
    }
}
