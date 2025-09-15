package com.inso.modules.ad.core.service.dao;

import com.inso.modules.ad.core.model.AdMaterielDetailInfo;

public interface MaterielDetailDao  {


    public void add(long materielid, String content, String sizes, String images);

    public void updateInfo(long id, String content, String sizes, String images);

    public AdMaterielDetailInfo findById(long id);


}
