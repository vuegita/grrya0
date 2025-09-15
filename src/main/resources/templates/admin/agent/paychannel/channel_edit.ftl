<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-支付通道管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/css/plugins/webuploader/webuploader.css"/>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/css/uploadImg.css"/>
    <script type="text/javascript" src="${STATIC_URL}/js/plugins/webuploader/webuploader.js"></script>

</head>
<style>
    .rm-img {
        width: 30%;
        height: 30%;
    }
</style>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>编辑出款信息</h5>
            <h5 style="color: red;margin-left: 20px">1.给每个网络类型配置一条出款信息</h5>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <input id="id" name="id" type="hidden" value="<#if entity?exists>${entity.id !}</#if>">


                <div  class="hidden">
                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>通道名称:</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="name" id="name" autocomplete="off" required
                                   maxlength="50"
                                   value="<#if entity?exists>${entity.name !}</#if>" <#if entity?exists>readonly</#if>/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>


                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>通道类型:</label>
                    <div class="col-sm-5">
                        <div >
                            <label><input type="radio" value="payout" name="type" id="type"
                                          <#if entity?exists>disabled</#if> <#if entity?exists><#if entity.type == 'payout'>checked</#if><#else >checked</#if>>
                                <i></i>代付　</label>
                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>通道产品:</label>
                    <div class="col-sm-5">
                        <div  >

                            <label><input type="radio" value="Coin" onclick="changeProductParams()" name="productType" id="productType" class="productType_radio"
                                          <#if entity?exists>disabled</#if>  <#if entity?exists><#if entity.productType == 'Coin'>checked</#if><#else >checked</#if>>
                                <i></i>Coin (数字货币)</label>
                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group" class="hidden">
                    <label class="control-label col-sm-2">排序:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="sort" id="sort" autocomplete="off"
                               maxlength="200"
                               value="1"/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2">备注:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="remark" id="remark" autocomplete="off"
                               maxlength="200"
                               value="1"/>
                    </div>
                </div>


                    <div class="form-group" id="shopServerDiv">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>商城地址(回调域名):</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="shopServer" id="shopServer" autocomplete="off"
                                   maxlength="100" placeholder="代收必填 | 代付可选"
                                   value=""/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>





                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">产品参数:</span></label>
                    <div class="col-sm-3">

                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>秘钥环境标识:</label>
                    <div class="col-sm-3">
                        <div >

                            <label><input type="radio" value="prod" name="env" id="env" <#if paymentInfo?exists && "prod" == paymentInfo.env>  checked="true" </#if> <#if paymentInfo?exists> <#else >checked</#if> />
                                <i></i>生产环境</label>

                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>支持币种:</label>
                        <div class="col-sm-5">
                            <div class="radio i-checks">
                                <br>

                                <#assign myCurrencyTypeMaps={}/>
                                <#if paymentInfo?exists>
                                    <#assign myCurrencyTypeArr=paymentInfo.currencyTypeArr!?split(",")>
                                    <#list myCurrencyTypeArr as item>
                                        <#assign myCurrencyTypeMaps += {"${item}" : "1"}/>
                                    </#list>
                                </#if>

                                <#list currencyTypeList as item>
                                    <#if item.getCategory() == "Crypto">
                                        <label><input type="checkbox"  name="currencyType"  value="${item.getKey()}"
                                                      checked
                                            />
                                            <i></i>${item.getKey()}
                                        </label>
                                    </#if>
                                </#list>

                            </div>

                        </div>
                    </div>




                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属代理:</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="coinAgentname" id="coinAgentname" autocomplete="off"
                                   value="<#if paymentInfo?exists>${paymentInfo.agentname !}</#if>"/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                </div>



                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>出款钱包地址:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="coinAccountAddress" id="coinAccountAddress" autocomplete="off"
                               value="<#if paymentInfo?exists>${paymentInfo.accountAddress !}</#if>"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>出款钱包私钥:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="coinAccountPrivateKey" id="coinAccountPrivateKey" autocomplete="off"
                               placeholder="如有更新请输入"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>






                <div class="form-group" >
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>网络类型:</label>
                    <div class="col-sm-5">

                        <#list networkTypeArr as item>
                            <label><input type="radio" value="${item.getKey()}"
                                          name="coinNetworkType" id="coinNetworkType"
                                          <#if paymentInfo?exists>disabled</#if>
                                        <#if paymentInfo?exists><#if paymentInfo.networkType! == '${item.getKey()}'>checked</#if></#if>>
                                <i></i>${item.getKey()}　</label>
                        </#list>

                    </div>
                </div>
                <div class="hr-line-dashed"></div>




                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>gasLimit:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="coinGasLimit" id="coinGasLimit" autocomplete="off"
                               value="<#if paymentInfo?exists>${paymentInfo.gasLimit !}</#if>"/>
                    </div>


                </div>
                <div class="form-group" style="margin-left: 200px">
                    <span class="text-danger">网络类型选 ETH (Mainnet)时gasLimit填 100000</span><br>
                    <span class="text-danger">网络类型选 BNB (Mainnet)时gasLimit填 100000</span><br>
                    <span class="text-danger">网络类型选 MATIC (Polygon)时gasLimit填 150000</span><br>
                   <span class="text-danger">网络类型选 TRX (TronGrid)时gasLimit填 40000000</span><br>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>通道状态:</label>
                    <div class="col-sm-5">
                        <div >
                            <label><input type="radio" value="enable" name="status" id="status"
                                          <#if entity?exists><#if entity.status == 'enable'>checked</#if></#if>>
                                <i></i>开启　</label>
                            <label><input type="radio" value="disable" name="status" id="status"
                                          <#if entity?exists><#if entity.status == 'disable'>checked</#if><#else >checked</#if>>
                                <i></i>关闭　</label>

