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
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">


                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">平台配置</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2">每期最多投注总额:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="platform_config_max_money_of_issue" id="platform_config_max_money_of_issue" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.platform_config_max_money_of_issue !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">为0表示不限制！！！</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">每期每人最多投注总额:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="platform_config_max_money_of_user" id="platform_config_max_money_of_user" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.platform_config_max_money_of_user !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">为0表示不限制！！！</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">开奖模式:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label><input type="radio" name="platform_config_open_mode" value="random" <#if config.platform_config_open_mode == "random">checked</#if>> <i></i>随机开奖</label>
                                    <label><input type="radio" name="platform_config_open_mode" value="rate" <#if config.platform_config_open_mode == "rate">checked</#if>> <i></i>比例开奖</label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">平台盈利比例:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="platform_config_open_rate" id="platform_config_open_rate" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d\\.]/g,'');"
                                       value="<#if config?exists >${config.platform_config_open_rate !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">平台盈利比例只有当开奖模式设置为比例才生效！！！</label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">平台盈利比例设置期间为0到1, 当设置为1时表示平台获利最大！！！</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>


                    </div>
                </div>

                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">会员红包配置</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2">每期最多投注总额:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="member_config_max_money_of_issue" id="member_config_max_money_of_issue" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.member_config_max_money_of_issue !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">为0表示不限制！！！</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">每期每人最多投注总额:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="member_config_max_money_of_user" id="member_config_max_money_of_user" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.member_config_max_money_of_user !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">为0表示不限制！！！</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">开奖模式:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label><input type="radio" name="member_config_open_mode" value="random" <#if config.member_config_open_mode == "random">checked</#if>> <i></i>随机开奖</label>
                                    <label><input type="radio" name="member_config_open_mode" value="rate" <#if config.member_config_open_mode == "rate">checked</#if>> <i></i>比例开奖</label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">平台盈利比例:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="member_config_open_rate" id="member_config_open_rate" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d\\.]/g,'');"
                                       value="<#if config?exists >${config.member_config_open_rate !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">会员盈利比例只有当开奖模式设置为比例才生效！！！</label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">会员盈利比例设置期间为0到1, 当设置为1时表示会员获利最大！！！</label><br>
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

        $.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: "/alibaba888/Liv2sky3soLa93vEr62/updateGameRedPackageConfig" ,//url
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
