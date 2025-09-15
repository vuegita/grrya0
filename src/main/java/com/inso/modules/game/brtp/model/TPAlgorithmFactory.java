package com.inso.modules.game.brtp.model;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class TPAlgorithmFactory implements PooledObjectFactory<TPAlgorithm> {

	@Override
	public PooledObject<TPAlgorithm> makeObject() throws Exception {
		// TODO Auto-generated method stub
		return new DefaultPooledObject<TPAlgorithm>(new TPAlgorithm());
	} 

	@Override
	public void destroyObject(PooledObject<TPAlgorithm> p) throws Exception {
		
	}

	@Override
	public boolean validateObject(PooledObject<TPAlgorithm> p) {
		return true;
	}

	@Override
	public void activateObject(PooledObject<TPAlgorithm> p) throws Exception {
		
	}

	@Override
	public void passivateObject(PooledObject<TPAlgorithm> p) throws Exception {
		
	}

}
