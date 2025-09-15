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
            <h5>系统设置</h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 提现风控配置仅对普通会员有效！！！</p>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <div class="panel panel-danger">

                    <div class="panel-heading">
                        <h3 class="panel-title">提现风控配置</h3>
                    </div>
                    <div class="panel-body">


<#--                        <div class="form-group">-->
<#--                            <label class="control-label col-sm-2">充值总额为0时是否允许提现:</label>-->
<#--                            <div class="col-sm-5">-->
<#--                                <div class="radio i-checks">-->
<#--                                    <label><input type="radio" name="risk_user_withdraw_enable_when_recharge_0" value="1" <#if config.risk_user_withdraw_enable_when_recharge_0 == "1">checked</#if>> <i></i>开启</label>-->
<#--                                    <label><input type="radio" name="risk_user_withdraw_enable_when_recharge_0" value="0" <#if config.risk_user_withdraw_enable_when_recharge_0 == "0">checked</#if>> <i></i>关闭</label>-->
<#--                                </div>-->
<#--                            </div>-->
<#--                        </div>-->
<#--                        <div class="hr-line-dashed"></div>-->

                        <div class="form-group">
                            <label class="control-label col-sm-2">提现时触发限制打码倍数:</label>
                            <div class="col-sm-5">
                                <input class="form-control" name="risk_user_withdraw_triger_limit_codeamount_multiple_number" id="risk_user_withdraw_triger_limit_codeamount_multiple_number" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.risk_user_withdraw_triger_limit_codeamount_multiple_number !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">触发充值为0限制条件:</label>
                            <div class="col-sm-5">
                                <div class="radio i-checks">
                                    <label><input type="radio" name="risk_user_withdraw_triger_impl_recharge_0" value="1" <#if config.risk_user_withdraw_triger_impl_recharge_0 == "1">checked</#if>> <i></i>开启</label>
                                    <label><input type="radio" name="risk_user_withdraw_triger_impl_recharge_0" value="0" <#if config.risk_user_withdraw_triger_impl_recharge_0 == "0">checked</#if>> <i></i>关闭</label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">触发提/充>=(?)限制条件:</label>
                            <div class="col-sm-5">
                                <input class="form-control" name="risk_user_withdraw_triger_impl_withdraw_divide_recharge_multiple_number" id="risk_user_withdraw_triger_impl_withdraw_divide_recharge_multiple_number" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.risk_user_withdraw_triger_impl_withdraw_divide_recharge_multiple_number !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">触发余额/充>=(?)限制条件:</label>
                            <div class="col-sm-5">
                                <input class="form-control" name="risk_user_withdraw_triger_impl_balance_divide_recharge_multiple_number" id="risk_user_withdraw_triger_impl_balance_divide_recharge_multiple_number" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.risk_user_withdraw_triger_impl_balance_divide_recharge_multiple_number !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>


                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">1. 当打码量倍数设置为0时，表示不限制 !!!</label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">2. 当触发条件关闭时或设置为0时，表示禁用，也就是不生效的意思 !!!</label><br>

                            <label class="control-label" style="color: red;margin-left: 100px">3. 打码量 = 历史打码 + 余额 * 提现时触发限制打码倍数 !!!</label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">4. 打码量示例: 如用户余额为10，打码倍数设置10，历史打码30, 此时打码量为 30 + 10 * 10 = 130 !!!</label><br>

                            <label class="control-label" style="color: red;margin-left: 100px">5. 触发提/充>=(?) 表示 提现总额 / 充值总额 = 触发倍数 !!!</label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">6. 示例: 设置触发提/充= 5时, 用户提现100 / 充值总额10 = 10 >= 5, 那么就会触发打码量 !!!</label><br>

                            <label class="control-label" style="color: red;margin-left: 100px">7. 触发余额/充>=(?) 表示 用户余额 / 充值总额 = 触发倍数 !!!</label><br>
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
                            <label class="control-label col-sm-2">自动提现开关:</label>
                            <div class="col-sm-5">
                                <div class="radio i-checks">
                                    <label><input type="radio" name="user_auto_withdraw_status" value="1" <#if config.user_auto_withdraw_status == "1">checked</#if>> <i></i>开启</label>
                                    <label><input type="radio" name="user_auto_withdraw_status" value="0" <#if config.user_auto_withdraw_status == "0">checked</#if>> <i></i>关闭</label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">1. 自动提现每分钟执行一次 !!!</label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">2. 自动提现开启后，后台审核将失效 !!!</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">代理补单赠送最大金额限制:</label>
                            <div class="col-sm-5">
                                <input class="form-control" name="passport_agent_supply_present_max_amount" id="passport_agent_supply_present_max_amount" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.passport_agent_supply_present_max_amount !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                    </div>

                </div>


                <div class="panel panel-danger">

                    <div class="panel-heading">
                        <h3 class="panel-title">有效会员条件</h3>
                    </div>
                    <div class="panel-body">


                        <div class="form-group">
                            <label class="control-label col-sm-2">有效邀请会员条件1: 最低总下单金额</label>
                            <div class="col-sm-5">
                                <input class="form-control" name="valid_invite_member_limit_total_deduct_code_amount" id="valid_invite_member_limit_total_deduct_code_amount" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.valid_invite_member_limit_total_deduct_code_amount !}</#if>" />
                            </div>
                        </div>

