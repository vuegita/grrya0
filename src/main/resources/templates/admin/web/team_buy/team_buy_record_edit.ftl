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
            <p  style="color: green">1. 页面币种设置来自等级为1的币种，其它等级设置的币种不生效!</p>
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.id}</#if>"/>

                <div class="form-group">
                    <label class="control-label col-sm-2">所属代理:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="agentname" id="agentname" autocomplete="off"
                               required type="text"
                               value="<#if entity?exists> ${entity.agentname !} </#if>" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">所属员工:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="staffname" id="staffname" autocomplete="off"
                               required type="text"
                               value="<#if entity?exists> ${entity.staffname !} </#if>" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属分类:</label>
                    <div class="col-sm-5">
                        <select class="form-control" id="businessType" name="businessType" >
                            <#if entity?exists>
                                <option value="${entity.getBusinessType()}">${entity.getBusinessType()} </option>
                            <#else>
                                <option value="">---请选择分类--- </option>
                                <#list teamBuyingBusinessArr as item>
                                    <option value="${item.getKey()}">${item.getKey()} | ${item.getRemark()}</option>
                                </#list>
                            </#if>
                        </select>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属币种:</label>-->
<#--                    <div class="col-sm-5">-->
<#--                        <select class="form-control" id="currencyType" name="currencyType" >-->
<#--                            <#if entity?exists>-->
<#--                                <option value="${entity.getCurrencyType()}">${entity.getCurrencyType()} </option>-->
<#--                            <#else>-->
<#--                                <option value="">---请选择币种--- </option>-->
<#--                                <#list currencyTypeList as item>-->
<#--                                    <option value="${item.getKey()}">${item.getKey()}</option>-->
<#--                                </#list>-->
<#--                            </#if>-->
<#--                        </select>-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2">创建者返还率:</label>-->
<#--                    <div class="col-sm-5">-->
<#--                        <input class="form-control" name="returnCreatorRate" id="returnCreatorRate" autocomplete="off"-->
<#--                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"-->
<#--                               placeholder="0 < X < 1"-->
<#--                               value="<#if entity?exists> ${entity.returnCreatorRate?string('#.####')  !} </#if>" />-->
<#--                    </div>-->
<#--                    <div class="col-sm-5" style="color: red">返回比例范围0~1, 例：1%的话填写0.01</div>-->
<#--                </div>-->
<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2">参与者返还率:</label>-->
<#--                    <div class="col-sm-5">-->
<#--                        <input class="form-control" name="returnJoinRate" id="returnJoinRate" autocomplete="off"-->
<#--                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"-->
<#--                               placeholder="0 < X < 1"-->
<#--                               value="<#if entity?exists> ${entity.returnJoinRate?string('#.####')  !} </#if>" />-->
<#--                    </div>-->
<#--                    <div class="col-sm-5" style="color: red">返回比例范围0~1, 例：1%的话填写0.01</div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2">最低邀请人数:</label>-->
<#--                    <div class="col-sm-5">-->
<#--                        <input class="form-control" name="needInviteCount" id="needInviteCount" autocomplete="off"-->
<#--                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"-->
<#--                               placeholder="X > 3 && X <= 10" disabled-->
<#--                               value="<#if entity?exists> ${entity.needInviteCount!} </#if>" />-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2">已邀请人数:</label>-->
<#--                    <div class="col-sm-5">-->
<#--                        <input class="form-control" name="hasInviteCount" id="hasInviteCount" autocomplete="off"-->
<#--                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"-->
<#--                               placeholder="value >= 1"-->
<#--                               value="<#if entity?exists> ${entity.hasInviteCount !} </#if>" />-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

                <div class="form-group">
                    <label class="control-label col-sm-2">实际投资金额:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="realInvesAmount" id="realInvesAmount" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity?exists> ${entity.realInvesAmount !} </#if>" />
                    </div>
<#--                    <div class="col-sm-5" style="color: red">等级为1时，最低投资金额填写0</div>-->
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>状态:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">

                            <label><input type="radio"  name="status" id="status" value="waiting"
                                        <#if entity?exists && entity.status == 'waiting'> checked </#if>/>
                                <i></i>waiting
                            </label>

                            <label><input type="radio"  name="status" id="status" value="realized"
                                        <#if entity?exists && entity.status == 'realized'> checked </#if>/>
                                <i></i>realized
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

            // var agentname = $('input[name="agentname"]').val();

            // var businessType = $('select[name="businessType"]').val();
            // var currencyType = $('select[name="currencyType"]').val();
            var realInvesAmount = $('input[name="realInvesAmount"]').val();

            // var returnRate = $('input[name="returnRate"]').val();
            // var minAmount = $('input[name="limitMinAmount"]').val();
            // var minInviteCount = $('input[name="limitMinInviteCount"]').val();


            var status = $('input[name="status"]:checked').val();

            // if(returnRate <= 0 || returnRate >= 1)
            // {
            //     $.global.openErrorMsg('预期收益率: 0 < X < 1 !');
            //     return;
            // }
            // if(minInviteCount < 3 || minInviteCount > 10)
            // {
            //     $.global.openErrorMsg('最低邀请人数 >= 3 && <=10 !');
            //     return;
            // }
            //
            // if(isEmpty(status))
            // {
            //     $.global.openErrorMsg('* 号为必填!');
            //     return;
            // }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/updateWebTeamBuyingRecordInfo",
                data: {
                    id:id.trim(),
                    realInvesAmount:realInvesAmount.trim(),

                    // businessType:businessType.trim(),
                    // currencyType:currencyType.trim(),
                    // level:level.trim(),
                    //
                    // returnRate:returnRate.trim(),
                    // minAmount:minAmount.trim(),
                    // minInviteCount:minInviteCount.trim(),
                    //
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
