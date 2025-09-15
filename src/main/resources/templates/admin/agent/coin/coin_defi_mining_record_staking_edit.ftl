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
            <h5>DeFi质押</h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 返利周期指剩于可返利周期，1周期=6小时!</p>
<#--            <p  style="color: green">2. Gas手续费限制设置!</p>-->
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.id}</#if>"/>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属用户:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="username" id="username" value="<#if entity?exists> ${entity.username} </#if>"
                               readonly
                               autocomplete="off" required maxlength="100"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>质押币种:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="baseCurrency" id="baseCurrency" value="<#if entity?exists> ${entity.baseCurrency} </#if>"
                               readonly
                               autocomplete="off" required maxlength="100"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">质押总额:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="stakingAmount" id="stakingAmount" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity?exists> ${entity.stakingAmount?string('#.########')  !} </#if> " />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">质押收益:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="stakingRewardAmount" id="stakingRewardAmount" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity?exists && entity.stakingRewardValue??> ${entity.stakingRewardValue?string('#.########')  !} </#if>" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">质押额外收益:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="stakingRewardExternal" id="stakingRewardExternal" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity?exists && entity.stakingRewardExternal??> ${entity.stakingRewardExternal?string('#.######')  !} </#if>" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">返利周期/(小时):</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="stakingRewardHour" id="stakingRewardHour" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               placeholder="6的倍数!"
                               value="<#if entity?exists> ${entity.stakingRewardHour !} </#if> " />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>结算方式:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">
                            <#list stakingSettleArr as item>
                                <label>
                                    <input type="radio"  name="settleMode" id="settleMode" value="${item.getKey()}"
                                            <#if entity?exists && entity.stakingSettleMode == item.getKey()> checked </#if>/>
                                    <i></i>${item.getName()}
                                </label>
                            </#list>

                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>状态:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">
                            <label>
                                <input type="radio"  name="stakingStatus" id="stakingStatus" value="enable"
                                        <#if entity?exists && entity.stakingStatus == 'enable'> checked </#if>/>
                                <i></i>启用
                            </label>

                            <label><input type="radio"  name="stakingStatus" id="stakingStatus" value="disable"
                                        <#if entity?exists && entity.stakingStatus == 'disable'> checked </#if>/>
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
                url: '/alibaba888/agent/findCoinCryptoContractInfoById',
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
            var stakingAmount = $('input[name="stakingAmount"]').val();
            var stakingRewardAmount = $('input[name="stakingRewardAmount"]').val();
            var stakingRewardExternal = $('input[name="stakingRewardExternal"]').val();

            var stakingRewardHour = $('input[name="stakingRewardHour"]').val();
            var stakingStatus = $('input[name="stakingStatus"]:checked').val();
            var settleMode = $('input[name="settleMode"]:checked').val();


            if(isEmpty(baseCurrency) || isEmpty(stakingStatus))
            {
                $.global.openErrorMsg('* 号为必填!');
                return;
            }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/agent/updateCoinDefiMiningStaking",
                data: {
                    id:id.trim(),
                    stakingAmount:stakingAmount.trim(),
                    stakingRewardAmount:stakingRewardAmount.trim(),
                    stakingRewardExternal:stakingRewardExternal.trim(),
                    stakingRewardHour:stakingRewardHour.trim(),
                    stakingStatus:stakingStatus.trim(),
                    settleMode:settleMode.trim(),
                },
                dataType: "json",
                success: function (data) {
                    console.log(data);
                    if (data != null && data.code == 200) {
                        $.global.openSuccessMsg("保存成功",function(){
                            window.close();
                        });
                    } else {
                        $.global.openErrorMsg(data.msg);
                    }
                },
                error: function (e) {
                    console.log(e);
                    $.global.openErrorMsg('保存失败，请重试');
                }
            })
        });
    });

</script>
</body>
</html>
