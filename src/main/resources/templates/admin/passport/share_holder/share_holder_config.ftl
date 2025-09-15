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
            <h5>配置管理</h5>

            <p style="color: blue"> 　</p>
<#--            <p  style="color: green">1. 范围0-10, 为0表示不打码！！！</p>-->
<#--            <p  style="color: green">2. 打码金额=订单金额*倍数！！!</p>-->
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <div class="panel panel-danger">

                    <div class="panel-heading">
                        <h3 class="panel-title"></h3>
                    </div>
                    <div class="panel-body">
                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>前端是否显示加入我们:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="contact_us_h5_show_switch" id="contact_us_h5_show_switch" value="true"
                                                <#if  config.contact_us_h5_show_switch == 'true'> checked </#if>/>
                                        <i></i>启用
                                    </label>

                                    <label><input type="radio"  name="contact_us_h5_show_switch" id="contact_us_h5_show_switch" value="false"
                                                <#if  config.contact_us_h5_show_switch == 'false'> checked </#if>/>
                                        <i></i>禁用
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>


                        <div class="form-group">
                            <label class="control-label col-sm-2">剩余数量:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="remaining_count" id="remaining_count" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.remaining_count !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>



                        <div class="form-group">
                            <label class="control-label col-sm-2">一级最低邀请人数:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="lv1_limit_min_invite_count" id="lv1_limit_min_invite_count" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.lv1_limit_min_invite_count !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">一级最低充值总额(最近3天平均):</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="lv1_limit_min_recharge_amount" id="lv1_limit_min_recharge_amount" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.lv1_limit_min_recharge_amount !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">二级最低邀请人数:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="lv2_limit_min_invite_count" id="lv2_limit_min_invite_count" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.lv2_limit_min_invite_count !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">二级最低充值总额(最近3天平均):</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="lv2_limit_min_recharge_amount" id="lv2_limit_min_recharge_amount" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.lv2_limit_min_recharge_amount !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">联系我们链接:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="contact_us" id="contact_us" autocomplete="off"
                                       required maxlength="1000" type="text"
                                       value="<#if config?exists>${config.contact_us !}</#if>" />
                            </div>
                        </div>
                        <div style="color: red;margin-left:10rem">例如：https://t.me/xxxxx</div>
                        <div class="hr-line-dashed"></div>



                        <div class="form-group">
                            <label class="control-label col-sm-2">加入我们群链接:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="contact_us_group" id="contact_us_group" autocomplete="off"
                                       required maxlength="1000" type="text"
                                       value="<#if config?exists>${config.contact_us_group !}</#if>" />
                            </div>
                        </div>
                        <div style="color: red;margin-left:10rem">例如：https://t.me/xxxxx</div>
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

        $.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: "/alibaba888/Liv2sky3soLa93vEr62/updatePassportShareHolderConfig" ,//url
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
