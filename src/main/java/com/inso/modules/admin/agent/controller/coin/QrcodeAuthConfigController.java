package com.inso.modules.admin.agent.controller.coin;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.core.model.VMType;
import com.inso.modules.coin.qrcode.QrcodeAuthManager;
import com.inso.modules.coin.qrcode.model.QrcodeConfigType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/alibaba888/agent")
public class QrcodeAuthConfigController {


    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private ContractService mContractService;

    @Autowired
    private QrcodeAuthManager mQrcodeAuthManager;

    @Autowired
    private AgentAuthManager mAgentAuthManager;


    @RequestMapping("root_coin_qrcode_auth_create")
    public String toCreateList(Model model, HttpServletRequest request)
    {
        CryptoCurrency.addModel(model);
        CryptoNetworkType.addFreemarkerModel(model);
        QrcodeConfigType.addFreemarkerModel(model);

        model.addAttribute("isSuperAdmin", AdminAccountHelper.isNy4timeAdminOrDEV() + StringUtils.getEmpty());
        boolean isShow = AgentAccountHelper.isAgentLogin();

        model.addAttribute("isShow", isShow);

        return "admin/agent/coin/qrcode/coin_qrcode_auth_create";
    }


    @RequestMapping("getCoinQrcodeAuthContractInfoList")
    @ResponseBody
    public String getCoinQrcodeAuthContractInfoList()
    {
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(networkType == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        List<ContractInfo> rsList = mContractService.queryByNetwork(false, networkType);
        if(CollectionUtils.isEmpty(rsList))
        {
            template.setJsonResult(SystemErrorResult.ERR_NODATA);
            return template.toJSONString();
        }

        template.setData(rsList);
        return template.toJSONString();
    }


    @RequestMapping("createCoinQrcodeAuthInfoUrl")
    @ResponseBody
    public String createCoinQrcodeAuthInfoUrl(Model model, HttpServletRequest request)
    {

        String username = WebRequest.getString("username");
        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        long mutisignWeith = WebRequest.getLong("mutisignWeith");

        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType()))
        {
            username=currentLoginInfo.getName();
        }


        long contractid = WebRequest.getLong("contractid");
        QrcodeConfigType qrcodeConfigType = QrcodeConfigType.getType(WebRequest.getString("qrcodeConfigType"));

        String address = WebRequest.getString("address");
        BigDecimal amount = WebRequest.getBigDecimal("amount");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(!StringUtils.isEmpty(address) && !RegexUtils.isBankName(address))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }


        ContractInfo contractInfo = mContractService.findById(false, contractid);
        if(contractInfo == null || !contractInfo.getStatus().equalsIgnoreCase(Status.ENABLE.getKey()))
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "当前合约不存在或未开启!");
            return apiJsonTemplate.toJSONString();
        }

        CryptoNetworkType networkType = CryptoNetworkType.getType(contractInfo.getNetworkType());
        if(qrcodeConfigType == QrcodeConfigType.Mutisign && networkType.getVmType() != VMType.TVM)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "仅支持波场链!");
            return apiJsonTemplate.toJSONString();
        }


        UserInfo staffInfo = mUserQueryManager.findUserInfo(username);
        if(staffInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }
        if(!mAgentAuthManager.verifyStaffData(staffInfo.getId())){
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
            return apiJsonTemplate.toJSONString();
        }



        if(!staffInfo.getType().equalsIgnoreCase(UserInfo.UserType.STAFF.getKey()))
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "请正确输入员工名!");
            return apiJsonTemplate.toJSONString();
        }

//        Map<String, Object> maps = Maps.newHashMap();
//        maps.put("type", qrcodeConfigType.getKey());
//        maps.put("amount", BigDecimalUtils.getNotNull(amount));
//        maps.put("address", StringUtils.getNotEmpty(address));
//
//        maps.put("inviteCode", staffInfo.getInviteCode());
//        maps.put("contractId", ContractInfoManager.encryptId(contractInfo.getId()));
//        maps.put("networkType", contractInfo.getNetworkType());
//        maps.put("currencyType", contractInfo.getCurrencyType());
//        apiJsonTemplate.setData(maps);

        String key = mQrcodeAuthManager.create(staffInfo, contractInfo, amount, qrcodeConfigType, address, mutisignWeith);
        apiJsonTemplate.setData(key);
        return apiJsonTemplate.toJSONString();
    }


}
