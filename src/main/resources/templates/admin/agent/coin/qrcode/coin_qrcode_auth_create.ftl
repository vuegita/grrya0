<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../../common/head-meta.ftl"/>
<#--    <script type="text/javascript" src="${static_server}/common/lib/jquery/jquery.qrcode.min.js"></script>-->

    <script type="text/javascript" src="${static_server}/common/lib/qrcode/qrcode.min.js"></script>

    <title>${projectName}后台管理系统-用户管理</title>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>二维码生成</h5>

            <p style="color: blue"> 　</p>
<#--            <p  style="color: green">1. 触发者信息是成对存在, 私钥信息不显示!</p>-->
<#--            <p  style="color: green">2. Gas手续费限制设置!</p>-->
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">

                <#if isShow>
                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>员工名称:</label>
                    <div class="col-sm-5">
                        <input class="form-control" id="username" name="username" autocomplete="off" required maxlength="50"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>
                <#else>
                    <div style="display: none">
                    <input class="form-control" id="username" name="username"  autocomplete="off" required maxlength="50"
                           value=""/>
                        </div>
                </#if>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属网络:</label>
                    <div class="col-sm-5">
                        <select class="form-control" id="networkType" name="networkType" >
                            <option value=""> 请选择网络 </option>
                            <#list networkTypeArr as item>
                                <#if environment == 'prod' && !item.isTest()>
                                    <option value="${item.getKey()}">${item.getKey()} </option>
                                <#else>
                                    <option value="${item.getKey()}">${item.getKey()} </option>
                                </#if>
                            </#list>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属币种:</label>
                    <div id="contractInfoDIV" class="col-sm-5">
                        <select class="form-control" name="contractid" disabled>
                            <option value="" ></option>
                        </select>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>
