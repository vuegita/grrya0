<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox">
        <div class="ibox-title">
            <h5></h5>

            <p style="color: blue"> 　</p>
        </div>

        <div class="ibox-content">

            <div class="panel panel-danger">
                <div class="panel-heading">
                    <h3 class="panel-title">会员基本信息</h3>
                </div>

                <div class="panel-body">

                    <#if userInfo ? exists>
                        <table id="tabls" class="table table-striped table-bordered">
                            <tr >
                                <th style="text-align:center;width: 250px">用户名</th>
                                <td style="color: blue">${userInfo.name!}</td>
                                <th style="text-align:center;width: 250px">手机号</th>
                                <td style="width: 330px;color: blue">${userInfo.phone!} | ${userInfo.email!}</td>
                            </tr>
                            <tr >
                                <th style="text-align:center;width: 250px">用户类型</th>
                                <td style="color: blue">${userInfo.getShowUserType()!}</td>
                                <th style="text-align:center;width: 250px">用户状态</th>
                                <td style="width: 330px;color: blue">${userInfo.status!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">用户等级</th>
                                <td style="color: blue">${userAttr.level!}</td>
                                <th style="text-align:center;width: 250px">推广码</th>
                                <td style="width: 330px;color: blue">${userInfo.inviteCode!} | 今日访问量 - ${userTodayInviteStatsInfo.totalCount}</td>
                            </tr>

                            <#if UserVIPInfo ? exists>
                            <tr >
                                <th style="text-align:center;width: 250px">VIP名称</th>
                                <td style="color: blue">${UserVIPInfo.vipName!}</td>
                                <th style="text-align:center;width: 250px">VIP等级</th>
                                <td style="width: 330px;color: blue">${UserVIPInfo.vipLevel!}</td>
                            </tr>
                            </#if>

                        </table>
                    </#if>
                    <br>

                </div>
            </div>

            <#if isCrypto>

            <div class="panel panel-danger">
                <div class="panel-heading">
                    <h3 class="panel-title">财务信息</h3>
                </div>

                <div class="panel-body">

                    <#if report ? exists>
                        <table id="tabls" class="table table-striped table-bordered">
                            <tr >
                                <th style="text-align:center;width: 250px">余额</th>
                                <td style="color: blue">${moneyInfo.balance!}</td>
                                <th style="text-align:center;width: 250px">打码量</th>
                                <td style="width: 330px;color: blue">${moneyInfo.codeAmount!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">充值总额</th>
                                <td style="color: blue">${report.recharge!}</td>
                                <th style="text-align:center;width: 250px">提现总额</th>
                                <td style="width: 330px;color: blue">${report.withdraw- report.refund!}</td>
                            </tr>

                            <tr >
<#--                                <th style="text-align:center;width: 250px">邀请好友赠送</th>-->
<#--                                <td style="color: blue">${userAttr.inviteFriendTotalAmount!}</td>-->
<#--                                -->
                                <th style="text-align:center;width: 250px">首充时间</th>
                                <td style="color: blue">
                                    <#if userAttr.firstRechargeTime?exists>
                                        ${userAttr.firstRechargeTime?string('yyyy-MM-dd hh:mm:ss')!} |
                                    </#if>
                                    ${userAttr.firstRechargeOrderno!}
                                </td>
                                <th style="text-align:center;width: 250px">首充金额</th>
                                <td style="color: blue">${userAttr.firstRechargeAmount!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">不可提金额</th>
                                <td style="color: blue">${moneyInfo.limitAmount!}</td>
                                <th style="text-align:center;width: 250px">不可提打码</th>
                                <td style="width: 330px;color: blue">${moneyInfo.limitCode}</td>
                            </tr>


                        </table>
                    </#if>
                    <br>

                </div>
            </div>



            <div class="panel panel-danger">
                <div class="panel-heading">
                    <h3 class="panel-title">投注信息</h3>
                </div>

                <div class="panel-body">

                    <#if report ? exists>
                        <table id="tabls" class="table table-striped table-bordered">
                            <tr >
                                <th style="text-align:center;width: 250px">投注总额</th>
                                <td style="color: blue">${report.businessDeduct!}</td>
                                <th style="text-align:center;width: 250px">中奖总额</th>
                                <td style="width: 330px;color: blue">${report.businessRecharge!}</td>
                            </tr>

                        </table>
                    </#if>
                    <br>

                </div>
            </div>

            <div class="panel panel-danger">
                <div class="panel-heading">
                    <h3 class="panel-title">推广信息</h3>
                </div>

                <div class="panel-body">

                    <#if returnWaterLog ? exists>
                        <table id="tabls" class="table table-striped table-bordered">
                            <tr >
                                <th style="text-align:center;width: 250px">一级推广人数</th>
                                <td style="color: blue">${returnWaterLog.level1Count!}</td>
                                <th style="text-align:center;width: 250px">一级推广佣金</th>
                                <td style="width: 330px;color: blue">${returnWaterLog.level1Amount!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">二级推广人数</th>
                                <td style="color: blue">${returnWaterLog.level2Count!}</td>
                                <th style="text-align:center;width: 250px">二级推广佣金</th>
                                <td style="width: 330px;color: blue">${returnWaterLog.level2Amount!}</td>
                            </tr>

                        </table>
                    </#if>
                    <br>

                </div>
            </div>

            </#if>


            <div class="panel panel-danger">
                <div class="panel-heading">
                    <h3 class="panel-title">今日状态信息</h3>
                </div>

                <div class="panel-body">

                    <table id="tabls" class="table table-striped table-bordered">
                        <tr >
                            <th style="text-align:center;width: 250px">Lv1邀请总人数</th>
                            <td style="color: blue">${userStatsDetailV2Info.totalLv1Count!}</td>
                            <th style="text-align:center;width: 250px">Lv2邀请总人数</th>
                            <td style="width: 330px;color: blue">${userStatsDetailV2Info.totalLv2Count!}</td>
                        </tr>

                        <tr >
                            <th style="text-align:center;width: 250px">Lv1有效邀请</th>
                            <td style="color: blue">${userStatsDetailV2Info.validLv1Count!}</td>
                            <th style="text-align:center;width: 250px">Lv2有效邀请</th>
                            <td style="width: 330px;color: blue">${userStatsDetailV2Info.validLv2Count!}</td>
                        </tr>

                        <tr >
                            <th style="text-align:center;width: 250px">Lv1游戏交易总额</th>
                            <td style="color: blue">${userStatsDetailV2Info.returnLv1Amount!}</td>
                            <th style="text-align:center;width: 250px">Lv2游戏交易总额</th>
                            <td style="width: 330px;color: blue">${userStatsDetailV2Info.returnLv2Amount!}</td>
                        </tr>

                        <tr >
                            <th style="text-align:center;width: 250px">Lv1流水总额</th>
                            <td style="color: blue">${userStatsDetailV2Info.tradeLv1Volumn!}</td>
                            <th style="text-align:center;width: 250px">Lv2流水总额</th>
                            <td style="width: 330px;color: blue">${userStatsDetailV2Info.tradeLv2Volumn!}</td>
                        </tr>

                        <tr >
                            <th style="text-align:center;width: 250px">大 | 小 | 单 | 双</th>
                            <td style="color: blue">

                                大 = ${userStatsDetailV2Info.tradeAmountBig!} |
                                小 = ${userStatsDetailV2Info.tradeAmountSmall!} |
                                单 = ${userStatsDetailV2Info.tradeAmountOdd!} |
                                双 = ${userStatsDetailV2Info.tradeAmountEven!}

                            </td>
                            <th style="text-align:center;width: 250px">数字</th>
                            <td style="width: 330px;color: blue">  ${userStatsDetailV2Info.tradeAmountNumber!}</td>
                        </tr>

                    </table>
                    <br>

                </div>
            </div>

            <div class="panel panel-danger">
                <div class="panel-heading">
                    <h3 class="panel-title">其它信息</h3>
                </div>

                <div class="panel-body">

                    <#if userAttr ? exists>
                        <table id="tabls" class="table table-striped table-bordered">
                            <tr >
                                <th style="text-align:center;width: 250px">直属代理</th>
                                <td style="color: blue">${userAttr.agentname!}</td>
                                <th style="text-align:center;width: 250px">直属员工上级</th>
                                <td style="color: blue">${userAttr.directStaffname!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">父级上级</th>
                                <td style="width: 330px;color: blue">${userAttr.parentname!}</td>
                                <th style="text-align:center;width: 250px">祖父级上级</th>
                                <td style="width: 330px;color: blue">${userAttr.grantfathername!}</td>
                            </tr>

                            <tr >
                                <th style="text-align:center;width: 250px">注册时间</th>
                                <td style="color: blue">${userInfo.createtime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                <th style="text-align:center;width: 250px">注册IP</th>
                                <td style="width: 330px;color: blue">${userInfo.registerip!}</td>
                            </tr>

                            <#if userInfo.lastlogintime??>
                            <tr >
                                <th style="text-align:center;width: 250px">最后登陆时间</th>
                                <td style="width: 330px;color: blue">${userInfo.lastlogintime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                <th style="text-align:center;width: 250px">最后登陆IP</th>
                                <td style="color: blue">${userInfo.lastloginip!}</td>
                            </tr>
                            </#if>

                        </table>
                    </#if>
                    <br>

                </div>
            </div>
        </div>


    </div>
</div>