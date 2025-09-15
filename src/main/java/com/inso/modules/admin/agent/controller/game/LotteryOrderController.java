package com.inso.modules.admin.agent.controller.game;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.GameCategory;
import com.inso.modules.game.rg.logical.RGBetTaskManager;
import com.inso.modules.game.rg.model.LotteryOrderInfo;
import com.inso.modules.game.rg.model.LotteryRGType;
import com.inso.modules.game.rg.service.LotteryOrderService;
import com.inso.modules.game.rg.service.LotteryPeriodService;
import com.inso.modules.game.service.GameService;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;

@Controller
@RequestMapping("/alibaba888/agent/game")
public class LotteryOrderController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private LotteryPeriodService mLotteryPeriodService;

    @Autowired
    private GameService mGameService;

    @Autowired
    private LotteryOrderService mLotteryOrderService;

    @Autowired
    private RGBetTaskManager mLotteryOrderManager;

    @RequestMapping("/lottery_rg/order/page")
    public String toList(Model model, HttpServletRequest request)
    {
        List<GameInfo> list = mGameService.queryAllByCategory(false, GameCategory.LOTTERY_RG);
        model.addAttribute("gameList", list);
        return "admin/agent/game/lottery_rg_order_list";
    }

    @RequestMapping("getLotteryOrderList")
    @ResponseBody
    public String getLotteryOrderList()
    {

        long agentid = AgentAccountHelper.getAdminAgentid();

        String time = WebRequest.getString("time");
        String issue = WebRequest.getString("issue");
        String statusSting = WebRequest.getString("txStatus");
        String typeString = WebRequest.getString("type");
        String orderno = WebRequest.getString("orderno");
        String username = WebRequest.getString("username");

        String sortName = WebRequest.getString("sortName");
        String sortOrder = WebRequest.getString("sortOrder");

        LotteryRGType type = LotteryRGType.getType(typeString);
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

        if(!StringUtils.isEmpty(issue) && !issue.startsWith(type.getCode() + StringUtils.getEmpty()))
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

        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        long staffid = -1;
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType
                ()))
        {
            staffid = currentLoginInfo.getId();
        }


        RowPager<LotteryOrderInfo> rowPager = mLotteryOrderService.queryScrollPage(pageVo, type, userid, agentid,staffid, orderno, issue, txStatus, sortName, sortOrder);

        template.setData(rowPager);
        return template.toJSONString();
    }


}
