package com.inso.framework.socketio.impl;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class MultipleIOClientFactory implements PooledObjectFactory<MultipleIOClient>  {

	@Override
	public PooledObject<MultipleIOClient> makeObject() throws Exception {
		return new DefaultPooledObject<MultipleIOClient>(new MultipleIOClient());
	}

	@Override
	public void destroyObject(PooledObject<MultipleIOClient> p) throws Exception {
		
	}

	@Override
	public boolean validateObject(PooledObject<MultipleIOClient> p) {
		return true;
	}

	@Override
	public void activateObject(PooledObject<MultipleIOClient> p) throws Exception {
		
	}

	@Override
	public void passivateObject(PooledObject<MultipleIOClient> p) throws Exception {
		
	}

}
