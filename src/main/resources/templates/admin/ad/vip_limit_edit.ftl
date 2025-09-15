<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-用户管理</title>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>添加配置</h5>
            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 今日总上限 <= VIP价格 / 30 !</p>
            <p  style="color: green">2. 免费额度 <= 今日总上限 * 0.9, 默认为 = 今日总上限 * 0.7 !</p>
            <p  style="color: green">3. 单笔最大金额 <= 今日总上限 * 0.2, 最大不能超过200 !</p>
            <p  style="color: green">4. 当日总金额 = 免费额度 + 邀请额度 + 购买VIP额度! !</p>
            <p  style="color: green">5. 返提现范围: 0% ~ 50% !</p>

        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.id}</#if>"/>

                <#if entity?exists>
                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>VIP名称:</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="vipName" id="vipName" value="<#if entity?exists> ${entity.vipName} </#if>" readonly autocomplete="off" required maxlength="50"/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>VIP等级:</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="vipLevel" id="vipLevel" value="<#if entity?exists> ${entity.vipLevel} </#if>" readonly autocomplete="off" required maxlength="50"/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <input type="hidden" name="vipid" id="vipid" value="<#if entity?exists>${entity.vipid}</#if>"/>
                </#if>

                <#if !entity?exists>
                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>VIP_ID:</label>
                        <div class="col-sm-3">
                            <select class="form-control" name="vipid" >
                                <#if vipInfoList?exists && (vipInfoList?size > 0) >
                                    <#list vipInfoList as item>
                                        <option value="${item.id}"
                                                <#if entity?exists && item.id == entity.vipid>selected</#if> >${item.name}
                                        </option>
                                    </#list>
                                <#else>
                                    <option disabled> 请先创建VIP </option>
                                </#if>

                            </select>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>
                </#if>

                <#if entity?exists>
                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>回本周期(天):</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="paybackPeriod" id="paybackPeriod"
                                   onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                   value="<#if entity?exists> ${entity.paybackPeriod} </#if>" autocomplete="off" required maxlength="100"/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>今日总上限:</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="totalMoneyOfDay" id="totalMoneyOfDay"
                                   onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');" <#if entity?exists && (entity.vipLevel > 0)> readonly </#if>
                                   value="<#if entity?exists> ${entity.totalMoneyOfDay} </#if>" autocomplete="off" required maxlength="100"/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>今日免费额度:</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="freeMoneyOfDay" id="freeMoneyOfDay"
                                   onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                   value="<#if entity?exists> ${entity.freeMoneyOfDay} </#if>" autocomplete="off" required maxlength="100"/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>单笔最大金额:</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="maxMoneyOfSingle" id="maxMoneyOfSingle"
                                   onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                   value="<#if entity?exists> ${entity.maxMoneyOfSingle} </#if>" autocomplete="off" required maxlength="100"/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>邀请好友个数:</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="inviteCountOfDay" id="inviteCountOfDay"
                                   onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                   value="<#if entity?exists> ${entity.inviteCountOfDay} </#if>" autocomplete="off" required maxlength="100"/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>邀请好友额度(自动生成):</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="inviteMoneyOfDay" id="inviteMoneyOfDay"
                                   onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');" readonly
                                   value="<#if entity?exists> ${entity.inviteMoneyOfDay} </#if>" autocomplete="off" required maxlength="100"/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

<#--                    <div class="form-group">-->
<#--                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>强制购买VIP个数:</label>-->
<#--                        <div class="col-sm-3">-->
<#--                            <input class="form-control" name="buyCountOfDay" id="buyCountOfDay"-->
<#--                                   onkeyup="this.value=this.value.replace(/[^\d]/g,'');"-->
<#--                                   value="<#if entity?exists> ${entity.buyCountOfDay} </#if>" autocomplete="off" required maxlength="100"/>-->
<#--                        </div>-->
<#--                    </div>-->
<#--                    <div class="hr-line-dashed"></div>-->

                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>成功邀请好友并购买VIP额度/每个:</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="buyMoneyOfDay" id="buyMoneyOfDay"
                                   onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                   value="<#if entity?exists> ${entity.buyMoneyOfDay} </#if>" autocomplete="off" required maxlength="100"/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>一级返佣比例(%):</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="lv1RebateBalanceRate" id="lv1RebateBalanceRate"
                                   onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                   value="<#if entity?exists> ${entity.lv1RebateBalanceRate!} </#if>" autocomplete="off" required maxlength="100"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>二级返佣比例(%):</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="lv2RebateBalanceRate" id="lv2RebateBalanceRate"
                                   onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                   value="<#if entity?exists> ${entity.lv2RebateBalanceRate!} </#if>" autocomplete="off" required maxlength="100"/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>返提现额度比例(%):</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="lv1RebateWithdrawlRate" id="lv1RebateWithdrawlRate"
                                   onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                   value="<#if entity?exists> ${entity.lv1RebateWithdrawlRate!} </#if>" autocomplete="off" required maxlength="100"/>
                        </div>
                    </div>

