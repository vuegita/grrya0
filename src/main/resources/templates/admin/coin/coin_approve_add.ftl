<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-用户管理</title>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>添加授权</h5>

            <p style="color: blue"> 　</p>
<#--            <p  style="color: green">1. 最低和最高价格都要为5的倍数!</p>-->
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.id}</#if>"/>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>钱包地址:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="address" id="address" value="" autocomplete="off" required maxlength="50"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>合约ID:</label>-->
<#--                    <div class="col-sm-3">-->
<#--                        <input class="form-control" name="contractid" id="contractid" value="" autocomplete="off" required maxlength="50"/>-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属网络:</label>
                    <div class="col-sm-3">
                        <select class="form-control" id="networkType" name="networkType" >
                            <option value="">请选择业务场景</option>
                            <#list networkTypeArr as networkType>
                                <option value="${networkType.key}" >${networkType.key}</option>
                            </#list>
                        </select>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属币种:</label>
                    <div class="col-sm-3">
                        <select class="form-control" id="currency" name="currency" >
                            <option value="">请选择业务场景</option>
                            <#list cryptoCurrencyArr as cryptoCurrency>
                                <option value="${cryptoCurrency.key}" >${cryptoCurrency.key}</option>
                            </#list>
                        </select>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>



                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>业务场景:</label>
                    <div class="col-sm-3">
                        <select class="form-control" id="fromtype" name="fromtype" >
                            <option value="">请选择业务场景</option>
                            <#list approveFromTypeArr as group>
                                <option value="${group.key}" >${group.key}</option>
                            </#list>
                        </select>
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


        $('.usertypeLabel').on("click",function() {
            var input = $(this).find("input");

            var userType = $(input).data('usertype');
            if(userType == 'staff')
            {
                $("#agentNameDIV").show();
            }
            else
            {
                $("#agentNameDIV").hide();
            }
        });


        //确认保存
        $("#submitBtn").click(function () {

            var address = $('input[name="address"]').val();
           // var contractid = $('input[name="contractid"]').val();
            var fromtype = $('select[name="fromtype"]').val();

            var networkType = $('select[name="networkType"]').val();
            var currency = $('select[name="currency"]').val();

            if ( isEmpty(address) || isEmpty(networkType) || isEmpty(currency) || isEmpty(fromtype)){
                $.global.openErrorMsg('* 号必填参数不能为空');
                return;
            }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/doAddTokenApprove",
                data: {
                    address: address,
                    networkType:networkType,
                    currency:currency,
                    fromType:fromtype,

                },
                dataType: "json",
                success: function (data) {
                    debugger
                    if (data != null && data.code == 200) {
                        $.global.openSuccessMsg("保存成功",function(){
                            window.close();
                        });
                    } else {
                        $.global.openErrorMsg(data.msg);
                    }
                },
                error: function (err) {
                    debugger
                    console.log(err)
                    $.global.openErrorMsg('保存失败，请重试');
                }
            })
        });
    });

</script>
</body>
</html>
