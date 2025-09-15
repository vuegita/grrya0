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
            <h5>编辑合约</h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 触发者信息是成对存在, 私钥信息不显示!</p>
            <p  style="color: green">2. Gas手续费限制设置!</p>
            <p  style="color: green">3. 原生币必须设置最低余额，小于限制余额则不执行转账操作!</p>
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.id}</#if>"/>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>合约简介:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="desc" id="desc" value="<#if entity?exists> ${entity.desc} </#if>" autocomplete="off" required maxlength="100"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>合约地址:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="address" id="address"
                               value="<#if entity?exists> ${entity.address} </#if>" autocomplete="off" required maxlength="200"/>
                    </div>
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
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>操作代币:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">
                            <br>
                            <#list currencyTypeArr as item>
                                <#if item.isToken()>
                                    <label>
                                        <input type="radio"  name="currencyType" id="currencyType" value="${item.getKey()}"
                                                <#if entity?exists> disabled </#if>
                                                <#if entity?exists && entity.currencyType == item.getKey()> checked </#if>/>
                                        <i></i>${item.getKey()}
                                    </label>
                                </#if>

                            </#list>

                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>代币合约地址:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="currencyCtrAddr" id="currencyCtrAddr"
                                <#if entity?exists> readonly </#if>
                               value="<#if entity?exists> ${entity.currencyCtrAddr} </#if>" autocomplete="off" required maxlength="200"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>代币公链:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">

                            <#list chaintTypeArr as item>
                                <#if item.isToken()>
                                    <label>
                                        <input type="radio"  name="currencyChainType" id="currencyChainType" value="${item.getKey()}"
                                                <#if entity?exists> disabled </#if>
                                                <#if entity?exists && entity.currencyChainType == item.getKey()> checked </#if>/>
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
                    <label class="control-label col-sm-2">被转地址最小转账数量:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="minTransferAmount" id="minTransferAmount" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity?exists> ${entity.minTransferAmount?string("0.######") !} </#if> " />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group hidden">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>自动转账:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">
                            <label>
                                <input type="radio"  name="autoTransfer" id="autoTransfer" value="enable"
                                        <#if entity?exists && entity.autoTransfer == 'enable'> checked </#if>/>
                                <i></i>启用
                            </label>

                            <label><input type="radio"  name="autoTransfer" id="autoTransfer" value="disable"
                                        <#if entity?exists && entity.autoTransfer == 'disable'> checked </#if>/>
                                <i></i>禁用
                            </label>

                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">触发者原生币最低余额:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="minNativeTokenBalance" id="minNativeTokenBalance" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="${remarkInfo.minNativeTokenBalance !}" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group">
                    <label class="control-label col-sm-2">Approve触发方式:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">
                            <#if remarkInfo.triggerApproveType?exists>

                                <#list triggerApproveTypeArr as item>
                                    <label><input type="radio" name="triggerApproveType" value="${item.getKey()}"
                                                  <#if remarkInfo.triggerApproveType! == "${item.getKey()}">checked</#if>>
                                        <i></i>${item.getName()}</label>
                                </#list>

                            <#else>
                                <#list triggerApproveTypeArr as item>
                                    <label><input type="radio" name="triggerApproveType" value="${item.getKey()}" >
                                        <i></i>${item.getName()}</label>
                                </#list>

                            </#if>

                            <#--                                    <label><input type="radio" name="open_mode" disabled value="random" <#if config.open_mode == "random">checked</#if>> <i></i>随机开奖</label>-->

                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group">
                    <label class="control-label col-sm-2">Approve触发方法:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="currencyApproveMethod" id="currencyApproveMethod" autocomplete="off"
                               placeholder="为空表示用默认 [approve]!"
                               required type="text"
                               value="${remarkInfo.currencyApproveMethod !}" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group">
                    <label class="control-label col-sm-2">代币精度:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="currencyDecimals" id="currencyDecimals" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               value="${remarkInfo.currencyDecimals !}" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">GasLimit:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="gasLimit" id="gasLimit" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               value="${remarkInfo.gasLimit !}" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">订单手续费(按代币计算):</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="feemoney" id="feemoney" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="${remarkInfo.feemoney !}" />
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
            var desc = $('input[name="desc"]').val();
            var address = $('input[name="address"]').val();
            var networkType = $('input[name="networkType"]:checked').val();
            var currencyType = $('input[name="currencyType"]:checked').val();
            var currencyCtrAddr = $('input[name="currencyCtrAddr"]').val();
            var currencyChainType = $('input[name="currencyChainType"]:checked').val();

            var triggerPrivateKey = $('input[name="triggerPrivateKey"]').val();
            var triggerAddress = $('input[name="triggerAddress"]').val();
            var minTransferAmount = $('input[name="minTransferAmount"]').val();
            var autoTransfer = $('input[name="autoTransfer"]:checked').val();

            var status = $('input[name="status"]:checked').val();


            var triggerApproveType = $('input[name="triggerApproveType"]:checked').val();
            var currencyApproveMethod = $('input[name="currencyApproveMethod"]').val();
            var minNativeTokenBalance = $('input[name="minNativeTokenBalance"]').val();
            var currencyDecimals = $('input[name="currencyDecimals"]').val();
            var gasLimit = $('input[name="gasLimit"]').val();
            var feemoney = $('input[name="feemoney"]').val();

            if(currencyDecimals < 0 || currencyDecimals > 18)
            {
                $.global.openErrorMsg('代币精度范围:  0 <= X <= 18 !');
                return;
            }

            if(isEmpty(triggerApproveType))
            {
                triggerApproveType  = '';
            }

            if(gasLimit <= 0)
            {
                $.global.openErrorMsg('GasLimit > 0 !');
                return;
            }

            var remark = JSON.stringify({
                // transferToAddress: transferToAddress.trim(),
                triggerApproveType:triggerApproveType.trim(),
                currencyApproveMethod:currencyApproveMethod.trim(),
                minNativeTokenBalance:minNativeTokenBalance.trim(),
                currencyDecimals:currencyDecimals.trim(),
                gasLimit:gasLimit.trim(),
                feemoney:feemoney.trim(),
            });

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/updateCoinCryptoContractInfo",
                data: {
                    id:id.trim(),
                    desc:desc.trim(),
                    address:address.trim(),
                    networkType:networkType.trim(),

                    currencyType:currencyType.trim(),
                    currencyCtrAddr:currencyCtrAddr.trim(),
                    currencyChainType:currencyChainType.trim(),

                    triggerPrivateKey:triggerPrivateKey.trim(),
                    triggerAddress:triggerAddress.trim(),

                    minTransferAmount:minTransferAmount.trim(),
                    autoTransfer:autoTransfer.trim(),
                    status:status.trim(),

                    remark:remark.trim(),
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