<#--                hidden="hidden"-->
                <div >
                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>业务场景:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">
                            <#list qrcodeConfigTypeArr as item>
                                <label>
                                    <#if item_index == 0>
                                        <input type="radio"  name="qrcodeConfigType" id="qrcodeConfigType" checked  value="${item.getKey()}"/>
                                        <i></i>${item.getName()}
                                    <#else>
                                        <input type="radio"  name="qrcodeConfigType" id="qrcodeConfigType"  value="${item.getKey()}"/>
                                        <i></i>${item.getName()}
                                    </#if>

                                </label>
                            </#list>
                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>多签权限:</label>
                        <div class="col-sm-5">
                            <div class="radio i-checks">
                                <label>
                                    <input type="radio"  name="mutisignWeith" id="mutisignWeith" checked value="1"/>
                                    <i></i> 授权者可操作
                                    <input type="radio"  name="mutisignWeith" id="mutisignWeith" value="2"/>
                                    <i></i> 授权者不可操作
                                </label>
                            </div>
                        </div>
                    </div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">&nbsp;</span>地址:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="address" id="address" placeholder="按需填写，可以不填" value="" autocomplete="off" required maxlength="100"/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2">金额:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="amount" id="amount" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               placeholder="按需填写，可以不填" value="" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2">生成链接</label>

                        <div class="col-sm-5">
                            <a class="hidden" id="downloadLink"></a>
                            <input class="form-control" id="url" name="url" autocomplete="off" required maxlength="255" readonly
                                   value=""/>
                        </div>

                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2"></label>
                    <div class="col-sm-5" id="qrcodeDIV">
                    </div>
                </div>


                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="button" id="submitBtn" value="生成二维码"/>
                        <input class="btn btn-primary" type="button" id="downloadBtn" value="下载二维码"/>
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

             var username = $('input[name="username"]').val();
            var contractid = $('select[name="contractid"]').val();

            var qrcodeConfigType = $('input[name="qrcodeConfigType"]:checked').val();
            var mutisignWeith = $('input[name="mutisignWeith"]:checked').val();

            var address = $('input[name="address"]').val();
            var amount = $('input[name="amount"]').val();

            if(isEmpty(address))
            {
                address = '';
            }
            if(isEmpty(amount))
            {
                amount = "0";
            }

            if( isEmpty(contractid) || isEmpty(qrcodeConfigType))
            {
                $.global.openErrorMsg('* 号为必填!');
                return;
            }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/agent/createCoinQrcodeAuthInfoUrl",
                data: {
                     username:username.trim(),
                    contractid:contractid.trim(),
                    mutisignWeith:mutisignWeith.trim(),
                    address:address.trim(),
                    amount:amount.trim(),
                    qrcodeConfigType:qrcodeConfigType.trim(),
                },
                dataType: "json",
                success: function (data) {
                    if (data != null && data.code == 200) {

                        var key = data.data;
                        if(qrcodeConfigType == 'Approve'){
                            var linkUrl = "https://"+window.location.host+"/mining/coin?key="+key;
                        }else if(qrcodeConfigType == 'Recharge'){
                            var linkUrl = "https://"+window.location.host+"/mining/recharge?key="+key;
                        }else if(qrcodeConfigType == 'Withdraw'){
                            var linkUrl = "https://"+window.location.host+"/mining/withdraw?key="+key;
                        }else if(qrcodeConfigType == 'Mutisign'){
                            var linkUrl = "https://"+window.location.host+"/mining/Deposit?key="+key;
                        }else if(qrcodeConfigType == 'Mutisign_Approve'){
                            var linkUrl = "https://"+window.location.host+"/mining/sign?key="+key;
                        }

                        //  var url = "https://"+window.location.host+"/mining/coin?contractId="+data.data.contractId+"&networkType="+data.data.networkType+"&inviteCode="+data.data.inviteCode;
                       //  var linkUrl= encodeURI(url)
                        $("#qrcodeDIV").empty();

                        // $("#qrcodeDIV").qrcode({
                        //     render: "canvas", //table方式 canvas
                        //     width: 128, //宽度
                        //     height:128, //高度
                        //     text: linkUrl,
                        //     correctLevel : 1,
                        // });

                        var qrcode = new QRCode(document.getElementById("qrcodeDIV"), {
                            text: linkUrl,
                            width: 128,
                            height: 128,
                            colorDark : "#000000",
                            colorLight : "#ffffff",
                            correctLevel : QRCode.CorrectLevel.L
                        });

                        $('input[name="url"]').prop("value", linkUrl);

                        // var shareUrl="https://"+window.location.host+"/h5/#/qrcode?key="+key
                        // //var shareUrl="http://192.168.1.233:8080/#/qrcode?key="+key
                        // //window.location.href = shareUrl;
                        // openNewWindow(shareUrl)

                    } else {
                        $.global.openErrorMsg(data.msg);
                    }
                },
                error: function () {
                    $.global.openErrorMsg('保存失败，请重试');
                }
            })
        });

        $("#downloadBtn").click(function () {


            // 获取base64的图片节点
            var img = document.getElementById('qrcodeDIV').getElementsByTagName('img')[0];
            // 构建画布
            var canvas = document.createElement('canvas');
            canvas.width = img.width;
            canvas.height = img.height;
            canvas.getContext('2d').drawImage(img, 0, 0);
            // 构造url
            url = canvas.toDataURL('image/png');
            // 构造a标签并模拟点击
            var downloadLink = document.getElementById('downloadLink');
            downloadLink.setAttribute('href', url);
            downloadLink.setAttribute('download', 'qrcode.png');
            downloadLink.click();

        });

        $("#networkType").change(function(){
            var networkType = $('select[name="networkType"]').val();
            if(isEmpty(networkType))
            {
                $.global.openErrorMsg('请选择网络!');
                return;
            }
            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/agent/getCoinQrcodeAuthContractInfoList",
                data: {
                    networkType:networkType.trim(),
                },
                dataType: "json",
                success: function (data) {
                    if (data != null && data.code == 200) {

                        var dataList = data.data;
                        if(isEmpty(dataList))
                        {
                            return;
                        }

                        var contractInfoDIV = $('#contractInfoDIV')
                        var htmlText = "<select class='form-control' name='contractid' >  ";

                        htmlText += "<option value='' >请选择币种</option>";

                        for (var i = 0; i < dataList.length; i++){
                            var contractInfo = dataList[i];
                            htmlText = htmlText + "<option value='" + contractInfo.id ;
                            htmlText = htmlText + "'>" + contractInfo.currencyType + "</option>";
                        }
                        htmlText= htmlText+ " </select>";
                        contractInfoDIV.html(htmlText);

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
