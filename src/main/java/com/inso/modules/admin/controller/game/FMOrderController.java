package com.inso.modules.admin.controller.game;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.fm.model.FMOrderInfo;
import com.inso.modules.game.fm.model.FMType;
import com.inso.modules.game.fm.service.FMOrderService;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.service.GameService;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class FMOrderController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private GameService mGameService;

    @Autowired
    private FMOrderService mOrderService;

    @RequiresPermissions("root_game_financial_management_order_list")
    @RequestMapping("root_game_financial_management_order")
    public String toList(Model model, HttpServletRequest request)
    {
        List<GameInfo> list = mGameService.queryAllByCategory(false, GameCategory.RED_PACKAGE);
        model.addAttribute("gameList", list);
        return "admin/game/game_fm_order_list";
    }

    @RequiresPermissions("root_game_financial_management_order_list")
    @RequestMapping("getGameFMOrderList")
    @ResponseBody
    public String getGameFMOrderList()
    {
        String time = WebRequest.getString("time");
        long issue = WebRequest.getLong("id");
        String statusSting = WebRequest.getString("txStatus");
        String typeString = WebRequest.getString("type");
        String orderno = WebRequest.getString("orderno");
        String username = WebRequest.getString("username");

        FMType type = FMType.getType(typeString);
        OrderTxStatus txStatus = OrderTxStatus.getType(statusSting);

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = -1;
        if(!StringUtils.isEmpty(username))
        {
            UserInfo userInfo = mUserService.findByUsername(false, username);
            if(userInfo == null)
            {
                template.setData(RowPager.getEmptyRowPager());
                return template.toJSONString();
            }
            userid = userInfo.getId();
        }

        RowPager<FMOrderInfo> rowPager = mOrderService.queryScrollPage(pageVo, type, userid, orderno, issue, txStatus,-1,-1);

        template.setData(rowPager);
        return template.toJSONString();
    }

}
