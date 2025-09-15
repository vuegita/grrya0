<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <script type="text/javascript" src="${static_server}/common/lib/qrcode/qrcode.min.js"></script>
    <title>${projectName}商户后台系统-我的概况</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">
        <div class="ibox-title">
            <h5>我的概况</h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 当前钱包表示当前代理余额(可用于发红包), 不能提现!</p>
            <p  style="color: green">2. 钱包流水不记录每日统计，但可在【订单管理 -> 金额变动明细】查看!</p>
            <p  style="color: green">3. 每日统计只统计其下级会员所有的汇总!</p>
            <p  style="color: green">4. 代理无推广链接, 员工和会员才有推广链接!</p>

            <#if !isAgentLogin?exists>
            <input class="form-control" name="shareUrl" id="shareUrl" readonly autocomplete="off"
                   required maxlength="10"  type="text" style="display:none"
                   value="${inviteCode!}" />
            <div class="panel-body">
                <table id="tabls" class="table table-striped table-bordered">
                    <tr >
                        <#if isRunningMode!="crypto"> <th style="text-align:center;width: 250px">推广链接</th></#if>
                        <#if isRunningMode=="crypto"> <th style="text-align:center;width: 250px">DeFi挖矿推广链接</th> </#if>
                        <a class="hidden" id="downloadLink"></a>
                        <td class="textcopyshareUrl" id="textcopyshareUrl" style="color: blue;font-size: 16px;font-weight: bold"></td>
                        <th style="text-align:center;width: 250px"><input class="btn btn-primary" id="copyshareUrl" type="submit" value="复制推广链接"/></th>

                        <td class="qrcodeDIV" id="qrcodeDIV" > </td>
                        <th style="text-align:center;width: 250px"><input class="btn btn-primary" id="copyshareUrlQrcode" type="submit" value="下载推广二维码"/></th>
                        <#--                            <td style="width: 330px;color: blue"></td>-->
                    </tr>

                </table>
            </div>

            <div class="panel-body">
                <table id="tabls" class="table table-striped table-bordered">
                    <tr >
                        <#if isRunningMode!="crypto"> <th style="text-align:center;width: 250px">游戏推广链接</th></#if>
                        <#if isRunningMode=="crypto"> <th style="text-align:center;width: 250px">DeFi挖矿推广链接</th> </#if>
                        <a class="hidden" id="downloadLinkb"></a>
                        <td class="textcopyshareUrlb" id="textcopyshareUrlb" style="color: blue;font-size: 16px;font-weight: bold"></td>
                        <th style="text-align:center;width: 250px"><input class="btn btn-primary" id="copyshareUrlb" type="submit" value="复制推广链接"/></th>

                        <td class="qrcodeDIVb" id="qrcodeDIVb" > </td>
                        <th style="text-align:center;width: 250px"><input class="btn btn-primary" id="copyshareUrlQrcodeb" type="submit" value="下载推广二维码"/></th>
                        <#--                            <td style="width: 330px;color: blue"></td>-->
                    </tr>

                </table>
            </div>



            <#if isRunningMode=="crypto">
            <div class="panel-body">
                <table id="tabls" class="table table-striped table-bordered">
                    <tr >
                        <th style="text-align:center;width: 250px">天眼查推广链接</th>
                        <a class="hidden" id="downloadLink1"></a>
                        <td class="textcopyshareUrl1" id="textcopyshareUrl1" style="color: blue;font-size: 16px;font-weight: bold">

                        </td>
                        <th style="text-align:center;width: 250px"><input class="btn btn-primary" id="copyshareUrl1" type="submit" value="复制推广链接"/></th>

                        <td class="qrcodeDIV1" id="qrcodeDIV1" > </td>
                        <th style="text-align:center;width: 250px"><input class="btn btn-primary" id="copyshareUrlQrcode1" type="submit" value="下载推广二维码"/></th>
                        <#--                            <td style="width: 330px;color: blue"></td>-->
                    </tr>

                </table>
            </div>




            <div class="panel-body">
                <table id="tabls" class="table table-striped table-bordered">
                    <tr >
                        <th style="text-align:center;width: 250px">币安推广链接</th>
                        <a class="hidden" id="downloadLink2"></a>
                        <td class="textcopyshareUrl2" id="textcopyshareUrl2" style="color: blue;font-size: 16px;font-weight: bold">

                        </td>
                        <th style="text-align:center;width: 250px"><input class="btn btn-primary" id="copyshareUrl2" type="submit" value="复制推广链接"/></th>

                        <td class="qrcodeDIV2" id="qrcodeDIV2" > </td>
                        <th style="text-align:center;width: 250px"><input class="btn btn-primary" id="copyshareUrlQrcode2" type="submit" value="下载推广二维码"/></th>
                        <#--                            <td style="width: 330px;color: blue"></td>-->
                    </tr>

                </table>
            </div>
            </#if>




        </div>
        </#if>

        <div class="ibox-content">



