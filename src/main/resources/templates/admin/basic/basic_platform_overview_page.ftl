<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}商户后台系统-基本信息</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
</head>

<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">
        <div class="ibox-title">
            <h5>平台概况</h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">1.后台统计数据每10分钟自动更新一次!</p>
            <p  style="color: green">2. 实时数据可到 【会员每日统计】和【员工每日统计】查看!</p>
            <p  style="color: green">3. 点击刷新按钮重新统计，10秒内最多刷新一次!</p>
            <p  style="color: green">4. 净利差总额=投注 - 中奖  + 平台扣款 - 平台充值 - 平台赠送 - 返佣总额!</p>
            <p  style="color: green;font-size: 18px">5. 当前在线人数： <span style="color: red">${onlineUserCount !}</span></p>
            <p  style="color: green;font-size: 18px">6. 当前活跃人数： <span style="color: red">${activeUserCount !}</span></p>
        </div>


        <div class="ibox-content">
<#--            <div class="panel " style="float: left;font-size: 18px;">-->
<#--                当前在线人数： <span style="color: red">${onlineUserCount !}</span>-->
<#--            </div>-->
<#--            <div class="panel " style="float: left;font-size: 18px;margin-left: 50px">-->
<#--                当前活跃人数： <span style="color: red">${onlineUserCount !}</span>-->
<#--            </div>-->
            <div class="panel " style="float: right">
              最后刷新时间： ${user_count.refreshTime !}
                <button id="search-btn" type="button" class="btn btn-outline btn-default" title="刷新">
                    <i class="glyphicon glyphicon-refresh" aria-hidden="true"></i>刷新
                </button>
            </div>
            <div style="height: 60px"></div>
            <div class="panel panel-danger">
                <div class="panel-heading">
                    <h3 class="panel-title">平台汇总</h3>
                </div>

                <div class="panel-body">
                    <#if platform_stats ? exists>
                        <table id="tabls" class="table table-striped table-bordered">
                            <tr >
                                <th style="text-align:center;width: 250px">总充值</th>
                                <td style="color: blue">${platform_stats.recharge!}</td>
                                <th style="text-align:center;width: 250px">总提现</th>
                                <td style="width: 330px;color: blue">${platform_stats.withdraw!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">总提现手续费</th>
                                <td style="width: 330px;color: blue">${platform_stats.feemoney!}</td>
                                <th style="text-align:center;width: 250px">总退款</th>
                                <td style="width: 330px;color: blue">${platform_stats.refund!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">平台总充值</th>
                                <td style="width: 330px;color: blue">${platform_stats.platformRecharge!}</td>

                                <th style="text-align:center;width: 250px">平台总扣款</th>
                                <td style="width: 330px;color: blue">${platform_stats.platformDeduct!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">平台总赠送</th>
                                <td style="width: 330px;color: blue">${platform_stats.platformPresentation!}</td>

                                <th style="text-align:center;width: 250px">退款总额</th>
                                <td style="width: 330px;color: blue">${platform_stats.refund!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">总投注</th>
                                <td style="width: 330px;color: blue">${platform_stats.businessDeduct!}</td>

                                <th style="text-align:center;width: 250px">总中奖</th>
                                <td style="width: 330px;color: blue">${platform_stats.businessRecharge!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">总投注手续费</th>
                                <td style="width: 330px;color: blue">${platform_stats.businessFeemoney!}</td>

                                <th style="text-align:center;width: 250px">总返佣</th>
                                <td style="width: 330px;color: blue">${platform_stats.returnWater!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">总盈亏</th>
                                <td style="width: 330px;color: blue">
                                    ${ platform_stats.totalProfit!}
                                </td>

                                <th style="text-align:center;width: 250px"></th>
                                <td style="width: 330px;color: blue"></td>
                            </tr>

                        </table>
                    </#if>
                    <br>
                </div>
            </div>

            <div class="panel panel-danger">
                <div class="panel-heading">
                    <h3 class="panel-title">用户汇总</h3>
                </div>

                <div class="panel-body">

                    <#if user_count ? exists>
                        <table id="tabls" class="table table-striped table-bordered">
                            <tr >
                                <th style="text-align:center;width: 250px">会员总人数</th>
                                <td style="color: blue">${user_count.totalMemberRegCount!}</td>
                                <th style="text-align:center;width: 250px">今日注册人数</th>
                                <td style="width: 330px;color: blue">${user_count.todayMemberRegCount!}</td>
                            </tr>
                            <tr >
                                <th style="text-align:center;width: 250px">代理总数</th>
                                <td style="color: blue">${user_count.totalAgentCount!}</td>
                                <th style="text-align:center;width: 250px">员工总数</th>
                                <td style="color: blue">${user_count.totalStaffCount!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">今日充值人数 | 首充金额</th>
                                <td style="color: blue">${user_count.todayFirstRechargeCount!} | ${user_count.todayFirstRechargeAmount!}</td>
                                <th style="text-align:center;width: 250px">今日裂变人数</th>
                                <td style="color: blue">${user_count.todayMemberSplitCount!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">会员总余额</th>
                                <td style="color: blue">${user_count.totalBalance!}</td>
                                <th style="text-align:center;width: 250px">今日活跃人数</th>
                                <td style="color: blue">${todayActiveUserCount!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">历史活跃人数</th>
                                <td style="color: blue">${historyActiveUserCountOfBeforeDay!}</td>
                                <th style="text-align:center;width: 250px"></th>
                                <td style="color: blue"></td>
                            </tr>

                        </table>
                    </#if>
                    <br>

                </div>
            </div>

            <div class="panel panel-danger">
                <div class="panel-heading">
                    <h3 class="panel-title">今日金额汇总</h3>
                </div>

                <div class="panel-body">
                    <#if user_amount ? exists>
                        <table id="tabls" class="table table-striped table-bordered">
                            <tr >
                                <th style="text-align:center;width: 250px">充值总额</th>
                                <td style="color: blue">${user_amount.recharge!}</td>
                                <th style="text-align:center;width: 250px">提现总额</th>
                                <td style="width: 330px;color: blue">${user_amount.withdraw!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">手续费总额</th>
                                <td style="width: 330px;color: blue">${user_amount.feemoney!}</td>
                                <th style="text-align:center;width: 250px">退款总额</th>
                                <td style="width: 330px;color: blue">${user_amount.refund!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">平台充值总额</th>
                                <td style="width: 330px;color: blue">${user_amount.platformRecharge!}</td>

                                <th style="text-align:center;width: 250px">平台扣款总额</th>
                                <td style="width: 330px;color: blue">${user_amount.platformDeduct!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">平台赠送总额</th>
                                <td style="width: 330px;color: blue">${user_amount.platformPresentation!}</td>

                                <th style="text-align:center;width: 250px"></th>
                                <td style="width: 330px;color: blue"></td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">投注总额</th>
                                <td style="width: 330px;color: blue">${user_amount.businessDeduct!}</td>

                                <th style="text-align:center;width: 250px">中奖总额</th>
                                <td style="width: 330px;color: blue">${user_amount.businessRecharge!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">投注手续费</th>
                                <td style="width: 330px;color: blue">${user_amount.businessFeemoney!}</td>

                                <th style="text-align:center;width: 250px">返佣</th>
                                <td style="width: 330px;color: blue">${user_amount.returnWater!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">盈亏总额</th>
                                <td style="width: 330px;color: blue">
                                    ${ user_amount.totalProfit!}
                                </td>

                                <th style="text-align:center;width: 250px">净利差总额</th>
                                <td style="width: 330px;color: red;font-weight: bold">
                                    ${(user_amount.businessDeduct - user_amount.businessRecharge + user_amount.platformDeduct-user_amount.platformRecharge- user_amount.platformPresentation-user_amount.returnWater )!}
                                </td>
                            </tr>

                        </table>
                    </#if>
                    <br>
                </div>
            </div>

            <#if gameOverInfoList?? && gameOverInfoList?size gt 0>

                <#list gameOverInfoList as rootItem>

                    <div class="panel panel-danger">
                        <div class="panel-heading">
                            <h3 class="panel-title">今日 ${rootItem.title!} 汇总</h3>
                        </div>

                        <div class="panel-body">

                            <#list rootItem.dataList as item>
                                <div class="form-group">
                                    <label class="control-label" style="color: green;margin-left: 100px"> ${item.title!} </label><br>
                                </div>
                                <table id="tabls" class="table table-striped table-bordered">
                                    <tr >
                                        <th style="text-align:center;width: 250px">投注总数</th>
                                        <td style="color: blue">${item.totalBetCount!}</td>
                                        <th style="text-align:center;width: 250px">投注总额</th>
                                        <td style="width: 330px;color: blue">${item.totalBetAmount!}</td>
                                    </tr>

                                    <tr >
                                        <th style="text-align:center;width: 250px">中奖总数</th>
                                        <td style="color: blue">${item.totalWinCount!}</td>
                                        <th style="text-align:center;width: 250px">中奖总额</th>
                                        <td style="width: 330px;color: blue">${item.totalWinAmount!}</td>
                                    </tr>

                                    <tr >
                                        <th style="text-align:center;width: 250px">手续费总额</th>
                                        <td style="color: blue">${item.totalFeemoney!}</td>
                                        <th style="text-align:center;width: 250px"></th>
                                        <td style="width: 330px;color: blue"></td>
                                    </tr>

                                    <tr >
                                        <th style="text-align:center;width: 250px">盈亏总额</th>
                                        <td style="width: 330px;color: blue">
                                            ${item.platformProfit!}
                                        </td>

                                        <th style="text-align:center;width: 250px"></th>
                                        <td style="width: 330px;color: blue"></td>
                                    </tr>
                                </table>
                            <#--                            <div class="hr-line-dashed"></div>-->
                            </#list>
                            <br>

                        </div>
                    </div>

                </#list>

            </#if>

            <#if game_lottery_rg ? exists>
            <div class="panel panel-danger">
                <div class="panel-heading">
                    <h3 class="panel-title">今日红绿汇总</h3>
                </div>

                <div class="panel-body">
                    <#list game_lottery_rg as item>
                            <div class="form-group">
                                <label class="control-label" style="color: green;margin-left: 100px"> ${item.title} </label><br>
                            </div>
                            <table id="tabls" class="table table-striped table-bordered">
                                <tr >
                                    <th style="text-align:center;width: 250px">投注总数</th>
                                    <td style="color: blue">${item.totalBetCount!}</td>
                                    <th style="text-align:center;width: 250px">投注总额</th>
                                    <td style="width: 330px;color: blue">${item.totalBetAmount!}</td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">中奖总数</th>
                                    <td style="color: blue">${item.totalWinCount!}</td>
                                    <th style="text-align:center;width: 250px">中奖总额</th>
                                    <td style="width: 330px;color: blue">${item.totalWinAmount!}</td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">手续费总额</th>
                                    <td style="color: blue">${item.totalFeemoney!}</td>
                                    <th style="text-align:center;width: 250px"></th>
                                    <td style="width: 330px;color: blue"></td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">盈亏总额</th>
                                    <td style="width: 330px;color: blue">
                                        ${item.platformProfit!}
                                    </td>

                                    <th style="text-align:center;width: 250px"></th>
                                    <td style="width: 330px;color: blue"></td>
                                </tr>
                            </table>
                        <#--                            <div class="hr-line-dashed"></div>-->
                        </#list>
                    <br>
                </div>
            </div>
            </#if>

            <#if game_ab ? exists>
            <div class="panel panel-danger">
                <div class="panel-heading">
                    <h3 class="panel-title">今日Andar-Bahar汇总</h3>
                </div>
                <div class="panel-body">
                        <#list game_ab as item>
                            <div class="form-group">
                                <label class="control-label" style="color: green;margin-left: 100px"> ${item.title} </label><br>
                            </div>
                            <table id="tabls" class="table table-striped table-bordered">
                                <tr >
                                    <th style="text-align:center;width: 250px">投注总数</th>
                                    <td style="color: blue">${item.totalBetCount!}</td>
                                    <th style="text-align:center;width: 250px">投注总额</th>
                                    <td style="width: 330px;color: blue">${item.totalBetAmount!}</td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">中奖总数</th>
                                    <td style="color: blue">${item.totalWinCount!}</td>
                                    <th style="text-align:center;width: 250px">中奖总额</th>
                                    <td style="width: 330px;color: blue">${item.totalWinAmount!}</td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">手续费总额</th>
                                    <td style="color: blue">${item.totalFeemoney!}</td>
                                    <th style="text-align:center;width: 250px"></th>
                                    <td style="width: 330px;color: blue"></td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">盈亏总额</th>
                                    <td style="width: 330px;color: blue">
                                        ${item.platformProfit!}
                                    </td>

                                    <th style="text-align:center;width: 250px"></th>
                                    <td style="width: 330px;color: blue"></td>
                                </tr>
                            </table>
                        <#--                            <div class="hr-line-dashed"></div>-->
                        </#list>
                    <br>
                </div>
            </div>
            </#if>


            <#if game_fruit ? exists>
            <div class="panel panel-danger">
                <div class="panel-heading">
                    <h3 class="panel-title">今日水果机汇总</h3>
                </div>

                <div class="panel-body">
                        <#list game_fruit as item>
                            <div class="form-group">
                                <label class="control-label" style="color: green;margin-left: 100px"> ${item.title} </label><br>
                            </div>
                            <table id="tabls" class="table table-striped table-bordered">
                                <tr >
                                    <th style="text-align:center;width: 250px">投注总数</th>
                                    <td style="color: blue">${item.totalBetCount!}</td>
                                    <th style="text-align:center;width: 250px">投注总额</th>
                                    <td style="width: 330px;color: blue">${item.totalBetAmount!}</td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">中奖总数</th>
                                    <td style="color: blue">${item.totalWinCount!}</td>
                                    <th style="text-align:center;width: 250px">中奖总额</th>
                                    <td style="width: 330px;color: blue">${item.totalWinAmount!}</td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">手续费总额</th>
                                    <td style="color: blue">${item.totalFeemoney!}</td>
                                    <th style="text-align:center;width: 250px"></th>
                                    <td style="width: 330px;color: blue"></td>
                                </tr>

                                <tr >
                                    <th style="text-align:center;width: 250px">盈亏总额</th>
                                    <td style="width: 330px;color: blue">
                                        ${item.platformProfit!}
                                    </td>

                                    <th style="text-align:center;width: 250px"></th>
                                    <td style="width: 330px;color: blue"></td>
                                </tr>
                            </table>
                        <#--                            <div class="hr-line-dashed"></div>-->
                        </#list>
                    <br>
                </div>
            </div>
            </#if>

        </div>





        </div>

    </div>
</div>

<!-- loading -->
<div class="modal fade" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop='static'>
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">提示</h4>
            </div>
            <div class="modal-body">
                <span class="head-time-s1">5</span>秒后刷新页面<span id="result"></span>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/bootstrap-table/locale/bootstrap-table-zh-CN.min.js"></script>
<script type="text/javascript">

    $(function () {
        $('#search-btn').click(function () {

            $.ajax({
                url: '/alibaba888/Liv2sky3soLa93vEr62/refrshBasicPlatformConfig',
                type: 'post',
                data: {},
                dataType: 'json',
                success: function (result) {
                    if (result.code === 200) {
                        $('#loading').modal('show');
                        var t = 5;
                        setInterval(function () {
                            $(".head-time-s1").text(t);
                            t--;
                            if (t == 0) {
                                location.reload();
                            }
                        }, 1000)
                    }else{
                        $.global.openSuccessMsg(result.msg);
                    }
                   // $.global.openSuccessMsg("请10秒后刷新页面");
                   //  setTimeout(function(){
                   //      //alert(1131231111321)
                   //     // refresh();
                   //      $('#loading').modal('hide');
                   //      location.reload();
                   //      },10000);
                    // if (result.code === 200) {
                    //     $.global.openSuccessMsg("请10秒后刷新页面");
                    //     return;
                    // }

                },
                error: function () {
                    $.global.openErrorMsgCollback('系统异常,操作失败!', function () {
                    });
                }
            });

           // refresh();
        });

    });


</script>
</body>
</html>
