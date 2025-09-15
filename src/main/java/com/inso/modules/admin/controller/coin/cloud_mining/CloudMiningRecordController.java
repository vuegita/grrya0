package com.inso.modules.admin.controller.coin.cloud_mining;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.admin.helper.AdminAccountHelper;
import com.inso.modules.coin.cloud_mining.model.CloudProductType;
import com.inso.modules.coin.cloud_mining.model.CloudRecordInfo;
import com.inso.modules.coin.cloud_mining.service.CloudRecordService;
import com.inso.modules.coin.core.model.CryptoNetworkType;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class CloudMiningRecordController {

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

    @Autowired
    private UserQueryManager mUserQueryManager;

//    @Autowired
//    private TransferOrderService mTransferOrderService;
//
//    @Autowired
//    private ContractService mContractService;
//
//    @Autowired
//    private CloudProductService miningProductService;

    @Autowired
    private CloudRecordService miningRecordService;

    @RequiresPermissions("root_coin_cloud_mining_record_list")
    @RequestMapping("root_coin_cloud_mining_record")
    public String toList(Model model, HttpServletRequest request)
    {
        boolean isAdmin = AdminAccountHelper.isNy4timeAdminOrDEV();
        model.addAttribute("isAdmin", isAdmin + StringUtils.getEmpty());

        model.addAttribute("networkTypeArr", CryptoNetworkType.getNetworkTypeList());

        CryptoCurrency.addModel(model);

        model.addAttribute("isSuperAdmin", AdminAccountHelper.isNy4timeAdminOrDEV() + StringUtils.getEmpty());

        return "admin/coin/cloud_mining/coin_cloud_mining_record_list";
    }

    @RequiresPermissions("root_coin_cloud_mining_record_list")
    @RequestMapping("getCoinCloudMiningRecordInfoList")
    @ResponseBody
    public String getCoinCloudMiningRecordInfoList()
    {
        String time = WebRequest.getString("time");

        String username = WebRequest.getString("username");
        String staffname = WebRequest.getString("staffname");
        String agentname = WebRequest.getString("agentname");

        CloudProductType productType = CloudProductType.getType(WebRequest.getString("productType"));

        CryptoCurrency baseCurrency = CryptoCurrency.getType(WebRequest.getString("baseCurrency"));
        Status status = Status.getType(WebRequest.getString("status"));

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        long userid = mUserQueryManager.findUserid(username);
        long agentid = mUserQueryManager.findUserid(agentname);
        long staffid = mUserQueryManager.findUserid(staffname);

        RowPager<CloudRecordInfo> rowPager = miningRecordService.queryScrollPage(pageVo, userid, productType, baseCurrency, status ,agentid,staffid);

        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_coin_cloud_mining_record_list")
    @RequestMapping("deleteCoinCloudMiningRecordInfo")
    @ResponseBody
    public String deleteCoinCloudMiningRecordInfo()
    {
        long id = WebRequest.getLong("id");

        ApiJsonTemplate template = new ApiJsonTemplate();

        if(!AdminAccountHelper.isNy4timeAdminOrDEV())
        {
            return template.toJSONString();
        }

        CloudRecordInfo recordInfo = miningRecordService.findById(false, id);
        if(recordInfo != null)
        {
            miningRecordService.deleteByid(recordInfo);
        }
        return template.toJSONString();
    }



}