<#--                        <div class="form-group">-->
<#--                            <label class="control-label col-sm-2">有效邀请会员条件2: 最低首充金额</label>-->
<#--                            <div class="col-sm-5">-->
<#--                                <input class="form-control" name="valid_invite_member_limit_min_recharge_amount" id="valid_invite_member_limit_min_recharge_amount" autocomplete="off"-->
<#--                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"-->
<#--                                       value="<#if config?exists>${config.valid_invite_member_limit_min_recharge_amount !}</#if>" />-->
<#--                            </div>-->
<#--                        </div>-->

<#--                        <div class="form-group">-->
<#--                            <label class="control-label" style="color: red;margin-left: 100px">2. 自动提现开启后，后台审核将失效 !!!</label><br>-->
<#--                        </div>-->
                        <div class="hr-line-dashed"></div>

                    </div>

                </div>

                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">返佣分级配置</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2">限制最低返佣金额启用:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="return_water_layer_level_limit_min_present_amount" id="return_water_layer_level_limit_min_present_amount" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.return_water_layer_level_limit_min_present_amount !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">按时间分级返佣:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="return_water_layer_level_by_time" id="return_water_layer_level_by_time" autocomplete="off"
                                       required maxlength="100" type="text"
                                       value="<#if config?exists>${config.return_water_layer_level_by_time !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">1. 限制最低返佣金额启用指的是：返佣金额 >= 设置的这个值</label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">2. 按时间分级返佣，随着注册时间越久，返佣逐级减少！！！</label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">3. 按时间分级配置格式,  天数=比例|天数=比例, 示例 7=0.8|15=0.6 </label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">4. 如果会员自定义返佣比例则此功能不起作用 </label><br>
                        </div>
                        <div class="hr-line-dashed"></div>
                    </div>
                </div>


                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">邮件模板配置</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2">注册模板标题:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="web_email_reg_tpl_title" id="web_email_reg_tpl_title" autocomplete="off"
                                       required maxlength="100" type="text"
                                       value="<#if config?exists>${config.web_email_reg_tpl_title !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">注册模板内容:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="web_email_reg_tpl_desc" id="web_email_reg_tpl_desc" autocomplete="off"
                                       required maxlength="200" type="text"
                                       value="<#if config?exists>${config.web_email_reg_tpl_desc !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>
                    </div>
                </div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="submit" id="submit" value="保存"/>
                    </div>
                </div>
            </form>

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

    $('#submit').click(function () {

        $('#myModalDel #tips').text('确定保存吗？');
        $('#myModalDel').modal();

        return false;
    });

    $('#delete_submit').click(function () {

        var data = $('#form').serialize();

        console.log(data);

        $.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: "/alibaba888/Liv2sky3soLa93vEr62/updateBasicSystemConfig" ,//url
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
    });

</script>
</body>
</html>
