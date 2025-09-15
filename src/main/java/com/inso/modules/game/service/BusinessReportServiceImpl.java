package com.inso.modules.game.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.BusinessReport;
import com.inso.modules.game.service.dao.BusinessReportDao;

@Service
public class BusinessReportServiceImpl implements BusinessReportService{

    @Autowired
    private BusinessReportDao mBusinessReportDao;

    @Override
    public void addReport(Date pdate, GameChildType childType, BusinessReport report, JSONObject remark) {
        mBusinessReportDao.addReport(pdate, childType, report, remark);
    }

    @Override
    public void delete(Date pdate, GameChildType childType) {
        mBusinessReportDao.delete(pdate, childType);
    }

    @Override
    public RowPager<BusinessReport> queryScrollPage(PageVo pageVo, String key) {
        return mBusinessReportDao.queryScrollPage(pageVo, key);
    }
}
