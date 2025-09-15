package com.inso.modules.game.service;

import java.util.List;

import com.inso.framework.bean.PageVo;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameCategory;

public interface GameService {

    public long add(GameCategory category, String key, String title, String describe, String icon);
    public void updateStatus(GameInfo game, Status status, long sort);
    public List<GameInfo> queryAll(boolean purge);
    public List<GameInfo> queryAllByCategory(boolean purge, GameCategory category);

    /**
     * 后台调用
     * @param key
     * @return
     */
    public GameInfo findByKey(boolean purge, String key);
    public GameInfo findById(boolean purge, long gameid);

    public RowPager<GameInfo> queryScrollPage(PageVo pageVo, GameCategory gameCategory);
}
