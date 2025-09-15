package com.inso.modules.web.settle.service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.web.settle.model.SettleBusinessType;
import com.inso.modules.web.settle.model.SettleRecordInfo;
import com.inso.modules.web.settle.service.dao.SettleRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

@Service
public class SettleRecordServiceImpl implements SettleRecordService{

    @Autowired
    private SettleRecordDao settleRecordDao;


    @Override
    public void addOrder(Date pdate, SettleBusinessType businessType, SettleRecordInfo recordInfo, JSONObject remark) {
        settleRecordDao.addOrder(pdate, businessType, recordInfo, remark);
    }

    @Override
    public void delete(Date pdate, SettleBusinessType businessType, long agentid, long staffid, ICurrencyType currencyType) {
        settleRecordDao.delete(pdate, businessType, agentid, staffid, currencyType);
    }

    @Override
    public RowPager<SettleRecordInfo> queryScrollPage(PageVo pageVo, long agentid, long staffid, ICurrencyType currencyType, SettleBusinessType businessType, String dimensionType) {
        return settleRecordDao.queryScrollPage(pageVo, agentid, staffid, currencyType, businessType, dimensionType);
    }
}
