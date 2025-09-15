package com.inso.modules.admin.controller.game;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RedPackageUtils;
import com.inso.framework.utils.RowPager;
import com.inso.modules.game.model.GamePeriodStatus;
import com.inso.modules.game.red_package.helper.RedPIDSignUtils;
import com.inso.modules.game.red_package.job.RedPRefundJob;
import com.inso.modules.game.red_package.logical.RedPGameManager;
import com.inso.modules.game.red_package.model.RedPPeriodInfo;
import com.inso.modules.game.red_package.model.RedPType;
import com.inso.modules.game.red_package.service.RedPPeriodService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class RedPPeriodController {


    @Autowired
    private RedPPeriodService mRedEnveloperService;

    @Autowired
    private RedPGameManager mRedpGameManager;

    @Autowired
    private UserService mUserService;

    @RequiresPermissions("root_game_red_package_period_list")
    @RequestMapping("root_game_red_package_period")
    public String toList(Model model, HttpServletRequest request)
    {
        return "admin/game/game_red_package_period_list";
    }

    @RequiresPermissions("root_game_red_package_period_list")
    @RequestMapping("getGameRedPeriodList")
    @ResponseBody
    public String getGameRedPeriodList()
    {
        String time = WebRequest.getString("time");
        long issue = WebRequest.getLong("issue");
        long userid = WebRequest.getLong("userid");
        String statusSting = WebRequest.getString("status");
        String typeString = WebRequest.getString("type");

        RedPType type = RedPType.getType(typeString);
        GamePeriodStatus status = GamePeriodStatus.getType(statusSting);

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        RowPager<RedPPeriodInfo> rowPager = mRedEnveloperService.queryScrollPage(pageVo, issue, userid, type, status);

        template.setData(rowPager);
        return template.toJSONString();
    }


    @RequiresPermissions("root_game_red_package_period_edit")
    @RequestMapping("/game/red_package/create/page")
    public String toCreateGameRedEnveloperPage(Model model, HttpServletRequest request)
    {
        return "admin/game/game_red_package_period_edit";
    }


    @RequiresPermissions("root_game_red_package_period_edit")
    @RequestMapping("/game/red_package/create")
    @ResponseBody
    public String createGameRedEnveloper()
    {
        BigDecimal totalAmount = WebRequest.getBigDecimal("totalAmount");
        String typeString = WebRequest.getString("type");
        long totalCount = WebRequest.getLong("totalCount");
        int expires = WebRequest.getInt("expires");
        BigDecimal externalLimitMinxAmount = WebRequest.getBigDecimal("externalLimitMinxAmount");
        String specifyUserName = WebRequest.getString("specifyUserName");
        String remark = WebRequest.getString("remark");

        BigDecimal maxAmount = WebRequest.getBigDecimal("maxAmount");

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

        if(externalLimitMinxAmount == null)
        {
            externalLimitMinxAmount = BigDecimal.ZERO;
        }

        if(specifyUserName==null){
            specifyUserName="";
        }

        // 创建红包
        mRedpGameManager.createBySystem(remark, totalAmount, totalCount, maxAmount, type, expires, externalLimitMinxAmount,specifyUserName);

        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_game_red_package_period_edit")
    @RequestMapping("reSettleAllRedPOrder")
    @ResponseBody
    public String reSettleAllLotteryOrder()
    {
        long issue = WebRequest.getLong("issue");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(issue <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        RedPPeriodInfo periodInfo = mRedEnveloperService.findByIssue(false, issue);
        if(periodInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        // 是1分钟期之前
        long time = System.currentTimeMillis() - periodInfo.getEndtime().getTime() - 60000;
        if(time < 0)
        {
            apiJsonTemplate.setError(-1, "重新结算期号要小于当前1分钟之前 !!!");
            return apiJsonTemplate.toJSONString();
        }

        RedPRefundJob.sendMessage(periodInfo.getId());
        return apiJsonTemplate.toJSONString();
    }


    @RequestMapping("encryptRedPackageId")
    @ResponseBody
    public String encryptRedPackageId()
    {
        long issue = WebRequest.getLong("issue");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(issue <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        RedPPeriodInfo periodInfo = mRedEnveloperService.findByIssue(false, issue);
        if(periodInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        String encryptid = RedPIDSignUtils.encrypt(issue);

        apiJsonTemplate.setData(encryptid);
        return apiJsonTemplate.toJSONString();
    }
}
