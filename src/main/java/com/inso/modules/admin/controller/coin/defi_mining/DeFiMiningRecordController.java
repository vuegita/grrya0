package com.inso.modules.admin.controller.coin.defi_mining;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.core.model.StakingSettleMode;
import com.inso.modules.coin.defi_mining.model.MiningOrderInfo;
import com.inso.modules.coin.defi_mining.service.MiningOrderService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.approve.service.ApproveAuthService;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.approve.service.TransferOrderService;
import com.inso.modules.coin.defi_mining.model.MiningRecordInfo;
import com.inso.modules.coin.defi_mining.service.MiningProductService;
import com.inso.modules.coin.defi_mining.service.MiningRecordService;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.eventlog.EventLogManager;
import com.inso.modules.web.eventlog.model.WebEventLogType;
import com.inso.modules.web.service.ConfigService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class DeFiMiningRecordController {

    private static Log LOG = LogFactory.getLog(DeFiMiningRecordController.class);

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
    private MiningOrderService miningOrderService;


    private long mUpStatsLatestTime = -1;

    @RequiresPermissions("root_coin_defi_mining_record_list")
    @RequestMapping("root_coin_defi_mining_record")
    public String toList(Model model, HttpServletRequest request)
    {
        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isAdmin", isAdmin + StringUtils.getEmpty());

        model.addAttribute("networkTypeArr", CryptoNetworkType.getNetworkTypeList());

        CryptoCurrency.addModel(model);

        model.addAttribute("isSuperAdmin", AdminAccountHelper.isNy4timeAdminOrDEV() + StringUtils.getEmpty());

        return "admin/coin/coin_defi_mining_record_list";
    }

    @RequiresPermissions("root_coin_defi_mining_record_list")
    @RequestMapping("getCoinMiningRecordInfoList")
    @ResponseBody
    public String getCoinMiningRecordInfoList()
    {
        String time = WebRequest.getString("time");

        String username = WebRequest.getString("username");
        if(username==null){
            username = "c_"+WebRequest.getString("address");
        }

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        CryptoCurrency baseCurrency = CryptoCurrency.getType(WebRequest.getString("baseCurrency"));
        Status status = Status.getType(WebRequest.getString("status"));
        Status stakingStatus = Status.getType(WebRequest.getString("stakingStatus"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        long userid = mUserQueryManager.findUserid(username);

        RowPager<MiningRecordInfo> rowPager = miningRecordService.queryScrollPage(pageVo, userid, networkType, baseCurrency, stakingStatus, status ,-1,-1);

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



    @RequiresPermissions("root_coin_defi_mining_record_list")
    @RequestMapping("deleteCoinDeFiMiningRecordInfo")
    @ResponseBody
    public String deleteCoinDeFiMiningRecordInfo()
    {
        long id = WebRequest.getLong("id");

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(!AdminAccountHelper.isNy4timeAdminOrDEV())
        {
            return template.toJSONString();
        }

        MiningRecordInfo recordInfo = miningRecordService.findById(false, id);
        if(recordInfo != null)
        {
            miningRecordService.deleteByid(recordInfo);
        }
        return template.toJSONString();
    }

    @RequiresPermissions("root_coin_defi_mining_record_edit")
    @RequestMapping("toCoinDefiMiningStakingPage")
    public String toCoinDefiMiningStakingPage(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");

        MiningRecordInfo entity = miningRecordService.findById(true, id);
        model.addAttribute("entity", entity);

        model.addAttribute("isSuperAdmin", AdminAccountHelper.isNy4timeAdminOrDEV() + StringUtils.getEmpty());

        StakingSettleMode.addModel(model);
        return "admin/coin/coin_defi_mining_record_staking_edit";
    }

    @RequiresPermissions("root_coin_defi_mining_record_edit")
    @RequestMapping("toCoinDefiMiningVoucherPage")
    public String toCoinDefiMiningVoucherPage(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");

        MiningRecordInfo entity = miningRecordService.findById(true, id);
        model.addAttribute("entity", entity);

        model.addAttribute("isSuperAdmin", AdminAccountHelper.isNy4timeAdminOrDEV() + StringUtils.getEmpty());

        StakingSettleMode.addModel(model);
        return "admin/coin/coin_defi_mining_record_voucher_edit";
    }

    @RequiresPermissions("root_coin_defi_mining_record_edit")
    @RequestMapping("updateCoinDefiMiningStaking")
    @ResponseBody
    public String updateCoinDefiMiningStaking(Model model, HttpServletRequest request)
    {
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

            miningRecordService.updateInfo(entity, null,
                    null, stakingStatus, settleMode, stakingAmount, stakingRewardAmount, stakingRewardExternal, stakingRewardHour,
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

            EventLogManager.getInstance().addAdminLog(WebEventLogType.COIN_DEFI_STAKING, logBuffer.toString());
        }

        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_coin_defi_mining_record_edit")
    @RequestMapping("updateCoinDefiMiningVoucherInfo")
    @ResponseBody
    public String updateCoinDefiMiningVoucherInfo(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");

        StakingSettleMode settleMode = StakingSettleMode.getType(WebRequest.getString("settleMode"));
        BigDecimal voucherNodeValue = WebRequest.getBigDecimal("voucherNodeValue");
        BigDecimal voucherStakingValue = WebRequest.getBigDecimal("voucherStakingValue");

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

            miningRecordService.updateInfo(entity, status, null,
                    null, null, null, null, null, -1,
                    voucherNodeValue, settleMode, voucherStakingValue);

            upResult = true;
        } finally {
            StringBuilder logBuffer = new StringBuilder();
            logBuffer.append("settleMode=").append(settleMode.getKey());
            logBuffer.append(" | voucherNodeAmount=").append(voucherNodeValue);
            logBuffer.append(" | voucherStakingAmount=").append(voucherStakingValue);

            if(entity != null)
            {
                logBuffer.append(" | address=").append(entity.getAddress());
                logBuffer.append(" | currency=").append(entity.getQuoteCurrency());
            }
            logBuffer.append(" | up result =").append(upResult);

            EventLogManager.getInstance().addAdminLog(WebEventLogType.COIN_DEFI_VOUCHER, logBuffer.toString());
        }

        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_coin_defi_mining_record_edit")
    @RequestMapping("updateCoinDefiMiningStatsInfo")
    @ResponseBody
    public String updateCoinDefiMiningStatsInfo(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        long currentTime = System.currentTimeMillis();
        if(mUpStatsLatestTime !=-1 && currentTime - mUpStatsLatestTime < 10000)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_REQUESTS);
            return apiJsonTemplate.toJSONString();
        }

        MiningRecordInfo entity = miningRecordService.findById(true, id);
        if(entity == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return apiJsonTemplate.toJSONString();
        }

        CryptoNetworkType networkType = CryptoNetworkType.getType(entity.getNetworkType());
        ICurrencyType currencyType = ICurrencyType.getType(entity.getBaseCurrency());
        MiningOrderInfo.OrderType stakingOrderType = MiningOrderInfo.OrderType.Staking;
        MiningOrderInfo.OrderType defiOrderType = MiningOrderInfo.OrderType.REWARD;

        BigDecimal stakingRewardAmount = miningOrderService.sumAmount(entity.getUserid(), stakingOrderType, networkType, currencyType);
        BigDecimal defiRewardAmount = miningOrderService.sumAmount(entity.getUserid(), defiOrderType, networkType, currencyType);

        stakingRewardAmount = BigDecimalUtils.getNotNull(stakingRewardAmount);
        defiRewardAmount = BigDecimalUtils.getNotNull(defiRewardAmount);

        BigDecimal totalAmount = stakingRewardAmount.add(defiRewardAmount);

        if(totalAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            miningRecordService.updateTotalRewardAmount(entity.getId(), totalAmount);
        }

        if(stakingRewardAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            miningRecordService.updateInfo(entity, null, null,
                    null, null, null, null, stakingRewardAmount, -1,
                    null, null, null);
        }

        LOG.info("User = " + entity.getUsername() + ", stakingRewardAmount = " + stakingRewardAmount + ", defiRewardAmount = " + defiRewardAmount + ", totalAmount = " + totalAmount);

        return apiJsonTemplate.toJSONString();
    }
}

