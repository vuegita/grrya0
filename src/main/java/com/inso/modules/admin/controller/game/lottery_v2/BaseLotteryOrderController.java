package com.inso.modules.admin.controller.game.lottery_v2;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.common.model.OrderTxStatus;
import com.inso.modules.game.GameChildType;
import com.inso.modules.game.model.GameInfo;
import com.inso.modules.game.model.NewLotteryOrderInfo;
import com.inso.modules.game.service.GameService;
import com.inso.modules.game.service.NewLotteryOrderService;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public abstract class BaseLotteryOrderController {

    protected Log LOG = LogFactory.getLog(this.getClass());

    @Autowired
    protected UserService mUserService;

    @Autowired
    protected GameService mGameService;

    @Autowired
    protected NewLotteryOrderService mLotteryOrderService;

    @Autowired
    protected UserQueryManager mUserQueryManager;

    @Autowired
    protected UserAttrService mUserAttrService;

    public abstract String getModuleRelateUrl();

    private void addModuleParameter(Model model, GameChildType gameChildType)
    {
        model.addAttribute("moduleRelateUrl", getModuleRelateUrl());
        model.addAttribute("moduleLotteryType", gameChildType.getKey());
        model.addAttribute("moduleCategoryType", gameChildType.getCategory().getKey());
        model.addAttribute("uniqueOpenResult", gameChildType.uniqueOpenResult() + StringUtils.getEmpty());
    }


    public String toListPage(Model model, HttpServletRequest request, GameChildType gameChildType, boolean fromAdmin)
    {
        List<GameInfo> list = mGameService.queryAllByCategory(false, gameChildType.getCategory());
        model.addAttribute("gameList", list);

        addModuleParameter(model, gameChildType);

        if(fromAdmin)
        {
            return "admin/game/lottery_v2/game_order_list";
        }
        else
        {
            return "admin/agent/game/lottery_v2/game_lottery_v2_order_list";
        }
    }

    public String getDataList(boolean isAdminDashboard)
    {
        String time = WebRequest.getString("time");
        String issue = WebRequest.getString("issue");
        String statusSting = WebRequest.getString("txStatus");
        String orderno = WebRequest.getString("orderno");
        String username = WebRequest.getString("username");


        String sortName = WebRequest.getString("sortName");
        String sortOrder = WebRequest.getString("sortOrder");

        GameChildType moduleLotteryType = GameChildType.getType(WebRequest.getString("moduleLotteryType"));
        GameChildType type = GameChildType.getType(WebRequest.getString("type"));
        OrderTxStatus txStatus = OrderTxStatus.getType(statusSting);

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time) || moduleLotteryType == null)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        if(type != null)
        {
            if(!StringUtils.isEmpty(issue) && !issue.startsWith(type.getCode() + StringUtils.getEmpty()))
            {
                template.setData(RowPager.getEmptyRowPager());
                return template.toJSONString();
            }
        }

        long userid = mUserQueryManager.findUserid(username);
        long agentid = -1;
        long staffid = -1;
        if(!isAdminDashboard)
        {
            if(AgentAccountHelper.isAgentLogin())
            {
                agentid = AgentAccountHelper.getAdminAgentid();
                staffid = mUserQueryManager.findUserid(WebRequest.getString("staffname"));
            }
            else
            {
                staffid = AgentAccountHelper.getAdminLoginInfo().getId();
            }
        }

        RowPager<NewLotteryOrderInfo> rowPager = mLotteryOrderService.queryScrollPage(pageVo, type, userid, agentid, staffid, moduleLotteryType, orderno, issue, txStatus , sortName, sortOrder);

//        List<TurntableOrderInfo> list =rowPager.getList();
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
