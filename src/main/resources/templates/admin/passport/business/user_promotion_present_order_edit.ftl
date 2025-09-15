<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>编辑</h5>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="orderno" id="orderno" value="<#if entity?exists>${entity.no}</#if>"/>

                <div class="form-group">

                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>用户名:</label>
                    <div class="col-sm-3">
                        <input class="form-control" id="username" name="username" autocomplete="off" required maxlength="500" value="<#if entity?exists>${entity.username}</#if>"/>
                    </div>
                    <button id="search-btn" type="button" class="btn btn-outline btn-default" title="查询">
                        <i class="glyphicon glyphicon-search" aria-hidden="true"></i> 搜索
                    </button>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger"></span>余额:</label>
                    <div class="col-sm-3">
                        <input name="balance" id="balance" class="form-control" autocomplete="off" required maxlength="50" disabled/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>赠送金额:</label>
                    <div class="col-sm-3">
                        <input  class="form-control" name="amount" id="amount" autocomplete="off" required maxlength="50"
                                onkeyup="this.value=this.value.replace(/[^\d\\.]/g,'');" value="<#if entity?exists>${entity.amount}</#if>"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>首次扣款金额:</label>
                    <div class="col-sm-3">
                        <input  class="form-control" name="limitRate1" id="limitRate1" autocomplete="off" required maxlength="50"
                                onkeyup="this.value=this.value.replace(/[^\d\\.]/g,'');" value="<#if entity?exists>${entity.limitRate1}</#if>"/>
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>二次扣款比例:</label>
                    <div class="col-sm-3">
                        <input  class="form-control" name="limitRate2" id="limitRate2" autocomplete="off" required maxlength="50"
                                onkeyup="this.value=this.value.replace(/[^\d\\.]/g,'');" value="<#if entity?exists>${entity.limitRate2}</#if>"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>结算状态:</label>
                    <div class="col-sm-3">
                        <div class="radio i-checks">
                            <label>
                                <input type="radio"  name="settleMode" id="settleMode" value="direct"
                                        <#if entity?exists && entity.settleMode == 'direct'> checked </#if>/>
                                <i></i>直接领取
                            </label>

                            <label><input type="radio"  name="settleMode" id="settleMode" value="deduct"
                                        <#if entity?exists && entity.settleMode == 'deduct'> checked </#if>/>
                                <i></i>扣款领取
                            </label>

                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="submit" id="mysubmit" value="提交"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>
            </form>
        </div>
    </div>
</div>


<#include "../../../common/delete_form.ftl">

<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript">
    $(function () {


        $('#search-btn').click(function(){

            var username = $('input[name="username"]').val();

            if(isEmpty(username))
            {
                $.global.openErrorMsg('* 号必填参数不能为空');
                return;
            }

            $.ajax({
                url: '/alibaba888/Liv2sky3soLa93vEr62/findUserInfo',
                type: 'post',
                dataType: 'json',
                data:{
                    username: username,
                    fundAccountType: "Spot",
                    currencyType: "USDT",
                },
                success: function(result){
                    console.log(result);
                    if(result && result.code == 200)
                    {
                        var balance = result.data.balance;
                        if(balance == null)
                        {
                            balance = 0;
                        }
                        $("#balance").prop("value", balance);
                    }
                    else
                    {
                        $.global.openErrorMsg(result.msg);
                    }
                },
                error: function(){
                    $.global.openErrorMsg('系统异常!');
                }
            });
        });

        $('#mysubmit').click(function () {

            var username = $('input[name="username"]').val();
            var amount =  $('input[name="amount"]').val();


            if(isEmpty(username) || isEmpty(amount))
            {
                $.global.openErrorMsg('* 号必填参数不能为空');
                return false;
            }

            $('#myModalDel #tips').text('确定提交金额为 ' + amount + ' 吗？');
            $('#myModalDel').modal();

            return false;
        });

        $('#delete_submit').click(function () {
            addSupply();
        });

    });

    function addSupply() {

        var orderno = $('input[name="orderno"]').val();
        var username = $('input[name="username"]').val();
        var amount =  $('input[name="amount"]').val();
        var limitRate1 = $('input[name="limitRate1"]').val();
        var limitRate2 = $('input[name="limitRate2"]').val();
        var settleMode = $('input[name="settleMode"]:checked').val();

        if(isEmpty(username) || isEmpty(amount) )
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return;
        }

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/root_passport_promotion_present_order/edit',
            type: 'post',
            dataType: 'json',
            data:{
                orderno: orderno,
                username: username,
                amount:amount,
                limitRate1:limitRate1,
                limitRate2:limitRate2,
                settleMode:settleMode,
            },
            success: function(result){
                console.log(result);
                if(result && result.code == 200)
                {
                    $.global.openSuccessMsg("SUCCESS",function(){
                        window.close();
                    });
                }
                else
                {
                    $.global.openErrorMsg(result.msg);
                }
            },
            error: function(){
                $.global.openErrorMsg('系统异常!');
            }
        });
    }

</script>
</body>
</html>
