<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-用户管理</title>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>代金卷配置</h5>

            <p style="color: blue"> 　</p>
<#--            <p  style="color: green">1. 返利周期指剩于可返利周期，1周期=6小时!</p>-->
<#--            <p  style="color: green">2. Gas手续费限制设置!</p>-->
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.id}</#if>"/>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属用户:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="username" id="username" value="<#if entity?exists> ${entity.username} </#if>"
                               readonly
                               autocomplete="off" required maxlength="100"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属币种:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="baseCurrency" id="baseCurrency" value="<#if entity?exists> ${entity.baseCurrency} </#if>"
                               readonly
                               autocomplete="off" required maxlength="100"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">DeFi代金总额:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="voucherNodeValue" id="voucherNodeValue" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity?exists> ${entity.voucherNodeValue?string('#.########')  !} </#if> " />
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>结算方式:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">
                            <#list stakingSettleArr as item>
                                <label>
                                    <input type="radio"  name="settleMode" id="settleMode" value="${item.getKey()}"
                                            <#if entity?exists && entity.voucherNodeSettleMode == item.getKey()> checked </#if>/>
                                    <i></i>${item.getName()}
                                </label>
                            </#list>

                        </div>
                    </div>
                </div>
                <p  style="color: red">1. 默认不结算:(会产生收益记录，但是用户余额不会增加，质押时不会增加到质押金额中)</p>
                <p  style="color: red">2. 结算到余额:(产生的收益会增加到用户余额中，用户可以提现，质押时会增加到质押金额中)</p>

                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">质押代金总额:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="voucherStakingValue" id="voucherStakingValue" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity?exists> ${entity.voucherStakingValue?string('#.########')  !} </#if> " />
                    </div>
                </div>
                <p  style="color: red">1. 质押代金总额:(默认为0，结算方式为 结算到余额 时DeFi代金总额会增加到质押代金总额)</p>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>状态:</label>
                    <div class="col-sm-5">
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

        //确认保存
        $("#submitBtn").click(function () {

            var id = $('input[name="id"]').val();
            var voucherNodeValue = $('input[name="voucherNodeValue"]').val();
            var voucherStakingValue = $('input[name="voucherStakingValue"]').val();
            var settleMode = $('input[name="settleMode"]:checked').val();
            var status = $('input[name="status"]:checked').val();

            if(isEmpty(voucherNodeValue) || isEmpty(voucherStakingValue))
            {
                $.global.openErrorMsg('* 号为必填!');
                return;
            }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/agent/updateCoinDefiMiningVoucherInfo",
                data: {
                    id:id.trim(),
                    voucherNodeValue:voucherNodeValue.trim(),
                    voucherStakingValue:voucherStakingValue.trim(),
                    settleMode:settleMode.trim(),
                    status:status.trim(),
                },
                dataType: "json",
                success: function (data) {
                    console.log(data);
                    if (data != null && data.code == 200) {
                        $.global.openSuccessMsg("保存成功",function(){
                            window.close();
                        });
                    } else {
                        $.global.openErrorMsg(data.msg);
                    }
                },
                error: function (e) {
                    console.log(e);
                    $.global.openErrorMsg('保存失败，请重试');
                }
            })
        });
    });

</script>
</body>
</html>
