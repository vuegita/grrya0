<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-用户管理</title>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>编辑</h5>

            <p style="color: blue"> 　</p>
<#--            <p  style="color: green">1. 触发者信息是成对存在, 私钥信息不显示!</p>-->
<#--            <p  style="color: green">2. Gas手续费限制设置!</p>-->
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.id}</#if>"/>

                <#if isShow>
                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>员工名称:</label>
                    <div class="col-sm-5">
                        <input class="form-control" id="username" name="username" autocomplete="off" required maxlength="50"
                               value="<#if entity?exists> ${entity.username !} </#if>"/>
                    </div>
                </div>
                <#else>
                    <div class="form-group">
                            <input class="form-control" id="username" name="username" autocomplete="off" required maxlength="50" type="hidden"
                                   value="<#if entity?exists> ${entity.username !} </#if>"/>

                    </div>
                </#if>
                <div class="hr-line-dashed"></div>



<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>合约ID:</label>-->
<#--                    <div class="col-sm-5">-->
<#--                        <input class="form-control" id="contractid" name="contractid" autocomplete="off" required maxlength="50"-->
<#--                                <#if entity?exists> disabled </#if>-->
<#--                               value="<#if entity?exists> ${entity.contractid !} </#if>"/>-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属网络:</label>
                    <div class="col-sm-10">
                        <div class="radio i-checks">

                            <#assign currentChainType=''/>
                            <#list networkTypeArr as item>

                                <#if item_index == 0>
                                    <br>
                                    <#assign currentChainType=item.getChainType().getKey()/>
                                </#if>

                                <#if currentChainType != item.getChainType().getKey()>
                                    <br>
                                    <br>
                                    <#assign currentChainType=item.getChainType().getKey()/>
                                </#if>

                                <#if environment == 'prod' && !item.isTest()>
                                    <label>
                                        <input type="radio"  name="networkType" id="networkType" value="${item.getKey()}"
                                                <#if entity?exists> disabled </#if>
                                                <#if entity?exists && entity.networkType == item.getKey()> checked </#if>/>
                                        <i></i>${item.getKey()}
                                    </label>
                                <#else>
                                    <label>
                                        <input type="radio"  name="networkType" id="networkType" value="${item.getKey()}"
                                                <#if entity?exists> disabled </#if>
                                                <#if entity?exists && entity.networkType == item.getKey()> checked </#if>/>
                                        <i></i>${item.getKey()}
                                    </label>
                                </#if>
                            </#list>

                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属币种:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">
                            <br>
                            <#list cryptoCurrencyArr as item>
                                <#if item.isSupportLiquidityMining()>
                                    <label>
                                        <input type="radio"  name="currency" id="currency"  value="${item.getKey()}"
                                                <#if entity?exists> disabled </#if>
                                                <#if entity?exists && entity.currency == item.getKey()> checked </#if>/>
                                        <i></i>${item.getKey()}
                                    </label>
                                </#if>
                            </#list>
                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>地址:</label>-->
<#--                    <div class="col-sm-5">-->
<#--                        <input class="form-control" name="address" id="address" placeholder="按需填写" value="<#if entity?exists> ${entity.address} </#if>" autocomplete="off" required maxlength="100"/>-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2">金额:</label>-->
<#--                    <div class="col-sm-5">-->
<#--                        <input class="form-control" name="amount" id="amount" autocomplete="off"-->
<#--                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"-->
<#--                               placeholder="按需填写"-->
<#--                               value="<#if entity?exists> ${entity.amount?string('#.######')  !} </#if> " />-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>类型:</label>-->
<#--                    <div class="col-sm-5">-->
<#--                        <div class="radio i-checks">-->
<#--                            <#list qrcodeConfigTypeArr as item>-->
<#--                                <label>-->
<#--                                    <input type="radio"  name="qrcodeConfigType" id="qrcodeConfigType"  value="${item.getKey()}"-->
<#--                                            <#if entity?exists> disabled </#if>-->
<#--                                            <#if entity?exists && entity.type == item.getKey()> checked </#if>/>-->
<#--                                    <i></i>${item.getKey()}-->
<#--                                </label>-->
<#--                            </#list>-->
<#--                        </div>-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>备注:</label>-->
<#--                    <div class="col-sm-5">-->
<#--                        <input class="form-control" name="name" id="name" value="<#if entity?exists> ${entity.name} </#if>" autocomplete="off" required maxlength="100"/>-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>状态:</label>-->
<#--                    <div class="col-sm-5">-->
<#--                        <div class="radio i-checks">-->
<#--                            <label>-->
<#--                                <input type="radio"  name="status" id="status" value="enable"-->
<#--                                        <#if entity?exists && entity.status == 'enable'> checked </#if>/>-->
<#--                                <i></i>启用-->
<#--                            </label>-->

<#--                            <label><input type="radio"  name="status" id="status" value="disable"-->
<#--                                        <#if entity?exists && entity.status == 'disable'> checked </#if>/>-->
<#--                                <i></i>禁用-->
<#--                            </label>-->

<#--                        </div>-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

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

            var username = $('input[name="username"]').val();
            //var name = $('input[name="name"]').val();
            var name = "1";

            var networkType = $('input[name="networkType"]:checked').val();
            var currency = $('input[name="currency"]:checked').val();

            //var status = $('input[name="status"]:checked').val();
            var status = "enable"

            //var qrcodeConfigType = $('input[name="qrcodeConfigType"]:checked').val();
            var qrcodeConfigType = "Recharge"

            //var address = $('input[name="address"]').val();
            var address = "";

            //var amount = $('input[name="amount"]').val();
            var amount = "";

            if(isEmpty(networkType) || isEmpty(status))
            {
                $.global.openErrorMsg('* 号为必填!');
                return;
            }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/agent/updateCoinQrcodeAuthInfo",
                data: {
                    id:id.trim(),
                    name:name.trim(),
                    username:username.trim(),
                    networkType:networkType.trim(),
                    currency:currency.trim(),

                    address:address.trim(),
                    amount:amount.trim(),
                    qrcodeConfigType:qrcodeConfigType.trim(),
                    status:status.trim(),
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
