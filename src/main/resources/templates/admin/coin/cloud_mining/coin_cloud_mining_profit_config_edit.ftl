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
            <h5>编辑配置</h5>

            <p style="color: blue"> 　</p>
<#--            <p  style="color: green">1. 触发者信息是成对存在, 私钥信息不显示!</p>-->
<#--            <p  style="color: green">2. Gas手续费限制设置!</p>-->
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.id}</#if>"/>

                <div class="form-group">
                    <label class="control-label col-sm-2">投资期限/(天):</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="days" id="days" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               placeholder="投资期限: value >= 1"
                               value="<#if entity?exists> ${entity.days !} </#if>" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">等级:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="level" id="level" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               placeholder="value >= 1"
                               value="<#if entity?exists> ${entity.level !} </#if>" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">最低投资金额:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="minAmount" id="minAmount" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity?exists> ${entity.minAmount?string('#.####')  !} </#if> " />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group">
                    <label class="control-label col-sm-2">日收益率:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="dailyRate" id="dailyRate" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               placeholder="0 < X < 1"
                               value="<#if entity?exists> ${entity.dailyRate?string('#.####')  !} </#if> " />
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

            var days = $('input[name="days"]').val();
            var level = $('input[name="level"]').val();

            var dailyRate = $('input[name="dailyRate"]').val();
            var minAmount = $('input[name="minAmount"]').val();

            var status = $('input[name="status"]:checked').val();

            if(dailyRate <= 0 || dailyRate >= 1)
            {
                $.global.openErrorMsg('预期收益率: 0 < X < 1 !');
            }

            if(isEmpty(status))
            {
                $.global.openErrorMsg('* 号为必填!');
                return;
            }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/updateCoinCloudMiningProfitConfig",
                data: {
                    id:id.trim(),

                    days:days.trim(),
                    level:level.trim(),

                    dailyRate:dailyRate.trim(),
                    minAmount:minAmount.trim(),

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