<#--                            <#if isShowAction == 'true'>-->
<#--                                <label><input type="radio" value="hidden" name="status" id="status"-->
<#--                                              <#if entity?exists><#if entity.status == 'hidden'>checked</#if><#else >checked</#if>>-->
<#--                                    <i></i>隐藏　</label>-->
<#--                            </#if>-->
                        </div>
                    </div>


                </div>
                <div class="form-group">
                    <label class="control-label col-sm-5"><span class="text-danger">通道状态请选择开启,保存后重新选择开启再保存一次才生效</span></label>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>谷歌验证码:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="googleCode" id="googleCode" autocomplete="off" placeholder=""
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               value="" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="button" id="btn_submit" value="保存"/>
                        <button class="btn btn-white" type="button" onclick="window.close();">取消</button>
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>

<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/lib/jquery.ajaxfileupload.js"></script>
<script type="text/javascript">
    $(function () {

        changeProductParams()
        //确认保存
        $("#btn_submit").click(function () {
            var data = null;
            var productType = $('input[name="productType"]:checked').val()

            if (productType === 'Tajpay') {
                data = getTajpayParam();
            }
            else if (productType === 'Bank') {
                data = getBankParam();
            }
            else if (productType === 'UPI') {
                data = getIndiaUPIParam();
            }
            else if (productType === 'Wallet') {
                data = getWalletParam();
            }
            else if (productType === 'Coin') {
                data = getCoinParam();
            }


            if (data == null){
                console.log('未知产品类型for ' + productType);
                //$.global.openErrorMsg('未知产品类型for ' + productType);
                return;
            }

            $.ajax({
                type: "post",
                url: '/alibaba888/agent/editPayChannel',
                data: data,
                dataType: "json",
                success: function (data) {
                    if (data != null && data.code == 200) {
                        $.global.openSuccessMsg("保存成功");
                        setTimeout(function () {
                            window.close();
                        }, 500);
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

    function changeProductParams() {

        var productType = $('input:radio[name="productType"]:checked').val();

        // if(isEmpty(productType))
        // {
        //     $('input:radio[name="productType"]:checked').val(true);
        //     $("#stateWFalse").attr(“checked”,true);
        // }

        $("#tajpayDIV").addClass("hidden");
        $("#bankDIV").addClass("hidden");
        $("#indiaUPIDIV").addClass("hidden");
        $("#walletDIV").addClass("hidden");
        $("#coinDIV").addClass("hidden");

        if(productType === 'Tajpay'){
            $("#tajpayDIV").removeClass("hidden");
        }
        else if(productType === 'Bank'){
            $("#bankDIV").removeClass("hidden");
        }
        else if(productType === 'UPI'){
            $("#indiaUPIDIV").removeClass("hidden");
        }
        else if(productType === 'Wallet'){
            $("#walletDIV").removeClass("hidden");
        }
        else if(productType === 'Coin'){
            $("#coinDIV").removeClass("hidden");
        }
    }

    function getTajpayParam() {
        var type = $('input[name="type"]:checked').val();

        var name = $("#name").val();
        var env = $('input[name="env"]:checked').val();
        var shopServer = $("#shopServer").val();
        var key = $("#key").val();
        var salt = $("#salt").val();
        var targetType = $('input[name="targetType"]:checked').val();

        if (isEmpty(name) || isEmpty(key) || isEmpty(salt) || isEmpty(env) || isEmpty(targetType)){
            $.global.openErrorMsg('* 号必填参数不能为空');
            return null;
        }

        if (type == "payin") {
            if (!checkURL(shopServer)){
                $.global.openErrorMsg('商城地址格式错误');
                return null;
            }
        }

        return {
            id: $("#id").val(),
            name: name,
            status: $('input[name="status"]:checked').val(),
            type: $('input[name="type"]:checked').val(),
            productType: $('input[name="productType"]:checked').val(),
            remark: $("#remark").val(),
            sort: $("#sort").val(),
            secretInfo: JSON.stringify({
                //特殊参数
                env: env,
                shopServer: shopServer,
                key: key.trim(),
                salt: salt.trim(),
                targetType:targetType.trim(),
            }),
        };
    }

    function getBankParam() {
        var type = $('input[name="type"]:checked').val();

        var name = $("#name").val();
        var env = $('input[name="env"]:checked').val();
        var shopServer = $("#shopServer").val();
        var bankName = $("#bankName").val();
        var bankCode = $("#bankCode").val();
        var bankAccount = $("#bankAccount").val();

        if (isEmpty(name) || isEmpty(bankName) || isEmpty(bankCode) || isEmpty(env) || isEmpty(bankAccount)){
            $.global.openErrorMsg('* 号必填参数不能为空');
            return null;
        }

        if (type == "payin") {
            if (!checkURL(shopServer)){
                $.global.openErrorMsg('商城地址格式错误');
                return null;
            }
        }

        return {
            id: $("#id").val(),
            name: name,
            status: $('input[name="status"]:checked').val(),
            type: $('input[name="type"]:checked').val(),
            productType: $('input[name="productType"]:checked').val(),
            remark: $("#remark").val(),
            sort: $("#sort").val(),
            secretInfo: JSON.stringify({
                //特殊参数
                env: env,
                shopServer: shopServer,
                bankName: bankName.trim(),
                bankCode: bankCode.trim(),
                bankAccount:bankAccount.trim(),
            }),
        };
    }


    function getIndiaUPIParam() {
        var type = $('input[name="type"]:checked').val();

        var name = $("#name").val();
        var env = $('input[name="env"]:checked').val();
        var shopServer = $("#shopServer").val();
        var upi = $("#indiaUPIAddress").val();

        if (isEmpty(name) || isEmpty(upi) || isEmpty(env)){
            $.global.openErrorMsg('* 号必填参数不能为空');
            return null;
        }

        if (type == "payin") {
            if (!checkURL(shopServer)){
                $.global.openErrorMsg('商城地址格式错误');
                return null;
            }
        }

        return {
            id: $("#id").val(),
            name: name,
            status: $('input[name="status"]:checked').val(),
            type: $('input[name="type"]:checked').val(),
            productType: $('input[name="productType"]:checked').val(),
            remark: $("#remark").val(),
            sort: $("#sort").val(),
            secretInfo: JSON.stringify({
                //特殊参数
                env: env,
                shopServer: shopServer,
                upi: upi.trim(),
            }),
        };
    }

    function getWalletParam() {
        var type = $('input[name="type"]:checked').val();

        var name = $("#name").val();
        var env = $('input[name="env"]:checked').val();
        var shopServer = $("#shopServer").val();
        var accountid = $("#walletAccountid").val();
        var walletType = $("#walletType").val();

        if (isEmpty(name) || isEmpty(accountid) || isEmpty(env) || isEmpty(walletType)){
            $.global.openErrorMsg('* 号必填参数不能为空');
            return null;
        }

        if (type == "payin") {
            if (!checkURL(shopServer)){
                $.global.openErrorMsg('商城地址格式错误');
                return null;
            }
        }

        return {
            id: $("#id").val(),
            name: name,
            status: $('input[name="status"]:checked').val(),
            type: $('input[name="type"]:checked').val(),
            productType: $('input[name="productType"]:checked').val(),
            remark: $("#remark").val(),
            sort: $("#sort").val(),
            secretInfo: JSON.stringify({
                //特殊参数
                env: env,
                shopServer: shopServer,
                accountid: accountid.trim(),
                walletType: walletType.trim(),
            }),
        };
    }

    function getCoinParam() {

        var type = $('input[name="type"]:checked').val();

        var name = $("#name").val();
        var env = $('input[name="env"]:checked').val();
        var shopServer = $("#shopServer").val();

        var accountPrivateKey = $("#coinAccountPrivateKey").val();
        var accountAddress = $("#coinAccountAddress").val();

        var coinAgentname = $("#coinAgentname").val(); //代币地址
        // var currencyType = $('input[name="coinCurrencyType"]:checked').val();
        var networkType = $('input[name="coinNetworkType"]:checked').val();
        var gasLimit = $("#coinGasLimit").val();
        var googleCode = $('input[name="googleCode"]').val();

        var currencyTypeValues = $('input[name=currencyType]:checked').map(function(){
            return $(this).val();
        }).get();

//isEmpty(name) ||  || isEmpty(coinAgentname)
        if ( isEmpty(accountAddress)  || isEmpty(networkType) || isEmpty(env) || isEmpty(currencyTypeValues)){
            $.global.openErrorMsg('* 号必填参数不能为空');
            return null;
        }

        var currencyTypeArr = currencyTypeValues.toString();

        if (isEmpty(gasLimit) || gasLimit <= 0){
            $.global.openErrorMsg('gasLimit 参数不能为空');
            return null;
        }

        if (type == "payin") {
            if (!checkURL(shopServer)){
                $.global.openErrorMsg('商城地址格式错误');
                return null;
            }
            // 代收不用
            accountPrivateKey = '';
        }
        else
        {
            if (isEmpty(accountPrivateKey)){
                //$.global.openErrorMsg('账户私钥不能为空');
                //return null;
                accountPrivateKey = "";
            }
        }

        return {
            id: $("#id").val(),
            name: name,
            status: $('input[name="status"]:checked').val(),
            type: $('input[name="type"]:checked').val(),
            productType: $('input[name="productType"]:checked').val(),
            remark: $("#remark").val(),
            sort: $("#sort").val(),
            googleCode: googleCode.trim(),
            secretInfo: JSON.stringify({
                //特殊参数
                env: env,
                shopServer: shopServer,

                accountPrivateKey:accountPrivateKey.trim(),
                accountAddress:accountAddress.trim(),

                agentname:coinAgentname.trim(),
                currencyTypeArr: currencyTypeArr.trim(),
                networkType: networkType.trim(),
                gasLimit:gasLimit.trim(),

            }),
        };
    }

    function checkURL(url){
        // var myReg=/^((https|http|ftp|rtsp|mms){0,1}(:\/\/){0,1})www\.(([A-Za-z0-9-~]+)\.)+([A-Za-z0-9-~\/])+$/i;
        // if(myReg.test(url)){
        //     return true;
        // }else{
        //     return false;
        // }

        if (url.indexOf("http://") !== -1 || url.indexOf("https://") !== -1){
            return true;
        }
        return false;
    }


</script>
</body>
</html>