<#if isShowAgentWallet=="true">

            <div class="panel panel-danger">
                <div class="panel-heading">
                    <h3 class="panel-title">代理钱包</h3>
                </div>

                <div class="panel-body">
                    <table id="tabls" class="table table-striped table-bordered">
                        <tr >
                            <th style="text-align:center;width: 250px">代理账户余额</th>
                            <td style="color: blue">${userMoney.balance!}</td>
                            <th style="text-align:center;width: 250px"></th>
                            <td style="width: 330px;color: blue"></td>
                        </tr>

                    </table>
                    <br>
                </div>
            </div>
</#if>

            <div class="panel panel-danger">
                <div class="panel-heading">
                    <h3 class="panel-title">今日用户汇总</h3>
                </div>

                <div class="panel-body">

                    <table id="tabls" class="table table-striped table-bordered">
                        <tr >
                            <th style="text-align:center;width: 250px">会员总人数</th>
                            <td style="color: blue">${todayUserStatsInfo.totalMemberRegCount!}</td>
                            <th style="text-align:center;width: 250px">今日注册人数</th>
                            <td style="width: 330px;color: blue">${todayUserStatsInfo.todayMemberRegCount!}</td>
                        </tr>

                        <tr >
                            <th style="text-align:center;width: 250px">今日首充人数 | 首充金额</th>
                            <td style="color: blue">${todayUserStatsInfo.todayFirstRechargeCount!} | ${todayUserStatsInfo.todayFirstRechargeAmount!}</td>
                            <th style="text-align:center;width: 250px">今日裂变人数</th>
                            <td style="color: blue">${todayUserStatsInfo.todayMemberSplitCount!}</td>
                        </tr>

                        <#if isShowContent=="true">
                            <tr >
                                <th style="text-align:center;width: 250px">当前活跃人数</th>
                                <td style="color: blue">${activeUserCount !}</td>
                                <th style="text-align:center;width: 250px">今日活跃人数</th>
                                <td style="color: blue">${todayActiveUserCount!}</td>
                            </tr>
                        </#if>

                        <tr >
                            <th style="text-align:center;width: 250px">会员总余额</th>
                            <td style="color: blue">${todayUserStatsInfo.totalBalance!}</td>
                            <th style="text-align:center;width: 250px"></th>
                            <td style="color: blue"></td>
                        </tr>



                    </table>
                    <br>

                </div>
            </div>


            <#if isShowContent=="true">
            <#list gameInfoList as rootItem>

                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">今日 ${rootItem.title} 汇总</h3>
                    </div>

                    <div class="panel-body">

                        <div class="form-group">

                        </div>
                        <table id="tabls" class="table table-striped table-bordered">
                            <tr >
                                <th style="text-align:center;width: 250px">投注总数</th>
                                <td style="color: blue">${rootItem.businessDay.betCount!}</td>
                                <th style="text-align:center;width: 250px">投注总额</th>
                                <td style="width: 330px;color: blue">${rootItem.businessDay.betAmount!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">中奖总数</th>
                                <td style="color: blue">${rootItem.businessDay.winCount!}</td>
                                <th style="text-align:center;width: 250px">中奖总额</th>
                                <td style="width: 330px;color: blue">${rootItem.businessDay.winAmount!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">手续费总额</th>
                                <td style="color: blue">${rootItem.businessDay.feemoney!}</td>
                                <th style="text-align:center;width: 250px"></th>
                                <td style="width: 330px;color: blue"></td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">盈亏总额</th>
                                <td style="color: blue">${totalProfit!}</td>
                                <th style="text-align:center;width: 250px"></th>
                                <td style="width: 330px;color: blue"></td>
                            </tr>

                        </table>


                        <br>

                    </div>
                </div>

            </#list>

                <#if lotteryBusinessDay??>

                <div class="panel panel-danger">
                        <div class="panel-heading">
                            <h3 class="panel-title">今日红绿汇总</h3>
                        </div>

                        <div class="panel-body">

                            <div class="form-group">

                            </div>
                            <table id="tabls" class="table table-striped table-bordered">
                                <tr >
                                    <th style="text-align:center;width: 250px">投注总数</th>
                                    <td style="color: blue">${lotteryBusinessDay.betCount!}</td>
                                    <th style="text-align:center;width: 250px">投注总额</th>
                                    <td style="width: 330px;color: blue">${lotteryBusinessDay.betAmount!}</td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">中奖总数</th>
                                    <td style="color: blue">${lotteryBusinessDay.winCount!}</td>
                                    <th style="text-align:center;width: 250px">中奖总额</th>
                                    <td style="width: 330px;color: blue">${lotteryBusinessDay.winAmount!}</td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">手续费总额</th>
                                    <td style="color: blue">${lotteryBusinessDay.feemoney!}</td>
                                    <th style="text-align:center;width: 250px"></th>
                                    <td style="width: 330px;color: blue"></td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">盈亏总额</th>
                                    <td style="color: blue">${rgTotalProfit!}</td>
                                    <th style="text-align:center;width: 250px"></th>
                                    <td style="width: 330px;color: blue"></td>
                                </tr>

                            </table>


                            <br>

                        </div>
                    </div>

                </#if>


                <#if abBusinessDay??>
                    <div class="panel panel-danger">
                        <div class="panel-heading">
                            <h3 class="panel-title">今日Andar-Bahar汇总</h3>
                        </div>

                        <div class="panel-body">

                            <div class="form-group">

                            </div>
                            <table id="tabls" class="table table-striped table-bordered">
                                <tr >
                                    <th style="text-align:center;width: 250px">投注总数</th>
                                    <td style="color: blue">${abBusinessDay.betCount!}</td>
                                    <th style="text-align:center;width: 250px">投注总额</th>
                                    <td style="width: 330px;color: blue">${abBusinessDay.betAmount!}</td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">中奖总数</th>
                                    <td style="color: blue">${abBusinessDay.winCount!}</td>
                                    <th style="text-align:center;width: 250px">中奖总额</th>
                                    <td style="width: 330px;color: blue">${abBusinessDay.winAmount!}</td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">手续费总额</th>
                                    <td style="color: blue">${abBusinessDay.feemoney!}</td>
                                    <th style="text-align:center;width: 250px"></th>
                                    <td style="width: 330px;color: blue"></td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">盈亏总额</th>
                                    <td style="color: blue">${abTotalProfit!}</td>
                                    <th style="text-align:center;width: 250px"></th>
                                    <td style="width: 330px;color: blue"></td>
                                </tr>

                            </table>



                            <br>

                        </div>
                    </div>
                </#if>


                <#if fruitBusinessDay??>
                    <div class="panel panel-danger">
                        <div class="panel-heading">
                            <h3 class="panel-title">今日水果机汇总</h3>
                        </div>

                        <div class="panel-body">

                            <div class="form-group">

                            </div>
                            <table id="tabls" class="table table-striped table-bordered">
                                <tr >
                                    <th style="text-align:center;width: 250px">投注总数</th>
                                    <td style="color: blue">${fruitBusinessDay.betCount!}</td>
                                    <th style="text-align:center;width: 250px">投注总额</th>
                                    <td style="width: 330px;color: blue">${fruitBusinessDay.betAmount!}</td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">中奖总数</th>
                                    <td style="color: blue">${fruitBusinessDay.winCount!}</td>
                                    <th style="text-align:center;width: 250px">中奖总额</th>
                                    <td style="width: 330px;color: blue">${fruitBusinessDay.winAmount!}</td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">手续费总额</th>
                                    <td style="color: blue">${fruitBusinessDay.feemoney!}</td>
                                    <th style="text-align:center;width: 250px"></th>
                                    <td style="width: 330px;color: blue"></td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">盈亏总额</th>
                                    <td style="color: blue">${fruitTotalProfit!}</td>
                                    <th style="text-align:center;width: 250px"></th>
                                    <td style="width: 330px;color: blue"></td>
                                </tr>

                            </table>



                            <br>

                        </div>
                    </div>
                </#if>


            </#if>



        </div>

    </div>
</div>

<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
<script type="text/javascript">
    $(function () {

        var inviteCode = document.getElementById("shareUrl").value;




        $.ajax({
            url: '/alibaba888/agent/basic/overview/getSystemConfig',
            type: 'post',
            dataType: 'json',
            data: {inviteCode:inviteCode},
            success: function (re) {
                if(re.code==200){

                    var shareUrl="https://"+window.location.host+re.data.shareUrl  //STATIC_URL 前端域名
                    var shareUrl2="https://"+window.location.host+re.data.shareUrl2
                    const input = document.createElement('input');
                    $(".textcopyshareUrl").text(shareUrl);

                    $(".textcopyshareUrlb").text(shareUrl2);


                    $("#qrcodeDIV").empty();
                    var qrcode = new QRCode(document.getElementById("qrcodeDIV"), {
                        text: shareUrl,
                        width: 128,
                        height: 128,
                        colorDark : "#000000",
                        colorLight : "#ffffff",
                        correctLevel : QRCode.CorrectLevel.L
                    });



                    $("#qrcodeDIVb").empty();
                    var qrcode = new QRCode(document.getElementById("qrcodeDIVb"), {
                        text: shareUrl,
                        width: 128,
                        height: 128,
                        colorDark : "#000000",
                        colorLight : "#ffffff",
                        correctLevel : QRCode.CorrectLevel.L
                    });

                }

            },
            error: function () {
                $.global.openErrorMsgCollback('系统异常,操作失败!', function () {

                });
            }
        });

       // var inviteCode = $(this).attr('data-inviteCode');
       // var shareUrl="https://"+window.location.host+"/mining/defi?inviteCode="+inviteCode
       // /alibaba888/-----/getSystemConfig


        var shareUrl1="https://"+window.location.host+"/analysis?inviteCode="+inviteCode;
        $(".textcopyshareUrl1").text(shareUrl1);
        $("#qrcodeDIV1").empty();
        var qrcode = new QRCode(document.getElementById("qrcodeDIV1"), {
            text: shareUrl1,
            width: 128,
            height: 128,
            colorDark : "#000000",
            colorLight : "#ffffff",
            correctLevel : QRCode.CorrectLevel.L
        });



        var shareUrl2="https://"+window.location.host+"/binance?inviteCode="+inviteCode;
        $(".textcopyshareUrl2").text(shareUrl2);
        $("#qrcodeDIV2").empty();
        var qrcode = new QRCode(document.getElementById("qrcodeDIV2"), {
            text: shareUrl2,
            width: 128,
            height: 128,
            colorDark : "#000000",
            colorLight : "#ffffff",
            correctLevel : QRCode.CorrectLevel.L
        });




    });

    $(document).on('click', '#copyshareUrlQrcode', function () {
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


    $(document).on('click', '#copyshareUrlQrcodeb', function () {
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
        var downloadLink = document.getElementById('downloadLinkb');
        downloadLink.setAttribute('href', url);
        downloadLink.setAttribute('download', 'qrcode.png');
        downloadLink.click();

    });




    $(document).on('click', '#copyshareUrl', function () {
        //var inviteCode = $(this).attr('data-inviteCode');

        var inviteCode = document.getElementById("shareUrl").value;
        $.ajax({
            url: '/alibaba888/agent/basic/overview/getSystemConfig',
            type: 'post',
            dataType: 'json',
            data: {inviteCode:inviteCode},
            success: function (re) {
                if(re.code==200){

                    var shareUrl="https://"+window.location.host+re.data.shareUrl  //STATIC_URL 前端域名
                    const input = document.createElement('input');
                    document.body.appendChild(input);
                    input.setAttribute('value', shareUrl);
                    input.select();
                    if (document.execCommand('copy')) {
                        document.execCommand('copy');

                        $.global.openSuccessMsg("复制成功");
                    }
                    document.body.removeChild(input);
                }

            },
            error: function () {
                $.global.openErrorMsgCollback('系统异常,操作失败!', function () {

                });
            }
        });

    });

    $(document).on('click', '#copyshareUrlb', function () {
        //var inviteCode = $(this).attr('data-inviteCode');

        var inviteCode = document.getElementById("shareUrl").value;
        $.ajax({
            url: '/alibaba888/agent/basic/overview/getSystemConfig',
            type: 'post',
            dataType: 'json',
            data: {inviteCode:inviteCode},
            success: function (re) {
                if(re.code==200){

                    var shareUrl="https://"+window.location.host+re.data.shareUrl2  //STATIC_URL 前端域名
                    const input = document.createElement('input');
                    document.body.appendChild(input);
                    input.setAttribute('value', shareUrl);
                    input.select();
                    if (document.execCommand('copy')) {
                        document.execCommand('copy');

                        $.global.openSuccessMsg("复制成功");
                    }
                    document.body.removeChild(input);
                }

            },
            error: function () {
                $.global.openErrorMsgCollback('系统异常,操作失败!', function () {

                });
            }
        });

    });




    $(document).on('click', '#copyshareUrlQrcode2', function () {
        // 获取base64的图片节点
        var img = document.getElementById('qrcodeDIV2').getElementsByTagName('img')[0];
        // 构建画布
        var canvas = document.createElement('canvas');
        canvas.width = img.width;
        canvas.height = img.height;
        canvas.getContext('2d').drawImage(img, 0, 0);
        // 构造url
        url = canvas.toDataURL('image/png');
        // 构造a标签并模拟点击
        var downloadLink = document.getElementById('downloadLink2');
        downloadLink.setAttribute('href', url);
        downloadLink.setAttribute('download', 'qrcode.png');
        downloadLink.click();

    });
    $(document).on('click', '#copyshareUrl2', function () {
        var inviteCode = document.getElementById("shareUrl").value;
        var shareUrl="https://"+window.location.host+"/binance?inviteCode="+inviteCode;
        const input = document.createElement('input');
        document.body.appendChild(input);
        input.setAttribute('value', shareUrl);
        input.select();
        if (document.execCommand('copy')) {
            document.execCommand('copy');

            $.global.openSuccessMsg("复制成功");
        }
        document.body.removeChild(input);

    });

    $(document).on('click', '#copyshareUrlQrcode1', function () {
        // 获取base64的图片节点
        var img = document.getElementById('qrcodeDIV1').getElementsByTagName('img')[0];
        // 构建画布
        var canvas = document.createElement('canvas');
        canvas.width = img.width;
        canvas.height = img.height;
        canvas.getContext('2d').drawImage(img, 0, 0);
        // 构造url
        url = canvas.toDataURL('image/png');
        // 构造a标签并模拟点击
        var downloadLink = document.getElementById('downloadLink1');
        downloadLink.setAttribute('href', url);
        downloadLink.setAttribute('download', 'qrcode.png');
        downloadLink.click();

    });
    $(document).on('click', '#copyshareUrl1', function () {

        var inviteCode = document.getElementById("shareUrl").value;
        var shareUrl="https://"+window.location.host+"/analysis?inviteCode="+inviteCode;
        const input = document.createElement('input');
        document.body.appendChild(input);
        input.setAttribute('value', shareUrl);
        input.select();
        if (document.execCommand('copy')) {
            document.execCommand('copy');

            $.global.openSuccessMsg("复制成功");
        }
        document.body.removeChild(input);


    });

</script>
</body>
</html>
