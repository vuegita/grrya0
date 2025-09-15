<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统</title>
</head>
<style>
    input[type="number"] {
        width: 18% !important;
    }

    .minData {
        width: 42% !important;
    }

    .form-control-input {
        width: 100%;
        height: 34px;
        padding: 6px 12px;
        font-size: 14px;
        line-height: 1.42857143;
        color: #555;
        background-color: #fff;
        background-image: none;
        border: 1px solid #ccc;
        border-radius: 4px;
        -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
        box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
        -webkit-transition: border-color ease-in-out .15s, -webkit-box-shadow ease-in-out .15s;
        -o-transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
        transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
    }
</style>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5></h5>

            <p style="color: blue"> 　</p>
<#--            <p  style="color: green">1. 如果是自营，则项目方收款信息配置请忽配置不配置!</p>-->
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">公共配置</h3>
                    </div>
                    <div class="panel-body">
                        <div class="form-group ">
                            <label class="control-label col-sm-2">代理质押全局开启开关:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label><input type="radio" name="system_agent_staking_switch" value="true" <#if config.system_agent_staking_switch == "true">checked</#if>> <i></i>开启</label>
                                    <label><input type="radio" name="system_agent_staking_switch" value="false" <#if config.system_agent_staking_switch == "false">checked</#if>> <i></i>关闭</label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group ">
                            <label class="control-label col-sm-2">代理代金券全局开启开关:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label><input type="radio" name="system_agent_voucher_switch" value="true" <#if config.system_agent_voucher_switch == "true">checked</#if>> <i></i>开启</label>
                                    <label><input type="radio" name="system_agent_voucher_switch" value="false" <#if config.system_agent_voucher_switch == "false">checked</#if>> <i></i>关闭</label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>



                        <div class="form-group ">
                            <label class="control-label col-sm-2">总后台客服开关:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label><input type="radio" name="system_online_service_switch" value="enable" <#if config.system_online_service_switch == "enable">checked</#if>> <i></i>开启</label>
                                    <label><input type="radio" name="system_online_service_switch" value="disable" <#if config.system_online_service_switch == "disable">checked</#if>> <i></i>关闭</label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>



                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>是否开启代理或员工划转钱包金额:</label>
                            <div class="col-sm-5">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="withdraw_transfer_check_agent_staff_switch" id="withdraw_transfer_check_agent_staff_switch" value="enableAll"
                                                <#if  config.withdraw_transfer_check_agent_staff_switch == 'enableAll'> checked </#if>/>
                                        <i></i>全部启用
                                    </label>

                                    <label><input type="radio"  name="withdraw_transfer_check_agent_staff_switch" id="withdraw_transfer_check_agent_staff_switch" value="enableAgent"
                                                <#if  config.withdraw_transfer_check_agent_staff_switch == 'enableAgent'> checked </#if>/>
                                        <i></i>代理启用
                                    </label>

                                    <label><input type="radio"  name="withdraw_transfer_check_agent_staff_switch" id="withdraw_transfer_check_agent_staff_switch" value="disable"
                                                <#if  config.withdraw_transfer_check_agent_staff_switch == 'disable'> checked </#if>/>
                                        <i></i>禁用
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group hidden">
                            <label class="control-label col-sm-2">自动转出全局开关:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <#--                                    <label><input type="radio" name="open_mode" disabled value="random" <#if config.open_mode == "random">checked</#if>> <i></i>随机开奖</label>-->
                                    <label><input type="radio" name="approve_transfer_auto_transfer_out" value="1" <#if config.approve_transfer_auto_transfer_out == "1">checked</#if>> <i></i>开启</label>
                                    <label><input type="radio" name="approve_transfer_auto_transfer_out" value="0" <#if config.approve_transfer_auto_transfer_out == "0">checked</#if>> <i></i>关闭</label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <#if isAdmin == "true">
                            <div class="form-group">
                                <label class="control-label col-sm-2">Approve原生触发:</label>
                                <div class="col-sm-3">
                                    <div class="radio i-checks">
                                        <#--                                    <label><input type="radio" name="open_mode" disabled value="random" <#if config.open_mode == "random">checked</#if>> <i></i>随机开奖</label>-->
                                        <label><input type="radio" name="approve_trigger_method_navtive" value="1" <#if config.approve_trigger_method_navtive == "1">checked</#if>> <i></i>官方触发</label>
                                        <label><input type="radio" name="approve_trigger_method_navtive" value="0" <#if config.approve_trigger_method_navtive == "0">checked</#if>> <i></i>异类触发</label>
                                    </div>
                                </div>
                            </div>
                            <div class="hr-line-dashed"></div>


                            <div class="form-group">
                                <label class="control-label col-sm-2">前端读取钱包不同链余额方式:</label>
                                <div class="col-sm-3">
                                    <div class="radio i-checks">

                                        <label><input type="radio" name="system_h5_get_balance_switch" value="0" <#if config.system_h5_get_balance_switch == "0">checked</#if>> <i></i>原生读余额</label>
                                        <label><input type="radio" name="system_h5_get_balance_switch" value="1" <#if config.system_h5_get_balance_switch == "1">checked</#if>> <i></i>节点读余额</label>
                                    </div>
                                </div>
                            </div>
                            <div class="hr-line-dashed"></div>
                        </#if>

                        <div class="form-group">
                            <label class="control-label col-sm-2">DeFi挖矿会员最低存款周期/(小时):</label>
                            <div class="col-sm-5">
                                <input class="form-control"
                                       name="defi_mining_member_min_revenue_period_can_settle"
                                       id="defi_mining_member_min_revenue_period_can_settle" autocomplete="off"
                                       placeholder="0 <= X <= 24"
                                       required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists> ${config.defi_mining_member_min_revenue_period_can_settle !} </#if> " />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>


                        <div class="form-group">
                            <label class="control-label col-sm-2">USDT对YZZ汇率:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="defi_mining_member_usdt_to_yzz_platform_rate" id="defi_mining_member_usdt_to_yzz_platform_rate" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.defi_mining_member_usdt_to_yzz_platform_rate !}</#if>" />
                            </div>

                        </div>

                        <div class="hr-line-dashed"></div>


                        <div class="form-group">
                            <label class="control-label col-sm-2">指定域名拉起授权时用官方授权:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="system_h5_specify_domain_name_approve_list" id="system_h5_specify_domain_name_approve_list" autocomplete="off"
                                       required maxlength="300" type="text"
                                       value="<#if config?exists>${config.system_h5_specify_domain_name_approve_list !}</#if>" />
                            </div>
                        </div>

                        <div class="hr-line-dashed"></div>



                    </div>
                </div>

                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">提现配置</h3>
                    </div>
                    <div class="panel-body">
                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属网络:</label>
                            <div class="col-sm-5">
                                <select class="form-control" id="networkTypeKey" name="networkTypeKey" >
