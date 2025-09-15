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
            <h5>编辑产品</h5>

            <p style="color: blue"> 　</p>
<#--            <p  style="color: green">1. 触发者信息是成对存在, 私钥信息不显示!</p>-->
<#--            <p  style="color: green">2. Gas手续费限制设置!</p>-->
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.id}</#if>"/>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>名称:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="name" id="name" value="<#if entity?exists> ${entity.name} </#if>" autocomplete="off" required maxlength="100"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>合约ID:</label>
                    <div class="col-sm-5">
                        <input class="form-control" id="contractid" name="contractid" autocomplete="off" required maxlength="50"
                                <#if entity?exists> disabled </#if>
                               value="<#if entity?exists> ${entity.contractid !} </#if>"/>
                    </div>
                    <#if !entity?exists>
                        <button id="search-btn" type="button" class="btn btn-outline btn-default" title="查询">
                            <i class="glyphicon glyphicon-search" aria-hidden="true"></i> 搜索
                        </button>
                    </#if>
                </div>
                <div class="hr-line-dashed"></div>

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
<#--                                                <#if entity?exists> disabled </#if>-->
                                               disabled
                                                <#if entity?exists && entity.networkType == item.getKey()> checked </#if>/>
                                        <i></i>${item.getKey()}
                                    </label>
                                <#else>
                                    <label>
                                        <input type="radio"  name="networkType" id="networkType" value="${item.getKey()}"
<#--                                                <#if entity?exists> disabled </#if>-->
                                               disabled
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
                    <label class="control-label col-sm-2">所属网络排序:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="networkTypeSort" id="networkTypeSort" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               value="<#if entity?exists> ${entity.networkTypeSort !} </#if>" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>挖矿币种:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">
                            <br>
                            <#list cryptoCurrencyArr as item>
                                <#if item.isSupportLiquidityMining()>
                                    <label>
                                        <input type="radio"  name="quoteCurrency" id="quoteCurrency"  value="${item.getKey()}"
<#--                                                <#if entity?exists> disabled </#if>-->
                                               disabled
                                                <#if entity?exists && entity.quoteCurrency == item.getKey()> checked </#if>/>
                                        <i></i>${item.getKey()}
                                    </label>
                                </#if>
                            </#list>
                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span> 收益币种:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">
                            <br>
                            <#list cryptoCurrencyArr as item>
                                <label>
                                    <input type="radio"  name="baseCurrency" id="baseCurrency" value="${item.getKey()}"
                                            <#if entity?exists> disabled </#if>
                                            <#if entity?exists && entity.baseCurrency == item.getKey()> checked </#if>/>
                                    <i></i>${item.getKey()}
                                </label>
                            </#list>

                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2">挖矿收益最小提现金额:</label>-->
<#--                    <div class="col-sm-5">-->
<#--                        <input class="form-control" name="minWithdrawAmount" id="minWithdrawAmount" autocomplete="off"-->
<#--                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"-->
<#--                               value="<#if entity?exists> ${entity.minWithdrawAmount?string('#.######')  !} </#if> " />-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

                <div class="form-group">
                    <label class="control-label col-sm-2">授权地址最小余额:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="minWalletBalance" id="minWalletBalance" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity?exists> ${entity.minWalletBalance?string('#.######')  !} </#if> " />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">预期收益率:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="expectedRate" id="expectedRate" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               placeholder="0 < X < 1"
                               value="<#if entity?exists> ${entity.expectedRate?string('#.####')  !} </#if> " />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">挖矿币种排序:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="quoteCurrencySort" id="quoteCurrencySort" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               value="<#if entity?exists> ${entity.quoteCurrencySort !} </#if> " />
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

        $('#search-btn').click(function(){
            $.ajax({
                url: '/alibaba888/Liv2sky3soLa93vEr62/findCoinCryptoContractInfoById',
                type: 'post',
                dataType: 'json',
                data:{
                    contractid: function () {
                        return $('input[name="contractid"]').val();
                    }
                },
                success: function(result){
                    if(result && result.code == 200)
                    {
                        var currencyType = result.data.currencyType;
                        var networkType = result.data.networkType;


                        $("input[name='quoteCurrency'][value='" + currencyType +"']").parent().addClass('checked').prop("checked", "checked");
                        $("input[name='networkType'][value='" + networkType +"']").parent().addClass('checked').prop("checked", "checked");

                    }
                    else
                    {
                        $.global.openErrorMsg(result.msg);
                    }
                },
                error: function(){
                    $.global.openErrorMsg('保存失败，请重试');
                }
            });
        });

        //确认保存
        $("#submitBtn").click(function () {

            var id = $('input[name="id"]').val();
            var contractid = $('input[name="contractid"]').val();
            var name = $('input[name="name"]').val();
            //var networkType = $('input[name="networkType"]:checked').val();
            var baseCurrency = $('input[name="baseCurrency"]:checked').val();
            //var quoteCurrency = $('input[name="quoteCurrency"]:checked').val();

            var expectedRate = $('input[name="expectedRate"]').val();
            // var minWithdrawAmount = $('input[name="minWithdrawAmount"]').val();
            var minWalletBalance = $('input[name="minWalletBalance"]').val();

            var networkTypeSort = $('input[name="networkTypeSort"]').val();
            var quoteCurrencySort = $('input[name="quoteCurrencySort"]').val();

            var status = $('input[name="status"]:checked').val();

            if(expectedRate <= 0 || expectedRate >= 1)
            {
                $.global.openErrorMsg('预期收益率: 0 < X < 1 !');
            }

            if(isEmpty(contractid) || isEmpty(baseCurrency))
            {
                $.global.openErrorMsg('* 号为必填!');
                return;
            }

            if(isEmpty(baseCurrency) || isEmpty(status))
            {
                $.global.openErrorMsg('* 号为必填!');
                return;
            }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/updateCoinMiningProductInfo",
                data: {
                    id:id.trim(),

                    contractid:contractid.trim(),
                    name:name.trim(),
                    //networkType:networkType.trim(),

                    baseCurrency:baseCurrency.trim(),
                    //quoteCurrency:quoteCurrency.trim(),

                    expectedRate:expectedRate.trim(),
                    // minWithdrawAmount:minWithdrawAmount.trim(),
                    minWalletBalance:minWalletBalance.trim(),

                    networkTypeSort:networkTypeSort.trim(),
                    quoteCurrencySort:quoteCurrencySort.trim(),

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
