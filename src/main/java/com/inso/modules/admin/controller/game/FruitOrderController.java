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
import com.inso.modules.game.fruit.model.FruitOrderInfo;
import com.inso.modules.game.fruit.model.FruitType;
import com.inso.modules.game.fruit.service.FruitOrderService;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.service.GameService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class FruitOrderController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private GameService mGameService;

    @Autowired
    private FruitOrderService  mOrderService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private UserAttrService mUserAttrService;

    @RequiresPermissions("root_game_fruit_order_list")
    @RequestMapping("root_game_fruit_order")
    public String toList(Model model, HttpServletRequest request)
    {
        List<GameInfo> list = mGameService.queryAllByCategory(false, GameCategory.FRUIT);
        model.addAttribute("gameList", list);
        return "admin/game/game_fruit_order_list";
    }

    // 代理后台也查询这个接口
//    @RequiresPermissions("root_game_fruit_order_list")
    @RequestMapping("getGameFruitOrderList")
    @ResponseBody
    public String getGameABOrderList()
    {
        String time = WebRequest.getString("time");
        String issue = WebRequest.getString("issue");
        String statusSting = WebRequest.getString("txStatus");
        String typeString = WebRequest.getString("type");
        String orderno = WebRequest.getString("orderno");
        String username = WebRequest.getString("username");

        FruitType type = FruitType.getType(typeString);
        OrderTxStatus txStatus = OrderTxStatus.getType(statusSting);

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        if(!StringUtils.isEmpty(issue) && !issue.startsWith(type.getCode() + StringUtils.getEmpty()))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        long userid = mUserQueryManager.findUserid(username);

        RowPager<FruitOrderInfo> rowPager = mOrderService.queryScrollPage(pageVo, type, userid, -1,-1, orderno, issue, txStatus);

//        List<FruitOrderInfo> list =rowPager.getList();
//
//        for(int i=0;i<list.size();i++){
//            UserAttr userAttr = mUserAttrService.find(false, list.get(i).getUserid() );
//            list.get(i).setAgentname(userAttr.getAgentname());
//            list.get(i).setStaffname(userAttr.getDirectStaffname());
//        }
//        rowPager.setList(list);
        template.setData(rowPager);
        return template.toJSONString();
    }

}
