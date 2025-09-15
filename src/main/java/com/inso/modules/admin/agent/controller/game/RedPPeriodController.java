package com.inso.modules.admin.agent.controller.game;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import com.inso.modules.common.model.FundAccountType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.passport.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RedPackageUtils;
import com.inso.framework.utils.RowPager;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.red_package.logical.RedPGameManager;
import com.inso.modules.game.red_package.model.RedPPeriodInfo;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.game.red_package.service.RedPPeriodService;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.money.model.UserMoney;
import com.inso.modules.passport.money.service.UserMoneyService;

@Controller
@RequestMapping("/alibaba888/agent/game")
public class RedPPeriodController {



    @Autowired
    private RedPPeriodService mRedEnveloperService;

    @Autowired
    private RedPGameManager mRedpGameManager;

    @Autowired
    private UserService mUserService;

    @RequestMapping("/red_package/period/page")
    public String toList(Model model, HttpServletRequest request)
    {
        return "admin/agent/game/red_package_period_list";
    }

    @RequestMapping("getGameRedPeriodList")
    @ResponseBody
    public String getGameRedPeriodList()
    {
        long agentid = AgentAccountHelper.getAdminLoginInfo().getId();

        String time = WebRequest.getString("time");
        long id = WebRequest.getLong("id");
        String statusSting = WebRequest.getString("status");
        String typeString = WebRequest.getString("type");

        RedPType type = RedPType.getType(typeString);
        GamePeriodStatus status = GamePeriodStatus.getType(statusSting);

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

        RowPager<RedPPeriodInfo> rowPager = mRedEnveloperService.queryScrollPage(pageVo, id, agentid, type, status);

        template.setData(rowPager);
        return template.toJSONString();
    }


    @RequestMapping("/red_package/create/page")
    public String toCreateGameRedEnveloperPage(Model model, HttpServletRequest request)
    {
        return "admin/agent/game/red_package_period_edit";
    }


    @RequestMapping("/red_package/create")
    @ResponseBody
    public String createGameRedEnveloper()
    {
        BigDecimal totalAmount = WebRequest.getBigDecimal("totalAmount");
        String typeString = WebRequest.getString("type");
        long totalCount = WebRequest.getLong("totalCount");
        int expires = WebRequest.getInt("expires");

        BigDecimal maxAmount = WebRequest.getBigDecimal("maxAmount");
        String specifyUserName = WebRequest.getString("specifyUserName");

        RedPType type = RedPType.getType(typeString);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(RedPType.SPECIGY.getKey().equalsIgnoreCase(typeString)){
            UserInfo userInfo = mUserService.findByUsername(false, specifyUserName);
            if(userInfo==null){
                // apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                apiJsonTemplate.setError(-1, "The specified user name does not exist!");
                return apiJsonTemplate.toJSONString();
            }

        }


        if(totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

//        if(!AgentAccountHelper.isAgentLogin())
//        {
//            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
//            return apiJsonTemplate.toJSONString();
//        }

        long maxCount = RedPackageUtils.getMaxCount(totalAmount);
        if(maxCount > 1000)
        {
            maxCount = 1000;
        }
//        if(maxCount > 10000)
//        {
//            maxCount = 10000;
//        }
        if(totalCount <= 0 || totalCount > maxCount)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(!(expires == 2 || expires == 5 || expires == 10 || expires == 30 || expires == 60 || expires == 120 || expires == 1440))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(type == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(specifyUserName==null){
            specifyUserName="";
        }
        // 创建红包
        UserInfo agentInfo = AgentAccountHelper.getAdminLoginInfo();
        ErrorResult result = mRedpGameManager.createByAgent(agentInfo, totalAmount, totalCount, maxAmount, type, expires ,specifyUserName);

        apiJsonTemplate.setJsonResult(result);

        return apiJsonTemplate.toJSONString();
    }


}
