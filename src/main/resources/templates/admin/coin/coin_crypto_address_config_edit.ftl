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
            <h5>编辑地址</h5>

        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.id}</#if>"/>


                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>钱包地址:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="address" id="address"
                               value="<#if entity?exists> ${entity.address} </#if>" autocomplete="off" required maxlength="200"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>钱包私钥:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="privateKey" id="privateKey"
                               value="<#if entity?exists> ${entity.privateKey} </#if>" autocomplete="off" required maxlength="200"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>



                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>合约网络:</label>
                    <div class="col-sm-10">
                        <div class="radio i-checks">

                            <#assign currentChainType=''/>
                            <#list networkTypeArr as item>

                                <#if item_index == 0>
                                    <br>
                                    <#assign currentChainType=item.getChainType().getKey()/>
                                </#if>

                                <#if currentChainType != item.getChainType().getKey()>
                                    <br>
                                    <br>
                                    <#assign currentChainType=item.getChainType().getKey()/>
                                </#if>

                                <#if environment == 'prod' && !item.isTest()>
                                    <label>
                                        <input type="radio"  name="networkType" id="networkType" value="${item.getKey()}"
                                                <#if entity?exists> disabled </#if>
                                                <#if entity?exists && entity.networkType == item.getKey()> checked </#if>/>
                                        <i></i>${item.getKey()}
                                    </label>
                                <#else>
                                    <label>
                                        <input type="radio"  name="networkType" id="networkType" value="${item.getKey()}"
                                                <#if entity?exists> disabled </#if>
                                                <#if entity?exists && entity.networkType == item.getKey()> checked </#if>/>
                                        <i></i>${item.getKey()}
                                    </label>
                                </#if>
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

        //确认保存
        $("#submitBtn").click(function () {

            var id = $('input[name="id"]').val();
            var address = $('input[name="address"]').val();
            var privateKey = $('input[name="privateKey"]').val();


            var networkType = $('input[name="networkType"]:checked').val();

            var status = $('input[name="status"]:checked').val();

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/updateCoinCryptoAddressConfigInfo",
                data: {
                    id:id.trim(),
                    address:address.trim(),
                    privateKey:privateKey.trim(),

                    networkType:networkType.trim(),
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
