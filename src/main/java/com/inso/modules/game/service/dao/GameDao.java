package com.inso.modules.game.service.dao;

import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameCategory;

public interface GameDao {

    public long add(GameCategory category, String key, String title, String describe, String icon, Status status, long sort);
    public void updateStatus(long gameid, Status status, long sort);

    public GameInfo findByIdByKey(String key);
    public GameInfo findById(long gameid);
    public List<GameInfo> queryAll();
    public List<GameInfo> queryByCategory(GameCategory gameCategory);

    public RowPager<GameInfo> queryScrollPage(PageVo pageVo, GameCategory gameCategory);
}
