package com.inso.modules.admin.controller.basic;


import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.web.model.VIPInfo;
import com.inso.modules.web.model.VIPType;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.money.service.UserMoneyService;
import com.inso.modules.passport.user.service.UserService;
import com.inso.modules.web.service.VIPService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;


@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class VIPController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private UserMoneyService mUserMoneyService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @Autowired
    private VIPService mVIPService;

    @RequiresPermissions("root_vip_config_list")
    @RequestMapping("root_vip_config")
    public String toPage(Model model)
    {
        VIPType[] vipTypeList = VIPType.values();
        model.addAttribute("vipTypeList", vipTypeList);
        return "admin/basic/vip_list";
    }

    @RequiresPermissions("root_vip_config_list")
    @RequestMapping("getRootVIPConfigList")
    @ResponseBody
    public String getRootVIPConfigList()
    {
        String statusStr = WebRequest.getString("status");
        Status status = Status.getType(statusStr);

        String vipTypeStr = WebRequest.getString("vipType");
        VIPType vipType = VIPType.getType(vipTypeStr);

        ApiJsonTemplate template = new ApiJsonTemplate();
        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));


        RowPager<VIPInfo> rowPager = mVIPService.queryScrollPage(pageVo, vipType, status);
        template.setData(rowPager);
        return template.toJSONString();
    }

    @RequiresPermissions("root_vip_config_edit")
    @RequestMapping("toEditRootVIPConfigPage")
    public String toEditRootVIPConfigPage(Model model)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            VIPInfo vipInfo = mVIPService.findById(false, id);
            model.addAttribute("entity", vipInfo);
        }

        VIPType[] vipTypeList = VIPType.values();
        model.addAttribute("vipTypeList", vipTypeList);
        return "admin/basic/vip_edit";
    }

    @RequiresPermissions("root_vip_config_edit")
    @RequestMapping("editRootVIPConfigInfo")
    @ResponseBody
    public String editRootVIPConfigInfo()
    {
        long id = WebRequest.getLong("id");

        String statusStr = WebRequest.getString("status");
        Status status = Status.getType(statusStr);

        String vipTypeStr = WebRequest.getString("type");
        VIPType vipType = VIPType.getType(vipTypeStr);

        String name = WebRequest.getString("name");

        // 价格
        BigDecimal price = WebRequest.getBigDecimal("price");

        long level = WebRequest.getLong("level");


        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(vipType == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(id > 0)
        {
//            if(level<1){
//                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
//                return apiJsonTemplate.toJSONString();
//            }

            if(StringUtils.isEmpty(name))
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            if(status == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            if(level < 1)
            {
                price = BigDecimal.ZERO;
            }
            //vip0价格为0时可以修改
            else if(price.compareTo(BigDecimal.ZERO) < 0)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            VIPInfo vipInfo = mVIPService.findById(false, id);
            if(vipInfo.getLevel() > 0 && price.compareTo(BigDecimal.ZERO) <= 0)
            {
                // VIP等级 > 0, 价格不能设置为0
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            mVIPService.updateInfo(vipInfo, status, name, price, level);
        }
        else
        {
            level = mVIPService.findMaxLevel(vipType);
            level += 1;
            name = "VIP" + level;
            price = BigDecimalUtils.getNotNull(price);
            mVIPService.addVIPLevel(vipType, level, name, price);
        }
        return apiJsonTemplate.toJSONString();
    }



}
