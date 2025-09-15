package com.inso.modules.admin.controller.coin.defi_mining;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.core.model.ContractInfo;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.coin.defi_mining.logical.MiningProductInit;
import com.inso.modules.coin.defi_mining.model.MiningProductInfo;
import com.inso.modules.coin.defi_mining.service.MiningProductService;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class DefiMiningProductController {

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
    private MiningProductService miningProductService;

    @Autowired
    private MiningProductInit mDeFiProductInit;

    @RequiresPermissions("root_coin_defi_mining_product_list")
    @RequestMapping("root_coin_defi_mining_product")
    public String toList(Model model, HttpServletRequest request)
    {
        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isSuperAdmin", isAdmin + StringUtils.getEmpty());

//        model.addAttribute("networkTypeArr", CryptoNetworkType.getNetworkTypeList());
        CryptoNetworkType.addFreemarkerModel(model);

        CryptoCurrency.addModel(model);

        return "admin/coin/coin_defi_mining_product_list";
    }

    @RequiresPermissions("root_coin_defi_mining_product_list")
    @RequestMapping("getCoinMiningProductInfoList")
    @ResponseBody
    public String getCoinMiningProductInfoList()
    {
        String time = WebRequest.getString("time");

        CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        CryptoCurrency quoteCurrency = CryptoCurrency.getType(WebRequest.getString("quoteCurrency"));
        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        RowPager<MiningProductInfo> rowPager = miningProductService.queryScrollPage(pageVo, networkType, quoteCurrency, status);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_coin_defi_mining_product_edit")
    @RequestMapping("toCoinMiningProductEditPage")
    public String toCoinMiningProductEditPage(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            MiningProductInfo entity = miningProductService.findById(false, id);
            model.addAttribute("entity", entity);
        }
        CryptoCurrency.addModel(model);

        model.addAttribute("networkTypeArr", CryptoNetworkType.getNetworkTypeList());
        return "admin/coin/coin_defi_mining_product_edit";
    }

    @RequiresPermissions("root_coin_defi_mining_product_edit")
    @RequestMapping("updateCoinMiningProductInfo")
    @ResponseBody
    public String updateCoinMiningProductInfo(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");

        long contractid = WebRequest.getLong("contractid");

        String name = WebRequest.getString("name");
        //CryptoNetworkType networkType = CryptoNetworkType.getType(WebRequest.getString("networkType"));

        CryptoCurrency baseCurrency = CryptoCurrency.getType(WebRequest.getString("baseCurrency"));
        //CryptoCurrency quoteCurrency = CryptoCurrency.getType(WebRequest.getString("quoteCurrency"));

        BigDecimal minWithdrawAmount = WebRequest.getBigDecimal("minWithdrawAmount");
        minWithdrawAmount = BigDecimal.ZERO;

        BigDecimal minWalletBalance = WebRequest.getBigDecimal("minWalletBalance");

        BigDecimal expectedRate = WebRequest.getBigDecimal("expectedRate");
        long networkTypeSort = WebRequest.getLong("networkTypeSort");
        long quoteCurrencySort = WebRequest.getLong("quoteCurrencySort");

        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(name))
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(baseCurrency == null || status == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if( minWalletBalance == null || expectedRate == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(expectedRate.compareTo(BigDecimal.ZERO) <= 0 || expectedRate.compareTo(BigDecimalUtils.DEF_1) >= 0)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "预期收益范围是 0 - 1 ");
            return apiJsonTemplate.toJSONString();
        }

        ContractInfo contractInfo = mContractService.findById(false, contractid);
        if(contractInfo == null || !contractInfo.getStatus().equalsIgnoreCase(Status.ENABLE.getKey()))
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "当前合约不存在或着未开启!");
            return apiJsonTemplate.toJSONString();
        }

        if(status == Status.ENABLE)
        {
            int maxSize = 100;
            List<MiningProductInfo> allList = miningProductService.queryAllList(false);
            if(allList != null && allList.size() >= maxSize)
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "最多只能启用" + maxSize + "个产品!");
                return apiJsonTemplate.toJSONString();
            }
        }

        if(id > 0)
        {
            MiningProductInfo productInfo = miningProductService.findById(false, id);
            miningProductService.updateInfo(productInfo, name, minWithdrawAmount, minWalletBalance, networkTypeSort, quoteCurrencySort, expectedRate, status);
        }
        else
        {
            miningProductService.add(contractInfo, name, baseCurrency, minWithdrawAmount, minWalletBalance, expectedRate, networkTypeSort, quoteCurrencySort, status);
        }
        return apiJsonTemplate.toJSONString();
    }


    @RequiresPermissions("root_coin_defi_mining_product_edit")
    @RequestMapping("batchCoinDeFiProductInfoList")
    @ResponseBody
    public String batchCoinDeFiProductInfoList()
    {
        ApiJsonTemplate template = new ApiJsonTemplate();
        mDeFiProductInit.init();
        return template.toJSONString();
    }


}
