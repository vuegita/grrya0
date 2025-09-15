package com.inso.modules.admin.controller.game;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.inso.framework.bean.ApiJsonTemplate;
import com.inso.framework.bean.PageVo;
import com.inso.framework.bean.SystemErrorResult;
import com.inso.framework.spring.web.WebRequest;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.game.fm.helper.FMHelper;
import com.inso.modules.game.fm.job.FMBeginJob;
import com.inso.modules.game.fm.logical.FMProductListManager;
import com.inso.modules.game.fm.model.FMProductInfo;
import com.inso.modules.game.fm.model.FMProductStatus;
import com.inso.modules.game.fm.model.FMType;
import com.inso.modules.game.fm.service.FMProductService;

/**
 * 理财产品
 */
@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class FMProductController {


    @Autowired
    private FMProductService mFMProductService;


    @RequiresPermissions("root_game_financial_management_product_list")
    @RequestMapping("root_game_financial_management_product")
    public String toList(Model model, HttpServletRequest request)
    {
        return "admin/game/game_fm_product_list";
    }

    @RequiresPermissions("root_game_financial_management_product_list")
    @RequestMapping("getGameFMProductList")
    @ResponseBody
    public String getGameFMProductList()
    {
        String time = WebRequest.getString("time");
        long id = WebRequest.getLong("id");
        long userid = WebRequest.getLong("userid");
        String statusSting = WebRequest.getString("status");

        FMProductStatus status = FMProductStatus.getType(statusSting);

        ApiJsonTemplate template = new ApiJsonTemplate();

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        if(!pageVo.parseTime(time))
        {
            template.setData(RowPager.getEmptyRowPager());
            return template.toJSONString();
        }

        RowPager<FMProductInfo> rowPager = mFMProductService.queryScrollPage(pageVo, id, userid, status);

        template.setData(rowPager);
        return template.toJSONString();
    }


    @RequiresPermissions("root_game_financial_management_product_edit")
    @RequestMapping("/game/fm_product/add/page")
    public String toCreateGameRedEnveloperPage(Model model, HttpServletRequest request)
    {
        long id = WebRequest.getLong("id");
        if(id > 0)
        {
            FMProductInfo productInfo = mFMProductService.findById(false, id);
            model.addAttribute("productInfo", productInfo);
        }
        return "admin/game/game_fm_product_edit";
    }

    @RequiresPermissions("root_game_financial_management_product_edit")
    @RequestMapping("/game/fm_product/edit")
    @ResponseBody
    public String editProductInfo()
    {
        long id = WebRequest.getLong("id");
        String title = WebRequest.getString("title");
        String desc = WebRequest.getString("desc");

        int time_horizon = WebRequest.getInt("time_horizon");
        int afterCreatetime = WebRequest.getInt("afterCreatetime");

        FMType fmType = FMType.SIMPLE;

        BigDecimal return_expected_start = WebRequest.getBigDecimal("return_expected_start");
        BigDecimal return_expected_end = WebRequest.getBigDecimal("return_expected_end");
        BigDecimal return_real_rate = WebRequest.getBigDecimal("return_real_rate");
        if(return_real_rate == null || return_real_rate.compareTo(BigDecimal.ZERO) <= 0)
        {
            // 计算实际收益率
            return_real_rate = FMHelper.calcReturnRealRate(return_expected_start, return_expected_end);
        }

        long sale_estimate = WebRequest.getLong("sale_estimate");

        long limit_min_sale = WebRequest.getLong("limit_min_sale");
        long limit_max_sale = WebRequest.getLong("limit_max_sale");
        long limit_min_bets = WebRequest.getLong("limit_min_bets");
        BigDecimal limit_min_balance = WebRequest.getBigDecimal("limit_min_balance");

        String statusString = WebRequest.getString("status");
        FMProductStatus status = FMProductStatus.getType(statusString);

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(StringUtils.isEmpty(title) || title.length() > 20)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "err title");
            return apiJsonTemplate.toJSONString();
        }

        if(StringUtils.isEmpty(desc) || desc.length() > 50)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "err desc");
            return apiJsonTemplate.toJSONString();
        }


        if(return_expected_start == null || return_expected_start.compareTo(BigDecimal.ZERO) <= 0)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "ERR 预期最小收益率");
            return apiJsonTemplate.toJSONString();
        }

        if(return_expected_end == null || return_expected_end.compareTo(BigDecimal.ZERO) <= 0)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "ERR 预期最大收益率");
            return apiJsonTemplate.toJSONString();
        }

        if(return_expected_start.compareTo(return_expected_end) >= 0)
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "最大收益率必须大于最小收益率");
            return apiJsonTemplate.toJSONString();
        }

        if(!(return_expected_start.compareTo(return_real_rate) <= 0 && return_expected_end.compareTo(return_real_rate) >= 0))
        {
            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "实际收益率必须在最大收益率和最小收益率之间!");
            return apiJsonTemplate.toJSONString();
        }

        if(sale_estimate <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(limit_min_sale <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(limit_max_sale <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(limit_min_bets < 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(limit_min_balance == null ||  limit_min_balance.compareTo(BigDecimal.ZERO) < 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        if(id <= 0)
        {
            if(!(time_horizon == 1 || time_horizon == 3 || time_horizon == 7))
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "err time_horizon");
                return apiJsonTemplate.toJSONString();
            }

            if(!(afterCreatetime == 5 || afterCreatetime == 10 || afterCreatetime == 30 || afterCreatetime == 60 || afterCreatetime == 120))
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "err createtime");
                return apiJsonTemplate.toJSONString();
            }

            DateTime nowTime = new DateTime();
            nowTime = nowTime.withSecondOfMinute(0);

            DateTime beginSaleTime = nowTime.plusMinutes(afterCreatetime);
            DateTime endSaleTime = beginSaleTime.plusDays(1);// 销售时长 1天

            mFMProductService.add(title.trim(), desc.trim(), time_horizon, fmType,
                    return_expected_start, return_expected_end, return_real_rate,
                    sale_estimate, sale_estimate,
                    limit_min_sale, limit_max_sale, limit_min_bets, limit_min_balance,
                    beginSaleTime, endSaleTime);
        }
        else
        {
            if(status == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return apiJsonTemplate.toJSONString();
            }

            FMProductInfo productInfo = mFMProductService.findById(false, id);
            if(productInfo == null)
            {
                apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return apiJsonTemplate.toJSONString();
            }

            FMProductStatus dbStatus = FMProductStatus.getType(productInfo.getStatus());
            if(dbStatus == FMProductStatus.DISCARD || dbStatus == FMProductStatus.REALIZED)
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "状态已为最终状态，无法更新!");
                return apiJsonTemplate.toJSONString();
            }

            if(dbStatus == FMProductStatus.SALING && FMProductListManager.isSalingFull())
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "开售产品最多只能100个!");
                return apiJsonTemplate.toJSONString();
            }

            // 最近2分钟内不能更新
            long countdwonSeconds = productInfo.getEndtime().getTime() - System.currentTimeMillis();
            if(countdwonSeconds > 0 && countdwonSeconds <= 120 * 1000)
            {
                apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "最后2分钟为锁定期!");
                return apiJsonTemplate.toJSONString();
            }

            mFMProductService.updateBasicInfo(id, title.trim(), desc.trim(), return_real_rate, status);


        }

        return apiJsonTemplate.toJSONString();
    }

    @RequiresPermissions("root_game_financial_management_product_edit")
    @RequestMapping("/game/fm_product/reSettle")
    @ResponseBody
    public String reSettle()
    {
        long issue = WebRequest.getLong("issue");

        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(issue <=0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

       // FMProductInfo model = mFMProductService.findById(false, issue);

        // 最近2分钟内不能更新
//        long countdwonSeconds = model.getEndtime().getTime() - System.currentTimeMillis();
//        if(countdwonSeconds > 120 * 1000)
//        {
//            apiJsonTemplate.setError(SystemErrorResult.ERR_CUSTOM.getCode(), "只能结算2分钟之前!");
//            return apiJsonTemplate.toJSONString();
//        }

        FMBeginJob.sendMessage(issue + StringUtils.getEmpty());

        return apiJsonTemplate.toJSONString();
    }


}
