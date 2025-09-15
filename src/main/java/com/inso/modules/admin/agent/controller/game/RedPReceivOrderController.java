package com.inso.modules.admin.agent.controller.game;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.red_package.model.RedPReceivOrderInfo;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.game.red_package.service.RedPReceivOrderService;
import com.inso.modules.game.service.GameService;
import com.inso.modules.passport.user.service.UserService;

@Controller
@RequestMapping("/alibaba888/agent/game")
public class RedPReceivOrderController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private GameService mGameService;

    @Autowired
    private RedPReceivOrderService mOrderService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @RequestMapping("/red_package/receiv_order/page")
    public String toList(Model model, HttpServletRequest request)
    {
        List<GameInfo> list = mGameService.queryAllByCategory(false, GameCategory.RED_PACKAGE);
        model.addAttribute("gameList", list);
        return "admin/agent/game/red_package_receiv_order_list";
    }

    @RequestMapping("getGameRedPReceivOrderList")
    @ResponseBody
    public String getGameRedPReceivOrderList()
    {
        long agentid = AgentAccountHelper.getAdminAgentid();

        String time = WebRequest.getString("time");
        long issue = WebRequest.getLong("issue");
        String statusSting = WebRequest.getString("txStatus");
        String typeString = WebRequest.getString("type");
        String orderno = WebRequest.getString("orderno");
        String username = WebRequest.getString("username");

        RedPType type = RedPType.getType(typeString);
        OrderTxStatus txStatus = OrderTxStatus.getType(statusSting);

        ApiJsonTemplate template = new ApiJsonTemplate();

        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }
        long staffid = 0;
        UserInfo loginUserInfo = AgentAccountHelper.getAdminLoginInfo();
        // 当前登陆就是员工
        if(loginUserInfo.getType().equalsIgnoreCase(UserInfo.UserType.STAFF.getKey()))
        {
            staffid = loginUserInfo.getId();
        }

        long userid = mUserQueryManager.findUserid(username);

        RowPager<RedPReceivOrderInfo> rowPager = mOrderService.queryScrollPage(pageVo, type, userid, orderno, issue, agentid,staffid, txStatus);

        template.setData(rowPager);
        return template.toJSONString();
    }

}
