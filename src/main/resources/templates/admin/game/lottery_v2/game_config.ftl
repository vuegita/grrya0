<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
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

            <p  style="color: green">1. 扫雷游戏的平台盈利比例表示调整用户中奖系数 >= 0, 0表示全杀, 1表示按游戏正常概率, 大于1表示送</p>
            <p  style="color: green">2. 足球游戏的平台盈利比例表示调整用户中奖系数 >= 0, 0表示全杀, 1表示按游戏正常概率, 大于1表示送</p>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">


                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">开奖配置</h3>
                    </div>
                    <div class="panel-body">

                        <#if config.max_money_of_issue?exists && config.max_money_of_issue != '' >
                            <div class="form-group">
                                <label class="control-label col-sm-2">每期最多投注总额:</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="max_money_of_issue" id="max_money_of_issue" autocomplete="off"
                                           required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                           value="<#if config?exists>${config.max_money_of_issue !}</#if>" />
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label" style="color: red;margin-left: 100px">为0表示不限制！！！</label><br>
                            </div>
                            <div class="hr-line-dashed"></div>
                        </#if>

                        <#if config.max_money_of_user?exists && config.max_money_of_user != '' >
                            <div class="form-group">
                                <label class="control-label col-sm-2">每期每人最多投注总额:</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="max_money_of_user" id="max_money_of_user" autocomplete="off"
                                           required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                           value="<#if config?exists>${config.max_money_of_user !}</#if>" />
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label" style="color: red;margin-left: 100px">为0表示不限制！！！</label><br>
                            </div>
                            <div class="hr-line-dashed"></div>
                        </#if>

                        <#if config.open_mode?exists && config.open_mode != '' >
                            <div class="form-group">
                                <label class="control-label col-sm-2">开奖模式:</label>
                                <div class="col-sm-3">
                                    <div class="radio i-checks">
                                        <#--                                    <label><input type="radio" name="open_mode" value="random" disabled <#if config.open_mode == "random">checked</#if>> <i></i>随机开奖</label>-->
                                        <label><input type="radio" name="open_mode" value="rate" <#if config.open_mode == "rate">checked</#if>> <i></i>比例开奖</label>
                                        <label><input type="radio" name="open_mode" value="smart" <#if config.open_mode == "smart">checked</#if>> <i></i>智能开奖</label>
                                    </div>
                                </div>
                            </div>
                            <div class="hr-line-dashed"></div>
                        </#if>


                        <#if config.open_rate?? && config.open_rate != '' >
                            <div class="form-group">
                                <label class="control-label col-sm-2">平台盈利比例:</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="open_rate" id="open_rate" autocomplete="off"
                                           required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d\\.]/g,'');"
                                           value="<#if config?exists >${config.open_rate !}</#if>" />
                                </div>
                            </div>
                        </#if>

                        <#if config.open_smart_num?exists && config.open_smart_num != '' >
                            <div class="form-group">
                                <label class="control-label col-sm-2">智能开奖概率:</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="open_smart_num" id="open_smart_num" autocomplete="off" placeholder="范围(1-10)的整数"
                                           required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                           value="<#if config?exists >${config.open_smart_num !}</#if>" />
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="control-label" style="color: red;margin-left: 100px">1. 平台盈利比例只有当开奖模式设置为比例才生效！！！</label><br>
                                <label class="control-label" style="color: red;margin-left: 100px">2. 平台盈利比例设置期间为0到1, 当设置为1时表示平台获利最大！！！</label><br>
                                <label class="control-label" style="color: red;margin-left: 100px">3. 智能开奖概率范围1-10, 如填5表示  随机数 >= 5 就杀</label><br>
                                <label class="control-label" style="color: red;margin-left: 100px">4. 智能开奖设置1，表示有90%概率走比例开奖, 10%走随机开奖</label><br>
                                <label class="control-label" style="color: red;margin-left: 100px">5. 智能开奖设置5，表示有50%概率走比例开奖, 50%走随机开奖</label><br>
                                <label class="control-label" style="color: red;margin-left: 100px">6. 智能开奖设置10，走随机开奖</label><br>
                            </div>
                            <div class="hr-line-dashed"></div>
                        </#if>

                        <#if config.max_crash_value?exists && config.max_crash_value != '' >
                            <div class="form-group">
                                <label class="control-label col-sm-2">Crash临界值:</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="max_crash_value" id="max_crash_value" autocomplete="off" placeholder=""
                                           required maxlength="1000" type="text"
                                           value="<#if config?exists >${config.max_crash_value !}</#if>" />
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label" style="color: red;margin-left: 100px">1. 飞机上升过程上所能飞到最大高度的爆炸值！！！</label><br>
                                <label class="control-label" style="color: red;margin-left: 100px">2. 配置格式  1.5|1.8|1.9| </label><br>

                            </div>
                            <div class="hr-line-dashed"></div>
                        </#if>

                        <#if config.max_win_amount_muitiple_2_bet_amount?exists && config.max_win_amount_muitiple_2_bet_amount != '' >
                            <div class="form-group">
                                <label class="control-label col-sm-2">Crash最大投注额倍数:</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="max_win_amount_muitiple_2_bet_amount" id="max_win_amount_muitiple_2_bet_amount" autocomplete="off" placeholder=""
                                           required maxlength="1000" type="text"
                                           value="<#if config?exists >${config.max_win_amount_muitiple_2_bet_amount !}</#if>" />
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label" style="color: red;margin-left: 100px">1. 飞机上升过程上所能飞到最大亏损金额！！！</label><br>
                                <label class="control-label" style="color: red;margin-left: 100px">2. 计算爆炸: (下注总额 - 下车总额) * 当前飞机值 + 下车中奖总额 >= 下注总额 * 现在配置的这个投注额倍数</label><br>

                            </div>
                            <div class="hr-line-dashed"></div>
                        </#if>

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



<#include "../../../common/delete_form.ftl">
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
            url: "/alibaba888/Liv2sky3soLa93vEr62/${moduleRelateUrl}/updateGameConfig" ,//url
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
