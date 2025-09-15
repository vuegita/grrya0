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
            <h5>编辑提币通道</h5>

            <p style="color: blue"> 　</p>
<#--            <p  style="color: green">1. 订单金额 = 项目方 + 平台方 + 代理方!</p>-->
<#--            <p  style="color: green">2. 订单手续费为Gas手续费(项目方收取)!</p>-->
<#--            <p  style="color: green">3. 平台分成比例不生效(扣除其它分成, 剩下的都归属平台收益)!</p>-->
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.id}</#if>"/>

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>维度类型:</label>-->
<#--                    <div class="col-sm-10">-->
<#--                        <div class="radio i-checks">-->

<#--                            <#list dimensionTypeArr as item>-->
<#--                                <label>-->
<#--                                    <input type="radio"  name="dimensionType" id="dimensionType" value="${item.getKey()}"-->
<#--                                            <#if entity?exists> disabled </#if>-->
<#--                                            <#if entity?exists && entity.dimensionType == item.getKey()> checked </#if>/>-->
<#--                                    <i></i> ${item.getKey()} | ${item.getName()}-->
<#--                                </label>-->

<#--                            </#list>-->

<#--                        </div>-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属代理:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="key" id="key" value="<#if entity?exists> ${entity.key} </#if>" placeholder="请输入代理名" autocomplete="off" required maxlength="100"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>合约网络:</label>
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
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>触发者私钥:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="triggerPrivateKey" id="triggerPrivateKey" value="" autocomplete="off"
                               placeholder="敏感信息, 如果要更新请输入！"
                               required maxlength="200"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>触发者地址:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="triggerAddress" id="triggerAddress" value="<#if entity?exists> ${entity.triggerAddress} </#if>" autocomplete="off" required maxlength="200"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>gasLimit:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="gasLimit" id="gasLimit" autocomplete="off"
                               value="<#if entity?exists>${entity.gasLimit !}</#if>"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">手续费率:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="feeRate" id="feeRate" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity?exists> ${entity.feeRate !} </#if> " />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">额度手续费:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="singleFeemoney" id="singleFeemoney" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity?exists> ${entity.singleFeemoney !} </#if> " />
                    </div>
                </div>
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
            var key = $('input[name="key"]').val();
            // var dimensionType = $('input[name="dimensionType"]:checked').val();

            var networkType = $('input[name="networkType"]:checked').val();
            var triggerPrivateKey = $('input[name="triggerPrivateKey"]').val();
            var triggerAddress = $('input[name="triggerAddress"]').val();
            var gasLimit = $('input[name="gasLimit"]').val();



            var feeRate = $('input[name="feeRate"]').val();
            var singleFeemoney = $('input[name="singleFeemoney"]').val();

            var status = $('input[name="status"]:checked').val();

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/updateCoinWithdrawChannelInfo",
                data: {
                    id:id.trim(),
                    key:key.trim(),
                    // dimensionType:dimensionType.trim(),
                    networkType:networkType.trim(),

                    triggerPrivateKey:triggerPrivateKey.trim(),
                    triggerAddress:triggerAddress.trim(),
                    gasLimit:gasLimit.trim(),

                    feeRate:feeRate.trim(),
                    singleFeemoney:singleFeemoney.trim(),

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