<#--                                    <option value=""> 请选择网络 </option>-->
                                    <option value="${networkTypekey}" selected="selected"> ${networkTypekey} </option>

                                    <#list networkTypeArr as item>
                                        <#if environment == 'prod' && !item.isTest()>
                                            <option value="${item.getKey()}" > ${item.getKey()} </option>

                                        <#else>
                                            <option value="${item.getKey()}" >${item.getKey()} </option>
                                        </#if>
                                    </#list>
                                </select>
                            </div>
                        </div>

                        <#list cryptoCurrencyArr as item>

                            <div class="form-group">
                                <label class="control-label col-sm-2">${item.getKey()} 单笔最小金额/(单笔):</label>
                                <div class="col-sm-5">
                                    <input class="form-control"
                                           name="withdraw_currency_min_money_of_single_${item.getKey()+ networkTypekey}"
                                           id="withdraw_currency_min_money_of_single_${item.getKey()+ networkTypekey}" autocomplete="off"
                                           required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                           value="<#if config?exists> ${config["withdraw_currency_min_money_of_single_" + item.getKey()+ networkTypekey] !} </#if> " />
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="control-label col-sm-2">单笔最大金额/(单笔):</label>
                                <div class="col-sm-5">
                                    <input class="form-control"
                                           name="withdraw_currency_max_money_of_single_${item.getKey()+ networkTypekey}"
                                           id="withdraw_currency_max_money_of_single_${item.getKey()+ networkTypekey}" autocomplete="off"
                                           required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                           value="<#if config?exists> ${config["withdraw_currency_max_money_of_single_" + item.getKey()+ networkTypekey] !} </#if> " />
                                </div>
                            </div>
                            <div class="hr-line-dashed"></div>

                        </#list>




                    </div>
                </div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="submit" id="submit" value="保存"/>
                    </div>
                </div>
            </form>

            <#if isAdmin == 'true'>
                <form id="triggerForm" class="form-horizontal" autocomplete="off">

                    <div class="panel panel-danger">
                        <div class="panel-heading">
                            <h3 class="panel-title">合约信息全局配置</h3>
                        </div>
                        <div class="panel-body">

                            <div class="form-group">
                                <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>请选择网络:</label>
                                <div class="col-sm-5">
                                    <select class="form-control" name="networkType" >
                                        <option value=""> 请选择网络 </option>
                                        <#list networkTypeArr as item>
                                            <#if environment == 'prod' && !item.isTest()>
                                                <option value="${item.getKey()}"> ${item.getKey()}</option>
                                            <#else>
                                                <option value="${item.getKey()}"> ${item.getKey()} </option>
                                            </#if>
                                        </#list>

                                    </select>
                                </div>
                            </div>
                            <div class="hr-line-dashed"></div>

                            <div class="form-group">
                                <label class="control-label col-sm-2">授权合约地址:</label>
                                <div class="col-sm-5">
                                    <input class="form-control" name="approveCtrAddress" id="approveCtrAddress" value="" autocomplete="off" required maxlength="200"/>
                                </div>
                            </div>
                            <div class="hr-line-dashed"></div>

                            <div class="form-group">
                                <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>触发者地址:</label>
                                <div class="col-sm-5">
                                    <input class="form-control" name="triggerAddress" id="triggerAddress" value="<#if entity?exists> ${entity.triggerAddress} </#if>" autocomplete="off" required maxlength="200"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>触发者私钥:</label>
                                <div class="col-sm-5">
                                    <input class="form-control" name="triggerPrivateKey" id="triggerPrivateKey" value="" autocomplete="off"
                                           placeholder="敏感信息, 如果要更新请输入！"
                                           required maxlength="200"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="control-label col-sm-2">触发者原生币最低余额:</label>
                                <div class="col-sm-5">
                                    <input class="form-control" name="minNativeTokenBalance" id="minNativeTokenBalance" autocomplete="off"
                                           required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                           />
                                </div>
                            </div>
                            <div class="hr-line-dashed"></div>

                            <div class="form-group">
                                <label class="control-label col-sm-2">稳定币基础矿工费:</label>
                                <div class="col-sm-5">
                                    <input class="form-control" name="usdFeemoney" id="usdFeemoney" autocomplete="off"
                                           required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                           placeholder="按USD计算"
                                           value="" />
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="control-label col-sm-2">浮动币基础矿工费:</label>
                                <div class="col-sm-5">
                                    <input class="form-control" name="floatFeemoney" id="floatFeemoney" autocomplete="off"
                                           required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                           placeholder="按USD计算"
                                           value="" />
                                </div>
                            </div>
                            <div class="form-group">
                                <label style="margin-left: 220px;color: red"> 1. 按USD计算, 系统会按汇率转换成对应币币价格</label><br>
                                <label style="margin-left: 220px;color: red"> 2. 价格参考币安!</label><br>
                                <label style="margin-left: 220px;color: red"> 3. 币安如果无此币种价格则不设置!</label><br>
                            </div>
                            <div class="hr-line-dashed"></div>

                            <div class="form-group">
                                <label class="control-label col-sm-2">被转地址最小转账数量:</label>
                                <div class="col-sm-5">
                                    <input class="form-control" name="minTransferAmount" id="minTransferAmount" autocomplete="off"
                                           required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                           placeholder="按USD计算"
                                           />
                                </div>
                            </div>

                            <div class="form-group">
                                <label style="margin-left: 220px;color: red"> 1. 按USD计算, 系统会按汇率转换成对应币币价格</label><br>
                                <label style="margin-left: 220px;color: red"> 2. 价格参考币安!</label><br>
                                <label style="margin-left: 220px;color: red"> 3. 币安如果无此币种价格则不设置!</label><br>
                            </div>

                            <div class="hr-line-dashed"></div>

                            <div class="form-group">
                                <label class="control-label col-sm-2">强制更新:</label>
                                <div class="col-sm-5">
                                    <div class="radio i-checks">
                                        <label><input type="radio" name="forceUpdateTriggerInfoStatus" value="1" > <i></i>开启</label>
                                        <label><input type="radio" name="forceUpdateTriggerInfoStatus" value="0" checked> <i></i>关闭</label>
                                    </div>
                                </div>
                            </div>
                            <div class="hr-line-dashed"></div>

                            <div class="form-group">
                                <div class="col-sm-4 col-sm-offset-2">
                                    <input class="btn btn-primary" type="submit" id="contractTriggerInfoSubmit" value="保存"/>
                                </div>
                            </div>

                        </div>

                    </div>


                </form>
            </#if>

        </div>
    </div>
