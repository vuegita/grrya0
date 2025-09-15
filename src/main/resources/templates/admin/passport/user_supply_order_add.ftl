<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>申请补单</h5>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>用户名:</label>
                    <div class="col-sm-3">
                        <input class="form-control" id="username" name="username" autocomplete="off" required maxlength="500" value="${username!}"/>
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
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>账户类型:</label>
                    <div class="col-sm-3">
                        <select class="form-control" id="fundAccountType" name="fundAccountType" >
                            <#list fundAccountTypeArr as item>
                                <option value="${item.getKey()}">${item.getKey()} | ${item.getRemark()}</option>
                            </#list>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>币种类型:</label>
                    <div class="col-sm-3">
                        <select class="form-control" id="currencyType" name="currencyType" >
                            <#list currencyTypeList as item>
                                <option value="${item.getKey()}">${item.getKey()} | ${item.getCategory()} </option>
                            </#list>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>补单金额:</label>
                    <div class="col-sm-3">
                        <input  class="form-control" name="amount" id="amount" autocomplete="off" required maxlength="50"
                                onkeyup="this.value=this.value.replace(/[^\d\\.]/g,'');" value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>补单类型:</label>
                    <div class="col-sm-3">
                        <select class="form-control" id="type" name="type" >
                            <option value="">请选择类型</option>
                            <option value="platform_recharge">平台充值=补单充值</option>
                            <option value="platform_deduct">平台扣款=误充扣款</option>
                            <option value="platform_presentation">平台赠送</option>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label style="margin-left: 220px;color: red"> 平台充值：用户丢失某笔成功充值的订单，使用平台充值补单（平台充值的金额应为实际到账银行卡的金额）</label><br>
                    <label style="margin-left: 220px;color: red"> 平台扣款：用户获得某笔未充值的订单，使用平台扣款还原（平台扣款的金额应为错误充值的金额）</label>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>备注:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" id="remark" name="remark" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="submit" id="mysubmit" value="提交"/>
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
                    fundAccountType: $('select[name="fundAccountType"]').val(),
                    currencyType: $('select[name="currencyType"]').val(),
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
            var type = $('select[name="type"]').val();
            var remark = $('input[name="remark"]').val();

            if(isEmpty(username) || isEmpty(amount) || isEmpty(type) || isEmpty(remark))
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
        var username = $('input[name="username"]').val();
        var amount =  $('input[name="amount"]').val();
        var fundAccountType = $('select[name="fundAccountType"]').val();
        var currencyType = $('select[name="currencyType"]').val();
        var type = $('select[name="type"]').val();
        var remark = $('input[name="remark"]').val();

        if(isEmpty(username) || isEmpty(amount) || isEmpty(type) || isEmpty(remark))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return;
        }

        if(isEmpty(fundAccountType) || isEmpty(currencyType))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return;
        }

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/addPlatformSupply',
            type: 'post',
            dataType: 'json',
            data:{
                username: username,
                amount:amount,
                fundAccountType:fundAccountType.trim(),
                currencyType:currencyType.trim(),
                type:type,
                remark:remark,
            },
            success: function(result){
                console.log(result);
                if(result && result.code == 200)
                {
                    $.global.openSuccessMsg("补单成功",function(){
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
