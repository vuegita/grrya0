package com.inso.modules.web.sad.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.google.GoogleAuthenticator;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.UUIDUtils;
import com.inso.modules.coin.core.model.MyDimensionType;
import com.inso.modules.common.model.Status;
import com.inso.modules.web.sad.model.SadInfo;
import com.inso.modules.web.sad.service.dao.SadDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SadServiceImpl implements SadService{

    @Autowired
    private SadDao mSadDao;

    @Override
    public void addOrder(String key, MyDimensionType dimensionType, String title, String content, Status status, String gb1Value)
    {
        String gbValue = GoogleAuthenticator.generateSecretKey();
        String salt = UUIDUtils.getUUID();
        String encryptPwd = SadInfo.generatePassword(gb1Value, salt);
        String encryptGaValue = SadInfo.encryptGaValue(gbValue);

        mSadDao.addOrder(key, dimensionType, title, content, status, encryptGaValue, encryptPwd, salt);
    }

    @Override
    public void delete(long id) {

    }

    @Override
    public SadInfo find(long id) {
        return null;
    }

    @Override
    public RowPager<SadInfo> queryScrollPage(PageVo pageVo, String key, MyDimensionType dimensionType) {
        return null;
    }
}
