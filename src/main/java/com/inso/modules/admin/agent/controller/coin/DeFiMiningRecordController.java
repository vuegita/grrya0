package com.inso.modules.admin.agent.controller.coin;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.agent.AgentAuthManager;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.admin.helper.AgentAccountHelper;
import com.inso.modules.coin.config.CoinConfig;
import com.inso.modules.coin.core.model.StakingSettleMode;
import com.inso.modules.coin.core.service.ProfitConfigService;
import com.inso.modules.coin.defi_mining.logical.StakingManager;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.approve.service.TransferOrderService;
import com.inso.modules.coin.defi_mining.model.MiningRecordInfo;
import com.inso.modules.coin.defi_mining.service.MiningProductService;
import com.inso.modules.coin.defi_mining.service.MiningRecordService;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.eventlog.EventLogManager;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import com.inso.modules.web.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Controller
@RequestMapping("/alibaba888/agent")
public class DeFiMiningRecordController {

    @Autowired
    private ConfigService mConfigService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private ApproveAuthService mApproveAuthService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private TransferOrderService mTransferOrderService;

    @Autowired
    private ContractService mContractService;

    @Autowired
    private MiningProductService miningProductService;

    @Autowired
    private MiningRecordService miningRecordService;

    @Autowired
    private ProfitConfigService mProfitConfigService;

    @Autowired
    private StakingManager mStakingWhiteMgr;

    @Autowired
    private AgentAuthManager mAgentAuthManager;

    @RequestMapping("root_coin_defi_mining_record")
    public String toList(Model model, HttpServletRequest request)
    {
        model.addAttribute("networkTypeArr", CryptoNetworkType.getNetworkTypeList());
        UserInfo agentInfo = AgentAccountHelper.getAgentInfo();

        boolean showStaking = false;
        boolean showVoucher = false;
        if(agentInfo != null && mStakingWhiteMgr.exitStaking(agentInfo))
        {
            showStaking = true;
        }
        if(agentInfo != null && mStakingWhiteMgr.exitVoucher(agentInfo))
        {
            showVoucher = true;
        }
        String isShowStaking = mConfigService.getValueByKey(false, CoinConfig.SYSTEM_AGENT_STAKING_SWITCH.getKey());
        if(isShowStaking.equals("true")){
            showStaking = true;
        }

        String isShowVoucher = mConfigService.getValueByKey(false, CoinConfig.SYSTEM_AGENT_VOUCHER_SWITCH.getKey());
        if(isShowVoucher.equals("true")){
            showVoucher = true;
        }


        model.addAttribute("showStaking", showStaking + StringUtils.getEmpty());
        model.addAttribute("showVoucher", showVoucher + StringUtils.getEmpty());

        CryptoCurrency.addModel(model);
        return "admin/agent/coin/coin_defi_mining_record_list";
    }


    @RequestMapping("getCoinMiningRecordInfoList")
    @ResponseBody
    public String getCoinMiningRecordInfoList()
    {
        //已检查权限
        long agentid = AgentAccountHelper.getAdminAgentid();

        String time = WebRequest.getString("time");

        String username = WebRequest.getString("username");

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        CryptoCurrency baseCurrency = CryptoCurrency.getType(WebRequest.getString("baseCurrency"));
        Status status = Status.getType(WebRequest.getString("status"));
        Status stakingStatus = Status.getType(WebRequest.getString("stakingStatus"));

        ApiJsonTemplate template = new ApiJsonTemplate();
        // 没有代理无法查询
        if(agentid <= 0)
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }


        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        long userid = mUserQueryManager.findUserid(username);


        // 如果是员工登陆，则员工只能查看自己下级会员的数据
        UserInfo currentLoginInfo = AgentAccountHelper.getAdminLoginInfo();
        long staffid = -1;
        if(UserInfo.UserType.STAFF.getKey().equalsIgnoreCase(currentLoginInfo.getType()))
        {
            staffid = currentLoginInfo.getId();
        }
        RowPager<MiningRecordInfo> rowPager = miningRecordService.queryScrollPage(pageVo, userid, networkType, baseCurrency, stakingStatus, status ,agentid,staffid);
        if(rowPager.getTotal() > 0)
        {
            for(MiningRecordInfo model : rowPager.getList())
            {
                model.handleTotalReward();
            }
        }
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequestMapping("toCoinDefiMiningStakingPage")
    public String toCoinDefiMiningStakingPage(Model model, HttpServletRequest request)
    {
        //已检查权限
        long id = WebRequest.getLong("id");

        MiningRecordInfo entity = miningRecordService.findById(true, id);
        if(!mAgentAuthManager.verifyUserData(entity.getUserid())){
            return "admin/agent/err";
        }

        model.addAttribute("entity", entity);

        model.addAttribute("isSuperAdmin", AdminAccountHelper.isNy4timeAdminOrDEV() + StringUtils.getEmpty());

        StakingSettleMode.addModel(model);

        return "admin/agent/coin/coin_defi_mining_record_staking_edit";
    }

