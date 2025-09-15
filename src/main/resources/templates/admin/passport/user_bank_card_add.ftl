<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/css/plugins/webuploader/webuploader.css"/>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/css/uploadImg.css"/>
    <script type="text/javascript" src="${STATIC_URL}/js/plugins/webuploader/webuploader.js"></script>

</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>添加银行卡</h5>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <#if cardInfo??>
                    <input type="hidden" id="cardid" name="cardid" value="${cardInfo.id}" autocomplete="off" required maxlength="50"/>
                </#if>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>用户名:</label>
                    <div class="col-sm-3">
                        <input class="form-control" id="username" name="username" value="<#if cardInfo??> ${cardInfo.username} </#if>" autocomplete="off" required maxlength="50"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>账户名称:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" id="cardName" name="cardName" value="<#if cardInfo??>${cardInfo.name}</#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>账户类型:</label>
                    <div class="col-sm-3">
                        <div class="radio i-checks">
                            <label><input type="radio" onclick="changeType()" name="type" id="type" value="bank"
                                           <#if cardInfo.type=="bank"> checked </#if> />
                                <i></i>Bank
                            </label>
<#--                            <label><input type="radio" onclick="changeType()" name="type" id="type" value="upi" /> <i></i>UPI</label>-->
                            <label><input type="radio" onclick="changeType()" name="type" id="type" value="wallet"   <#if cardInfo.type=="wallet"> checked </#if>/> <i></i>Wallet</label>
                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>Account:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" id="account" name="account" value="<#if cardInfo??> ${cardInfo.account} </#if>"  autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="" id="ifscDIV">
                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>ifsc:</label>
                        <div class="col-sm-3">
                            <input type="text" class="form-control" id="ifsc" name="ifsc" value="<#if cardInfo??> ${cardInfo.ifsc} </#if>" autocomplete="off" maxlength="255"
                                   value=""/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>受益人名称:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" id="beneficiaryName" name="beneficiaryName" value="<#if cardInfo??> ${cardInfo.beneficiaryName} </#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>受益人邮箱:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" id="beneficiaryEmail" name="beneficiaryEmail" value="<#if cardInfo??> ${cardInfo.beneficiaryEmail} </#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>受益人手机:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" id="beneficiaryPhone" name="beneficiaryPhone" value="<#if cardInfo??> ${cardInfo.beneficiaryPhone} </#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>ID:</label>
                    <div class="col-sm-3">
                        <input type="text" class="form-control" id="idcard" name="idcard" value="<#if cardInfo??> ${cardInfo.remark} </#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group">
                    <label class="control-label col-sm-2">状态:</label>
                    <div class="col-sm-3">
                        <div class="radio i-checks">
                            <#if !cardInfo??>
                                <label><input type="radio" name="status" value="enable" checked/> <i></i>启用</label>
                                <label><input type="radio" name="status" value="disable" /> <i></i>禁用</label>
                            <#else>
                                <label><input type="radio" name="status" value="enable" <#if cardInfo.status == "enable"> checked </#if> /> <i></i>启用</label>
                                <label><input type="radio" name="status" value="disable" <#if cardInfo.status == "disable"> checked </#if> /> <i></i>禁用</label>
                            </#if>
                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="button" id="mysubmit" value="保存"/>
                        <button class="btn btn-white" type="button" onclick="window.history.back();">取消</button>
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
<script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/lib/jquery.ajaxfileupload.js"></script>
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
            var account =  $('input[name="account"]').val();
            var cardName = $('input[name="cardName"]').val();
            var ifsc =  $('input[name="ifsc"]').val();
            var type = $('input[name="type"]').val();
            var beneficiaryName = $('input[name="beneficiaryName"]').val();
            var beneficiaryEmail = $('input[name="beneficiaryEmail"]').val();
            var beneficiaryPhone = $('input[name="beneficiaryPhone"]').val();
            var idcard=$('input[name="idcard"]').val();
            var status = $('input[name="status"]').val();


            if(isEmpty(username) || isEmpty(cardName) || isEmpty(account) || isEmpty(ifsc) || isEmpty(type) || isEmpty(idcard) || isEmpty(status))
            {
                $.global.openErrorMsg('* 号必填参数不能为空');
                return false;
            }

            if(isEmpty(beneficiaryName) || isEmpty(beneficiaryEmail) || isEmpty(beneficiaryPhone))
            {
                $.global.openErrorMsg('* 号必填参数不能为空');
                return false;
            }

            $('#myModalDel #tips').text('确定提交吗？');
            $('#myModalDel').modal();

            return false;
        });

        $('#delete_submit').click(function () {
            addData();
        });

    });

    function changeType() {
        alert();
        // var type = $('input:radio[name="type"]:checked').val();
        //
        // console.log("type = " + type)
        //
        // if(type === 'bank'){
        //     $("#ifscDIV").removeClass("hidden");
        // }
        // else
        // {
        //     $("#ifscDIV").addClass("hidden");
        // }
    }

    function addData() {
        var cardid = $('input[name="cardid"]').val();
        var username = $('input[name="username"]').val();
        var account =  $('input[name="account"]').val();
        var cardName = $('input[name="cardName"]').val();
        var ifsc =  $('input[name="ifsc"]').val();
        var type = $('input[name="type"]:checked').val();
        var beneficiaryName = $('input[name="beneficiaryName"]').val();
        var beneficiaryEmail = $('input[name="beneficiaryEmail"]').val();
        var beneficiaryPhone = $('input[name="beneficiaryPhone"]').val();
        var idcard=$('input[name="idcard"]').val();
        var status = $('input[name="status"]:checked').val();

        if(isEmpty(username) || isEmpty(cardName) || isEmpty(account) || isEmpty(ifsc) || isEmpty(type) || isEmpty(idcard)  || isEmpty(status))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return false;
        }

        if(isEmpty(beneficiaryName) || isEmpty(beneficiaryEmail) || isEmpty(beneficiaryPhone))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return false;
        }

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/addUserBankCardPage',
            type: 'post',
            dataType: 'json',
            data:{
                cardid:cardid,
                username: username,
                cardName:cardName,
                account:account,
                ifsc:ifsc,
                type:type,
                beneficiaryName:beneficiaryName,
                beneficiaryEmail:beneficiaryEmail,
                beneficiaryPhone:beneficiaryPhone,
                idcard:idcard,
                status:status
            },
            success: function(result){
                console.log(result);
                if(result && result.code == 200)
                {
                    $.global.openSuccessMsg("添加成功",function(){
                        window.history.go(-1);
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
