package com.inso.modules.game.service;

import com.inso.framework.bean.PageVo;
import com.inso.framework.cache.CacheManager;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.modules.common.model.Status;
import com.inso.modules.game.cache.GameCacheKeyHelper;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.service.dao.GameDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class GameServiceImpl implements GameService{

    @Autowired
    private GameDao mGameDao;


    @Override
    @Transactional
    public long add(GameCategory category, String key, String title, String describe, String icon) {
        return mGameDao.add(category, key, title, describe, icon, Status.DISABLE, 100);
    }

    @Override
    @Transactional
    public void updateStatus(GameInfo game, Status status, long sort) {
        mGameDao.updateStatus(game.getId(), status, sort);
        String cachekey = GameCacheKeyHelper.findGameInfo(game.getId());
        CacheManager.getInstance().delete(cachekey);

        GameCategory gameCategory = GameCategory.getType(game.getCategoryKey());
        String categoryCacheKey = GameCacheKeyHelper.queryAllByCategory(gameCategory);
        CacheManager.getInstance().delete(categoryCacheKey);

        String cachekey2 = GameCacheKeyHelper.findGameInfoByKey(game.getKey());
        CacheManager.getInstance().delete(cachekey2);
    }

    @Override
    public List<GameInfo> queryAll(boolean purge) {
        return mGameDao.queryAll();
    }

    public RowPager<GameInfo> queryScrollPage(PageVo pageVo, GameCategory gameCategory)
    {
        return mGameDao.queryScrollPage(pageVo, gameCategory);
    }

    public List<GameInfo> queryAllByCategory(boolean purge, GameCategory category)
    {
        String cachekey = GameCacheKeyHelper.queryAllByCategory(category);
        List<GameInfo> list = CacheManager.getInstance().getList(cachekey, GameInfo.class);
        if(purge || CollectionUtils.isEmpty(list))
        {
            list = mGameDao.queryByCategory(category);
            if(CollectionUtils.isEmpty(list))
            {
                list = Collections.emptyList();
            }
            CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(list));
        }
        return list;
    }

    public GameInfo findByKey(boolean purge, String key)
    {
        String cachekey = GameCacheKeyHelper.findGameInfoByKey(key);
        GameInfo game = CacheManager.getInstance().getObject(cachekey, GameInfo.class);
        if(purge || game == null)
        {
            game = mGameDao.findByIdByKey(key);
            if(game != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(game), CacheManager.EXPIRES_WEEK);
            }
        }
        return game;
    }

    @Override
    public GameInfo findById(boolean purge, long gameid)
    {
        String cachekey = GameCacheKeyHelper.findGameInfo(gameid);
        GameInfo game = CacheManager.getInstance().getObject(cachekey, GameInfo.class);
        if(purge || game == null)
        {
            game = mGameDao.findById(gameid);
            if(game != null)
            {
                CacheManager.getInstance().setString(cachekey, FastJsonHelper.jsonEncode(game), CacheManager.EXPIRES_WEEK);
            }
        }
        return mGameDao.findById(gameid);
    }
}
