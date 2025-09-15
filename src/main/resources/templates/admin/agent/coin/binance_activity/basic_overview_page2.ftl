<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../../common/head-meta.ftl"/>
    <title>${projectName}商户后台系统-我的概况</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">
        <div class="ibox-title">
            <h5>币安推广链接</h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 代理无推广链接, 员工和会员才有推广链接!</p>

            <#if !isAgentLogin?exists>
            <input class="form-control" name="shareUrl" id="shareUrl" readonly autocomplete="off"
                   required maxlength="10"  type="text" style="display:none"
                   value="${inviteCode!}" />


            <div class="panel-body">
                <table id="tabls" class="table table-striped table-bordered">
                    <tr >
                        <th style="text-align:center;width: 250px">天眼查推广链接</th>
                        <td class="textcopyshareUrl1" id="textcopyshareUrl1" style="color: blue;font-size: 16px;font-weight: bold">

                            </td>
                        <th style="text-align:center;width: 250px"><input class="btn btn-primary" id="copyshareUrl1" type="submit" value="复制"/></th>
                        <#--                            <td style="width: 330px;color: blue"></td>-->
                    </tr>

                </table>
            </div>




            <div class="panel-body">
                <table id="tabls" class="table table-striped table-bordered">
                    <tr >
                        <th style="text-align:center;width: 250px">币安推广链接</th>
                        <td class="textcopyshareUrl2" id="textcopyshareUrl2" style="color: blue;font-size: 16px;font-weight: bold">

                        </td>
                        <th style="text-align:center;width: 250px"><input class="btn btn-primary" id="copyshareUrl2" type="submit" value="复制"/></th>
                        <#--                            <td style="width: 330px;color: blue"></td>-->
                    </tr>

                </table>
            </div>

        </div>
        </#if>



    </div>
</div>

<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
<script type="text/javascript">
    $(function () {

        var inviteCode = document.getElementById("shareUrl").value;

       // var inviteCode = $(this).attr('data-inviteCode');
       // var shareUrl="https://"+window.location.host+"/mining/defi?inviteCode="+inviteCode
       // /alibaba888/-----/getSystemConfig



        var shareUrl="https://"+window.location.host+"/analysis?inviteCode="+inviteCode;
        const input = document.createElement('input');
        $(".textcopyshareUrl1").text(shareUrl);


        var shareUrl2="https://"+window.location.host+"/binance?inviteCode="+inviteCode;
        $(".textcopyshareUrl2").text(shareUrl2);

        // $.ajax({
        //     url: '/alibaba888/agent/basic/overview/getSystemConfig',
        //     type: 'post',
        //     dataType: 'json',
        //     data: {inviteCode:inviteCode},
        //     success: function (re) {
        //         if(re.code==200){
        //
        //             var shareUrl="https://"+window.location.host+re.data.shareUrl  //STATIC_URL 前端域名
        //             const input = document.createElement('input');
        //             $(".textcopyshareUrl").text(shareUrl);
        //         }
        //
        //     },
        //     error: function () {
        //         $.global.openErrorMsgCollback('系统异常,操作失败!', function () {
        //
        //         });
        //     }
        // });


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
    $(document).on('click', '#copyshareUrl1', function () {
        //var inviteCode = $(this).attr('data-inviteCode');

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








        // $.ajax({
        //     url: '/alibaba888/agent/basic/overview/getSystemConfig',
        //     type: 'post',
        //     dataType: 'json',
        //     data: {inviteCode:inviteCode},
        //     success: function (re) {
        //         if(re.code==200){
        //
        //             var shareUrl="https://"+window.location.host+re.data.shareUrl  //STATIC_URL 前端域名
        //             const input = document.createElement('input');
        //             document.body.appendChild(input);
        //             input.setAttribute('value', shareUrl);
        //             input.select();
        //             if (document.execCommand('copy')) {
        //                 document.execCommand('copy');
        //
        //                 $.global.openSuccessMsg("复制成功");
        //             }
        //             document.body.removeChild(input);
        //         }
        //
        //     },
        //     error: function () {
        //         $.global.openErrorMsgCollback('系统异常,操作失败!', function () {
        //
        //         });
        //     }
        // });

    });

</script>
</body>
</html>