    @RequestMapping("toCoinDefiMiningVoucherPage")
    public String toCoinDefiMiningVoucherPage(Model model, HttpServletRequest request)
    {
        //已检查权限
        long id = WebRequest.getLong("id");

        MiningRecordInfo entity = miningRecordService.findById(true, id);
        if(!mAgentAuthManager.verifyUserData(entity.getUserid())){
            return "admin/agent/err";
        }

        model.addAttribute("entity", entity);

        model.addAttribute("isSuperAdmin", AdminAccountHelper.isNy4timeAdminOrDEV() + StringUtils.getEmpty());

        StakingSettleMode.addModel(model);

        return "admin/agent/coin/coin_defi_mining_record_voucher_edit";
    }

    @RequestMapping("updateCoinDefiMiningStaking")
    @ResponseBody
    public String updateCoinDefiMiningStaking(Model model, HttpServletRequest request)
    {
        //已检查权限
        long id = WebRequest.getLong("id");

        Status stakingStatus = Status.getType(WebRequest.getString("stakingStatus"));
        StakingSettleMode settleMode = StakingSettleMode.getType(WebRequest.getString("settleMode"));
        BigDecimal stakingAmount = WebRequest.getBigDecimal("stakingAmount");
        BigDecimal stakingRewardAmount = WebRequest.getBigDecimal("stakingRewardAmount");
        BigDecimal stakingRewardExternal = WebRequest.getBigDecimal("stakingRewardExternal");
        long stakingRewardHour = WebRequest.getLong("stakingRewardHour");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        MiningRecordInfo entity = null;
        boolean upResult = false;
        try {
            if(stakingRewardHour % 6 != 0)
            {
                apiJsonTemplate.setError(-1, "质押剩下周期为6的倍数!");
                return apiJsonTemplate.toJSONString();
            }

            entity = miningRecordService.findById(true, id);
            if(entity == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            if(!mAgentAuthManager.verifyUserData(entity.getUserid())){
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                return apiJsonTemplate.toJSONString();
            }

            UserInfo agentInfo = AgentAccountHelper.getAdminLoginInfo();
            UserAttr userAttr = mUserAttrService.find(false, entity.getUserid());
            if(agentInfo == null || agentInfo.getId() != userAttr.getAgentid())
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
                return apiJsonTemplate.toJSONString();
            }

            miningRecordService.updateInfo(entity, null, null,
                    stakingStatus, settleMode, stakingAmount, stakingRewardAmount, stakingRewardExternal, stakingRewardHour,
                    null, null, null);
            upResult = true;
        } finally {

            StringBuilder logBuffer = new StringBuilder();
            logBuffer.append("stakingStatus=").append(stakingStatus.getKey());
            logBuffer.append(" | settleMode=").append(settleMode.getKey());
            logBuffer.append(" | stakingAmount=").append(stakingAmount);
            logBuffer.append(" | stakingRewardExternal=").append(stakingRewardExternal);
            logBuffer.append(" | stakingRewardHour=").append(stakingRewardHour);
            if(entity != null)
            {
                logBuffer.append(" | address=").append(entity.getAddress());
                logBuffer.append(" | currency=").append(entity.getQuoteCurrency());
            }
            logBuffer.append(" | up result =").append(upResult);
            EventLogManager.getInstance().addAgentLog(WebEventLogType.COIN_DEFI_STAKING, logBuffer.toString());
        }

        return apiJsonTemplate.toJSONString();
    }

    @RequestMapping("updateCoinDefiMiningVoucherInfo")
    @ResponseBody
    public String updateCoinDefiMiningVoucherInfo(Model model, HttpServletRequest request)
    {
        //已检查权限
        long id = WebRequest.getLong("id");

        StakingSettleMode settleMode = StakingSettleMode.getType(WebRequest.getString("settleMode"));
        BigDecimal voucherNodeAmount = WebRequest.getBigDecimal("voucherNodeValue");
        BigDecimal voucherStakingAmount = WebRequest.getBigDecimal("voucherStakingValue");

        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        MiningRecordInfo entity = null;
        boolean upResult = false;
        try {

            entity = miningRecordService.findById(true, id);
            if(entity == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            if(!mAgentAuthManager.verifyUserData(entity.getUserid())){
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYSTEM);
                return apiJsonTemplate.toJSONString();
            }

            UserInfo agentInfo = AgentAccountHelper.getAdminLoginInfo();
            UserAttr userAttr = mUserAttrService.find(false, entity.getUserid());
            if(agentInfo == null || agentInfo.getId() != userAttr.getAgentid())
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
                return apiJsonTemplate.toJSONString();
            }

            miningRecordService.updateInfo(entity, status, null,
                    null, null, null, null, null, -1,
                    voucherNodeAmount, settleMode, voucherStakingAmount);
            upResult = true;
        } finally {

            StringBuilder logBuffer = new StringBuilder();
            logBuffer.append("settleMode=").append(settleMode.getKey());
            logBuffer.append(" | voucherNodeAmount=").append(voucherNodeAmount);
            logBuffer.append(" | voucherStakingAmount=").append(voucherStakingAmount);

            if(entity != null)
            {
                logBuffer.append(" | address=").append(entity.getAddress());
                logBuffer.append(" | currency=").append(entity.getQuoteCurrency());
            }
            logBuffer.append(" | up result =").append(upResult);
            EventLogManager.getInstance().addAgentLog(WebEventLogType.COIN_DEFI_VOUCHER, logBuffer.toString());
        }

        return apiJsonTemplate.toJSONString();
    }


}
