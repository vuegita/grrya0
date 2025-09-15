package com.inso.modules.admin;

import com.inso.modules.common.PlarformConfigDBUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.inso.modules.admin.core.AdminDBUpdate;
import com.inso.modules.common.SystemConfigDBUpdate;
import com.inso.modules.passport.user.logical.CodeAmountDBUpdate;

/**
 * @author Administrator
 *
 */
@Component
@Order(value=1)
public class ServerRunner implements CommandLineRunner {
	
	@Autowired
	private AdminDBUpdate mDBUpdate;

	@Autowired
    private CodeAmountDBUpdate mCodeAmountDBUpdate;

	@Autowired
    private SystemConfigDBUpdate mSystemConfigDB;

    @Autowired
    private PlarformConfigDBUpdate  mPlarformConfigDB;
	
    @Override
    public void run(String... args) throws Exception {
        doTask();
    }

    @Async
    public void doTask()
    {
        mDBUpdate.update();

        mCodeAmountDBUpdate.updateDB();

        mSystemConfigDB.updateDB();

        mPlarformConfigDB.updateDB();
    }

}
