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
            <h5>打码量设置</h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 范围0-10, 为0表示不打码！！！</p>
            <p  style="color: green">2. 打码金额=订单金额*倍数！！!</p>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <div class="panel panel-danger">

                    <div class="panel-heading">
                        <h3 class="panel-title">普通会员打码量配置</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2">用户充值:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="simple_user_recharge" id="simple_user_recharge" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.simple_user_recharge !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-2">首次充值:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="simple_first_recharge" id="simple_first_recharge" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.simple_first_recharge !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">平台赠送:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="simple_platform_presentation" id="simple_platform_presentation" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.simple_platform_presentation !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">红包赠送:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="simple_red_package_presentation" id="simple_red_package_presentation" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.simple_red_package_presentation !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">任务赠送:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="simple_task_presentation" id="simple_task_presentation" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.simple_task_presentation !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">返佣赠送:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="simple_return_water_presentation" id="simple_return_water_presentation" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.simple_return_water_presentation !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">注册赠送:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="simple_register_presentation" id="simple_register_presentation" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.simple_register_presentation !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

<#--                        <div class="form-group">-->
<#--                            <label class="control-label" style="color: red;margin-left: 100px">范围0-10, 为0表示不限制！！！</label><br>-->
<#--                            <label class="control-label" style="color: red;margin-left: 100px">打码金额=订单金额*倍数！！！</label><br>-->
<#--                        </div>-->
<#--                        <div class="hr-line-dashed"></div>-->

                    </div>

                </div>

                <div class="panel panel-danger">

                    <div class="panel-heading">
                        <h3 class="panel-title">推广会员打码量配置</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2">用户充值:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="promotion_user_recharge" id="promotion_user_recharge" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.promotion_user_recharge !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-2">首次充值:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="promotion_first_recharge" id="promotion_first_recharge" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.promotion_first_recharge !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">平台赠送:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="promotion_platform_presentation" id="promotion_platform_presentation" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.promotion_platform_presentation !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">红包赠送:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="promotion_red_package_presentation" id="promotion_red_package_presentation" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.promotion_red_package_presentation !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">任务赠送:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="promotion_task_presentation" id="promotion_task_presentation" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.promotion_task_presentation !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">返佣赠送:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="promotion_return_water_presentation" id="promotion_return_water_presentation" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.promotion_return_water_presentation !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">注册赠送:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="promotion_register_presentation" id="promotion_register_presentation" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.promotion_register_presentation !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

<#--                        <div class="form-group">-->
<#--                            <label class="control-label" style="color: red;margin-left: 100px">范围0-10, 为0表示不限制！！！</label><br>-->
<#--                            <label class="control-label" style="color: red;margin-left: 100px">打码金额=订单金额*倍数！！！</label><br>-->
<#--                        </div>-->
<#--                        <div class="hr-line-dashed"></div>-->

                    </div>

                </div>

                <div class="panel panel-danger">

                    <div class="panel-heading">
                        <h3 class="panel-title">基础配置</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2">打码转余额比例:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="passport_code_amount_limit_type_code_2_balance" id="passport_code_amount_limit_type_code_2_balance" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${passport_code_amount_limit_type_code_2_balance !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">比如设置10：  打码100那么可转化可提现余额为:  100 / 10 = 10！</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">外部游戏打码倍数:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="game_pg_code_amount" id="game_pg_code_amount" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${game_pg_code_amount !}</#if>" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">外部游戏流水倍数:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="game_pg_running_amount" id="game_pg_running_amount" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${game_pg_running_amount !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">外部游戏指的是接入别人的游戏, 例如PG!</label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">外部流水倍数: 0 表示关闭, 1表示正常流水, 可设置小数表示下注金额要打个折扣!</label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">外部流水倍数示例: 设置0.5, 用户在外部游戏下注100， 实际在平台可折扣流水 只有 50!</label><br>
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
            url: "/alibaba888/Liv2sky3soLa93vEr62/updateBasicCodeAmountConfig" ,//url
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
