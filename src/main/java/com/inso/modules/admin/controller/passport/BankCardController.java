package com.inso.modules.admin.controller.passport;


import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.inso.modules.common.model.FiatCurrencyType;
import com.inso.modules.common.model.ICurrencyType;
import com.inso.modules.common.model.RemarkVO;
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
import com.inso.framework.utils.CollectionUtils;
import com.inso.framework.utils.RegexUtils;
import com.inso.framework.utils.RowPager;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.common.model.Status;
import com.inso.modules.passport.UserErrorResult;
import com.inso.modules.passport.user.logical.UserQueryManager;
import com.inso.modules.passport.business.model.BankCard;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.business.service.CardService;
import com.inso.modules.passport.user.service.UserService;

@Controller
@RequestMapping("/alibaba888/Liv2sky3soLa93vEr62")
public class BankCardController {

    @Autowired
    private UserService mUserService;

    @Autowired
    private CardService mCardService;

    @Autowired
    private UserQueryManager mUserQueryManager;

    @RequiresPermissions("root_passport_user_bank_list")
    @RequestMapping("root_passport_user_bank")
    public String toBankCardPage(Model model)
    {
        return "admin/passport/user_bank_card_list";
    }

    @RequiresPermissions("root_passport_user_bank_list")
    @RequestMapping("getUserBankCardList")
    @ResponseBody
    public String getUserBankCardList()
    {
        String time = WebRequest.getString("time");
        String username = WebRequest.getString("username");

        String cardTypeString = WebRequest.getString("cardType");
//        String statusString = WebRequest.getString("status");


        ApiJsonTemplate template = new ApiJsonTemplate();

        BankCard.CardType cardType = BankCard.CardType.getType(cardTypeString);

        PageVo pageVo = new PageVo(WebRequest.getInt("offset"), WebRequest.getInt("limit"));
        pageVo.parseTime(time);

        long userid = mUserQueryManager.findUserid(username);

        RowPager<BankCard> rowPager = mCardService.queryScrollPage(pageVo, userid, null, null);
        template.setData(rowPager);

        return template.toJSONString();
    }

    @RequiresPermissions("root_passport_user_bank_edit")
    @RequestMapping("toAddUserBankCardPage")
    public String toAddBankCardPage(Model model)
    {
        long cardid = WebRequest.getLong("cardid");
        if(cardid > 0)
        {
            BankCard card = mCardService.findByCardid(false, cardid);
            String remark=card.getRemark();

            if(!StringUtils.isEmpty(remark)){
                String idcard=JSONObject.parseObject(remark).getString("idcard");
                if(!StringUtils.isEmpty(idcard)){
                    card.setRemark(idcard);
                }

            }

            if(card != null)
            {
                model.addAttribute("cardInfo", card);
            }
        }
        return "admin/passport/user_bank_card_add";
    }

    @RequiresPermissions("root_passport_user_bank_edit")
    @RequestMapping("addUserBankCardPage")
    @ResponseBody
    public String addBankCardPage()
    {
        long cardid = WebRequest.getLong("cardid");
        String username = WebRequest.getString("username");
        String cardName = WebRequest.getString("cardName");
        String account = WebRequest.getString("account");
        String ifsc = WebRequest.getString("ifsc");
        BankCard.WalletSubType walletSubType = BankCard.WalletSubType.getType(ifsc);

        String typeString = WebRequest.getString("type");
        String beneficiaryName = WebRequest.getString("beneficiaryName");
        String beneficiaryEmail = WebRequest.getString("beneficiaryEmail");
        String beneficiaryPhone = WebRequest.getString("beneficiaryPhone");

        String idcard = WebRequest.getString("idcard");
        String statusString = WebRequest.getString("status");

        ApiJsonTemplate template = new ApiJsonTemplate();

        BankCard.CardType cardType = BankCard.CardType.getType(typeString);
        Status status = Status.getType(statusString);

        if(cardType == null || status == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(cardName) || !RegexUtils.isLetterOrDigitOrBottomLine(cardName))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(account) || account.length() > 100)
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }
        ICurrencyType currencyType = ICurrencyType.getSupportCurrency();

        if(cardType == BankCard.CardType.BANK)
        {
            if(!RegexUtils.isDigit(account))
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }

//            if(StringUtils.isEmpty(ifsc) || ifsc.length() != 11 || !RegexUtils.isLetterDigit(ifsc))
//            {
//                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
//                return template.toJSONString();
//            }
            if (  currencyType == FiatCurrencyType.INR && (StringUtils.isEmpty(ifsc) || ifsc.length() != 11 || !RegexUtils.isLetterDigit(ifsc))) {
                template.setJsonResult(UserErrorResult.ERR_BANK_IFSC);
                return template.toJSONString();
            }

        }
        else if(cardType == BankCard.CardType.UPI)
        {
            if(!RegexUtils.isUPI(account))
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }
            ifsc = null;
        }

        if(StringUtils.isEmpty(beneficiaryName))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(beneficiaryEmail) || !RegexUtils.isEmail(beneficiaryEmail))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        if(StringUtils.isEmpty(beneficiaryPhone) || !RegexUtils.isMobile(beneficiaryPhone))
        {
            template.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return template.toJSONString();
        }

        // wallet, check idcard
        if(cardType == BankCard.CardType.WALLET)
        {
            if( walletSubType == null)
            {
                template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                return template.toJSONString();
            }

            // COP下必须是要有这个
            if(currencyType == FiatCurrencyType.COP)
            {
                if(StringUtils.isEmpty(idcard) || !RegexUtils.isLetterDigit(idcard))
                {
                    template.setJsonResult(SystemErrorResult.ERR_PARAMS);
                    return template.toJSONString();
                }
            }
        }


        UserInfo userInfo = mUserService.findByUsername(false, username);
        if(userInfo == null)
        {
            template.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
            return template.toJSONString();
        }

        RemarkVO remarkVO = null;
        if(!StringUtils.isEmpty(idcard) && RegexUtils.isLetterDigit(idcard))
        {
            remarkVO = RemarkVO.create(null);
            remarkVO.put("idcard", idcard);
        }
        if(cardid > 0)
        {
            BankCard bankCard = mCardService.findByCardid(false, cardid);
            if(bankCard == null)
            {
                template.setJsonResult(SystemErrorResult.ERR_EXIST_NOT);
                return template.toJSONString();
            }

            mCardService.updateAccountInfo(bankCard, account, ifsc,cardType,remarkVO);
            ICurrencyType bankCardCurrencyType = ICurrencyType.getType(bankCard.getCurrencyType());
            mCardService.updateBeneficiaryInfo(bankCard, beneficiaryName, beneficiaryEmail, beneficiaryPhone,bankCardCurrencyType);
            mCardService.updateStatus(bankCard, status);

        }
        else
        {
            List<BankCard> list = mCardService.queryListByUserid(false, userInfo.getId());
            if(!CollectionUtils.isEmpty(list) && list.size() > BankCard.DEFAULT_MAX_ADD_CARD_SIZE)
            {
                template.setJsonResult(UserErrorResult.ERR_ADD_CARD_SIZE_LIMIT);
                return template.toJSONString();
            }

            //mCardService.addCard(userInfo.getId(), username, cardType, cardName, ifsc, account, beneficiaryName, beneficiaryEmail, beneficiaryPhone, remarkVO);
        }

        return template.toJSONString();
    }

    @RequiresPermissions("root_passport_user_bank_delete")
    @RequestMapping("deleteUserCardInfo")
    @ResponseBody
    public String deleteUserCardInfo()
    {
        long cardid = WebRequest.getLong("cardid");
        ApiJsonTemplate apiJsonTemplate = new ApiJsonTemplate();

        if(cardid <= 0)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        BankCard cardInfo = mCardService.findByCardid(false, cardid);
        if(cardInfo == null)
        {
            apiJsonTemplate.setJsonResult(SystemErrorResult.ERR_PARAMS);
            return apiJsonTemplate.toJSONString();
        }

        mCardService.deleteCardInfo(cardInfo);

        return apiJsonTemplate.toJSONString();

    }

}