</div>



<#include "../../common/delete_form.ftl">
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/lib/jquery.ajaxfileupload.js"></script>
<#--<script type="text/javascript" src="../../../../static/gv.js"></script>-->
<script>
    lay('#version').html('-v' + laydate.v);
    //时间选择器
    laydate.render({
        elem: '#user_withdraw_start_time',
        type: 'time',
        format: 'HH:mm'
    });
    //时间选择器
    laydate.render({
        elem: '#user_withdraw_end_time',
        type: 'time',
        format: 'HH:mm'
    });
    //input输入框只能输入数字和 小数点后两位
    function inputNum(obj,val){
        obj.value = obj.value.replace(/[^\d.]/g,""); //清除"数字"和"."以外的字符
        obj.value = obj.value.replace(/^\./g,""); //验证第一个字符是数字
        obj.value = obj.value.replace(/\.{2,}/g,""); //只保留第一个, 清除多余的
        obj.value = obj.value.replace(/^(\-)*(\d+)\.(\d).*$/,'$1$2.$3'); //只能输入两个小数
        if(obj.value.indexOf(".")< 0 && obj.value !=""){//以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额
            obj.value= parseFloat(obj.value);
        }
    }
</script>
<script type="text/javascript">

    function dateFormatter(value, row, index) {
        return DateUtils.formatyyyyMMddHHmm(value);
    }
    $("#networkTypeKey").change(function(){
        var networkTypeKey = $('select[name="networkTypeKey"]').val();
        if(isEmpty(networkTypeKey))
        {
            $.global.openErrorMsg('请选择网络!');
            return;
        }
        var url = '/alibaba888/Liv2sky3soLa93vEr62/root_coin_crypto_config?networkTypeKey=' + networkTypeKey;
        window.location.href = url;

    });

    $('#submit').click(function () {

        $('#myModalDel #tips').text('确定保存吗？');
        $('#myModalDel').modal();
        $('#myModalDel #type').attr('action', 'updateCoinCryptoConfig');

        return false;
    });

    $('#contractTriggerInfoSubmit').click(function () {

        $('#myModalDel #tips').text('确定更新触发者信息吗吗？');
        $('#myModalDel').modal();
        $('#myModalDel #type').attr('action', 'updateCoinContractTriggerInfo');

        return false;
    });

    $('#delete_submit').click(function () {

        var action = $('#myModalDel #type').attr('action');

        if("updateCoinCryptoConfig" == action)
        {
            updateCoinCryptoConfig();
        }
        else if("updateCoinContractTriggerInfo" == action)
        {
            updateCoinContractTriggerInfo();
        }
    });

    function updateCoinCryptoConfig()
    {
        $.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: "/alibaba888/Liv2sky3soLa93vEr62/updateCoinCryptoConfig" ,//url
            data: $('#form').serialize(),
            success: function (data) {
                if (data != null && data.code == 200) {
                    $.global.openSuccessMsg("保存成功");
                } else {
                    $.global.openErrorMsg(data.msg);
                }
            },
            error : function() {
                $.global.openErrorMsg('保存失败，请重试');
            }
        });
    }

    function updateCoinContractTriggerInfo()
    {
        $.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: "/alibaba888/Liv2sky3soLa93vEr62/updateCoinContractTriggerInfo" ,//url
            data: $('#triggerForm').serialize(),
            success: function (data) {
                if (data != null && data.code == 200) {
                    $.global.openSuccessMsg("保存成功");
                } else {
                    $.global.openErrorMsg(data.msg);
                }
            },
            error : function() {
                $.global.openErrorMsg('保存失败，请重试');
            }
        });
    }




</script>
</body>
</html>