<#--                    <div class="form-group">-->
<#--                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>二级返提现额度比例(%):</label>-->
<#--                        <div class="col-sm-3">-->
<#--                            <input class="form-control" name="lv2RebateWithdrawlRate" id="lv2RebateWithdrawlRate"-->
<#--                                   onkeyup="this.value=this.value.replace(/[^\d]/g,'');"-->
<#--                                   value="<#if entity?exists> ${entity.lv2RebateWithdrawlRate} </#if>" autocomplete="off" required maxlength="100"/>-->
<#--                        </div>-->
<#--                    </div>-->
<#--                    <div class="hr-line-dashed"></div>-->

                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>状态:</label>
                        <div class="col-sm-3">
                            <div class="radio i-checks">
                                <label>
                                    <input type="radio"  name="status" id="status" value="enable"
                                            <#if entity?exists && entity.status == 'enable'> checked </#if>/>
                                    <i></i>启用
                                </label>

                                <label><input type="radio"  name="status" id="status" value="disable"
                                            <#if entity?exists && entity.status == 'disable'> checked </#if>/>
                                    <i></i>禁用
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                </#if>



                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="button" id="submitBtn" value="保存"/>
                        <button class="btn btn-white" type="button" onclick="window.close();">取消</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js"></script>
<script type="text/javascript">
    $(function () {


        $('.usertypeLabel').on("click",function() {
            var input = $(this).find("input");

            var userType = $(input).data('usertype');
            if(userType == 'staff')
            {
                $("#agentNameDIV").show();
            }
            else
            {
                $("#agentNameDIV").hide();
            }
        });


        //确认保存
        $("#submitBtn").click(function () {
            var id = $('input[name="id"]').val();

            var vipid = $('select[name="vipid"]').val();


            var paybackPeriod = $('input[name="paybackPeriod"]').val();
            var totalMoneyOfDay = $('input[name="totalMoneyOfDay"]').val();
            var freeMoneyOfDay = $('input[name="freeMoneyOfDay"]').val();
            var maxMoneyOfSingle = $('input[name="maxMoneyOfSingle"]').val();
            var inviteCountOfDay = $('input[name="inviteCountOfDay"]').val();
            var inviteMoneyOfDay = $('input[name="inviteMoneyOfDay"]').val();
            var buyCountOfDay = $('input[name="buyCountOfDay"]').val();
            var buyMoneyOfDay = $('input[name="buyMoneyOfDay"]').val();


            var lv1RebateBalanceRate = $('input[name="lv1RebateBalanceRate"]').val();
            var lv2RebateBalanceRate = $('input[name="lv2RebateBalanceRate"]').val();
            var lv1RebateWithdrawlRate = $('input[name="lv1RebateWithdrawlRate"]').val();
            var lv2RebateWithdrawlRate = $('input[name="lv2RebateWithdrawlRate"]').val();

            var forceBuyVip = $('input[name="forceBuyVip"]:checked').val();
            var status = $('input[name="status"]:checked').val();

            if(isEmpty(vipid))
            {
                vipid = $('input[name="vipid"]').val();
            }

            // if ( isEmpty(name) || isEmpty(status) ){
            //     $.global.openErrorMsg('* 号必填参数不能为空');
            //     return;
            // }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/editAdVipLimitInfo",
                data: {
                    id:id,
                    vipid: vipid,
                    paybackPeriod:paybackPeriod,
                    totalMoneyOfDay:totalMoneyOfDay,
                    freeMoneyOfDay:freeMoneyOfDay,
                    maxMoneyOfSingle:maxMoneyOfSingle,
                    inviteCountOfDay:inviteCountOfDay,
                    inviteMoneyOfDay:inviteMoneyOfDay,
                    buyCountOfDay:buyCountOfDay,
                    buyMoneyOfDay:buyMoneyOfDay,
                    forceBuyVip:forceBuyVip,
                    lv1RebateBalanceRate:lv1RebateBalanceRate,
                    lv2RebateBalanceRate:lv2RebateBalanceRate,
                    lv1RebateWithdrawlRate:lv1RebateWithdrawlRate,
                    lv2RebateWithdrawlRate:lv2RebateWithdrawlRate,
                    status:status,
                },
                dataType: "json",
                success: function (data) {
                    if (data != null && data.code == 200) {
                        $.global.openSuccessMsg("保存成功",function(){
                            window.close();
                        });
                    } else {
                        $.global.openErrorMsg(data.msg);
                    }
                },
                error: function () {
                    $.global.openErrorMsg('保存失败，请重试');
                }
            })
        });
    });

</script>
</body>
</html>
