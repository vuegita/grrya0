package com.inso.modules.admin.controller.paychannel;

import java.math.BigDecimal;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.ErrorResult;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.context.MyEnvironment;
import com.inso.framework.google.GoogleUtil;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.core.model.Admin;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.money.UserPayManager;
import com.inso.modules.passport.business.model.BankCard;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.business.model.UserWithdrawVO;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.service.ConfigService;

/**
 * 系统出款
 */
@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class SystemPayoutFiatController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserPayManager mUserPayManager;

    @Autowired
    private ConfigService mConfigService;

    @RequiresPermissions("root_pay_system_payout_fiat_list")
    @RequestMapping("root_pay_system_payout_fiat")
    public String toPage(Model model)
    {
        // 51234567890 - success
        model.addAttribute("username", UserInfo.DEFAULT_SYSTEM_ACCOUNT);
        if(MyEnvironment.isDev())
        {
            model.addAttribute("beneficiaryEmail", "test@gmail.com");
            model.addAttribute("beneficiaryPhone", "9999999999");

            model.addAttribute("bankNumber", "00011020001772");
            model.addAttribute("bankIfsc", "BARBOVJTAKI");
        }
        else if(MyEnvironment.isProd())
        {
            String email = mConfigService.getValueByKey(false, "admin_platform_config:system_payout_def_email");
            String phone = mConfigService.getValueByKey(false, "admin_platform_config:system_payout_def_phone");
            model.addAttribute("beneficiaryEmail", email);
            model.addAttribute("beneficiaryPhone", phone);
        }
        return "admin/paychannel/web_system_payout_fiat";
    }

    @RequiresPermissions("root_pay_system_payout_fiat_list")
    @PostMapping("/submit_system_payout")
    @ResponseBody
    public String actionSubmitPayout()
    {
        String username = WebRequest.getString("username");
        BigDecimal amount = WebRequest.getBigDecimal("amount");
        String beneficiaryName = WebRequest.getString("beneficiaryName");
        String beneficiaryEmail = WebRequest.getString("beneficiaryEmail");
        String beneficiaryPhone = WebRequest.getString("beneficiaryPhone");
        String bankNumber = WebRequest.getString("bankNumber");
        String bankIfsc = WebRequest.getString("bankIfsc");
        String remoteip = WebRequest.getRemoteIP();
        String googlecode = WebRequest.getString("googlecode");

        ApiJsonTemplate apiTemplate = new ApiJsonTemplate();

        if(!WhiteIPManager.getInstance().verify(remoteip))
        {
            apiTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return  apiTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(username))
        {
            apiTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return  apiTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(beneficiaryName) || StringUtils.isEmpty(beneficiaryEmail))
        {
            apiTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return  apiTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(bankNumber) || StringUtils.isEmpty(bankIfsc))
        {
            apiTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return  apiTemplate.toJSONString();
        }

        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
        {
            apiTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return  apiTemplate.toJSONString();
        }

        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo == null)
        {
            apiTemplate.setJsonResult(UserErrorResult.ERR_ACCOUNT_NOT_EXIST);
            return  apiTemplate.toJSONString();
        }

        // 账户被禁用
        Status status = Status.getType(userInfo.getStatus());
        if(status != Status.ENABLE)
        {
            apiTemplate.setJsonResult(SystemErrorResult.ERR_DISABLE);
            return  apiTemplate.toJSONString();
        }

        Admin admin = (Admin) SecurityUtils.getSubject().getPrincipal();
        // 生产环境只有shareday 才能操作 和 inpay 账户才能操作
        if(MyEnvironment.isProd())
        {
            String googleKey = admin.getGooglekey();
            if (StringUtils.isEmpty(googleKey)) {
                apiTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return  apiTemplate.toJSONString();
            }

            if (!GoogleUtil.checkGoogleCode(googleKey, googlecode)) {
                apiTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return  apiTemplate.toJSONString();
            }
        }

        // build payout info
        UserWithdrawVO remark = new UserWithdrawVO();
        remark.put(UserWithdrawVO.KEY_TYPE, BankCard.CardType.BANK);
        remark.put(UserWithdrawVO.KEY_ACCOUNT, bankNumber);
        remark.put(UserWithdrawVO.KEY_IFSC, bankIfsc);
        remark.put(UserWithdrawVO.KEY_BENEFICIARYNAME, beneficiaryName);
        remark.put(UserWithdrawVO.KEY_BENEFICIARYEMAIL, beneficiaryEmail);
        remark.put(UserWithdrawVO.KEY_BENEFICIARYPHONE, beneficiaryPhone);

        // submit order
        ErrorResult result = mUserPayManager.createWithdrawOrder(userInfo, remark, null, amount ,bankNumber,"", -1);

        //
        apiTemplate.setJsonResult(result);
        return apiTemplate.toJSONString();
    }

}
