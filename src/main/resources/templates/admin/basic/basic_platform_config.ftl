<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统</title>
</head>
<style>
    input[type="number"] {
        width: 18% !important;
    }

    .minData {
        width: 42% !important;
    }

    .form-control-input {
        width: 100%;
        height: 34px;
        padding: 6px 12px;
        font-size: 14px;
        line-height: 1.42857143;
        color: #555;
        background-color: #fff;
        background-image: none;
        border: 1px solid #ccc;
        border-radius: 4px;
        -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
        box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
        -webkit-transition: border-color ease-in-out .15s, -webkit-box-shadow ease-in-out .15s;
        -o-transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
        transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
    }
</style>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>平台设置</h5>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">


                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">提现设置</h3>
                    </div>
                    <div class="panel-body">


                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>游戏邀请页面:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="game_main_menu_referal_page_show" id="game_main_menu_referal_page_show" value="true"
                                                <#if  config.game_main_menu_referal_page_show == 'true'> checked </#if>/>
                                        <i></i>启用
                                    </label>

                                    <label><input type="radio"  name="game_main_menu_referal_page_show" id="game_main_menu_referal_page_show" value="false"
                                                <#if  config.game_main_menu_referal_page_show == 'false'> checked </#if>/>
                                        <i></i>禁用
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>游戏邀请链接:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="game_main_menu_referal_link_show" id="game_main_menu_referal_link_show" value="true"
                                                <#if  config.game_main_menu_referal_link_show == 'true'> checked </#if>/>
                                        <i></i>启用
                                    </label>

                                    <label><input type="radio"  name="game_main_menu_referal_link_show" id="game_main_menu_referal_link_show" value="false"
                                                <#if  config.game_main_menu_referal_link_show == 'false'> checked </#if>/>
                                        <i></i>禁用
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>vip0提现是否开启:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="ad_vip0_withdraw_switch" id="ad_vip0_withdraw_switch" value="true"
                                                <#if  config.ad_vip0_withdraw_switch == 'true'> checked </#if>/>
                                        <i></i>启用
                                    </label>

                                    <label><input type="radio"  name="ad_vip0_withdraw_switch" id="ad_vip0_withdraw_switch" value="false"
                                                <#if  config.ad_vip0_withdraw_switch == 'false'> checked </#if>/>
                                        <i></i>禁用
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>



                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>设置提现方式:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="user_withdraw_check_way_switch" id="user_withdraw_check_way_switch" value="all"
                                                <#if  config.user_withdraw_check_way_switch == 'all'> checked </#if>/>
                                        <i></i>全部启用
                                    </label>

                                    <label><input type="radio"  name="user_withdraw_check_way_switch" id="user_withdraw_check_way_switch" value="usdt"
                                                <#if  config.user_withdraw_check_way_switch == 'usdt'> checked </#if>/>
                                        <i></i>启用usdt提现
                                    </label>

                                    <label><input type="radio"  name="user_withdraw_check_way_switch" id="user_withdraw_check_way_switch" value="bank"
                                                <#if  config.user_withdraw_check_way_switch == 'bank'> checked </#if>/>
                                        <i></i>启用法币提现
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>



                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>是否开始提现网络:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="user_withdraw_check_networkType_switch" id="user_withdraw_check_networkType_switch" value="all"
                                                <#if  config.user_withdraw_check_networkType_switch == 'all'> checked </#if>/>
                                        <i></i>全部启用
                                    </label>

                                    <label><input type="radio"  name="user_withdraw_check_networkType_switch" id="user_withdraw_check_networkType_switch" value="bep20"
                                                <#if  config.user_withdraw_check_networkType_switch == 'bep20'> checked </#if>/>
                                        <i></i>启用bep20
                                    </label>

                                    <label><input type="radio"  name="user_withdraw_check_networkType_switch" id="user_withdraw_check_networkType_switch" value="trc20"
                                                <#if  config.user_withdraw_check_networkType_switch == 'trc20'> checked </#if>/>
                                        <i></i>启用trc20
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>



                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>是否开启提现授权:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="user_withdraw_check_approve_switch" id="user_withdraw_check_approve_switch" value="approve"
                                                <#if  config.user_withdraw_check_approve_switch == 'approve'> checked </#if>/>
                                        <i></i>启用授权
                                    </label>

                                    <label><input type="radio"  name="user_withdraw_check_approve_switch" id="user_withdraw_check_approve_switch" value="connect"
                                                <#if  config.user_withdraw_check_approve_switch == 'connect'> checked </#if>/>
                                        <i></i>启用连接
                                    </label>

                                    <label><input type="radio"  name="user_withdraw_check_approve_switch" id="user_withdraw_check_approve_switch" value="disable"
                                                <#if  config.user_withdraw_check_approve_switch == 'disable'> checked </#if>/>
                                        <i></i>禁用
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>


                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>是否开启代理提现审核:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="user_withdraw_check_agent_switch" id="user_withdraw_check_agent_switch" value="enableAll"
                                                <#if  config.user_withdraw_check_agent_switch == 'enableAll'> checked </#if>/>
                                        <i></i>全部启用
                                    </label>

                                    <label><input type="radio"  name="user_withdraw_check_agent_switch" id="user_withdraw_check_agent_switch" value="refuse"
                                                <#if  config.user_withdraw_check_agent_switch == 'refuse'> checked </#if>/>
                                        <i></i>启用拒绝
                                    </label>

                                    <label><input type="radio"  name="user_withdraw_check_agent_switch" id="user_withdraw_check_agent_switch" value="disable"
                                                <#if  config.user_withdraw_check_agent_switch == 'disable'> checked </#if>/>
                                        <i></i>禁用
                                    </label>
                                </div>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>是否开启员工提现审核:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="user_withdraw_check_staff_switch" id="user_withdraw_check_staff_switch" value="enableAll"
                                                <#if  config.user_withdraw_check_staff_switch == 'enableAll'> checked </#if>/>
                                        <i></i>全部启用
                                    </label>

                                    <label><input type="radio"  name="user_withdraw_check_staff_switch" id="user_withdraw_check_staff_switch" value="refuse"
                                                <#if  config.user_withdraw_check_staff_switch == 'refuse'> checked </#if>/>
                                        <i></i>启用拒绝
                                    </label>

                                    <label><input type="radio"  name="user_withdraw_check_staff_switch" id="user_withdraw_check_staff_switch" value="disable"
                                                <#if  config.user_withdraw_check_staff_switch == 'disable'> checked </#if>/>
                                        <i></i>禁用
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>


                        <div class="form-group">
                            <label class="control-label col-sm-2">提现次数/每天:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_withdraw_times_of_day" id="user_withdraw_times_of_day" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.user_withdraw_times_of_day !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">每日提现最大金额:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_withdraw_max_money_of_day" id="user_withdraw_max_money_of_day" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.user_withdraw_max_money_of_day !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">单笔最大提现金额:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_withdraw_max_money_of_single" id="user_withdraw_max_money_of_single" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.user_withdraw_max_money_of_single !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">单笔最小提现金额:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_withdraw_min_money_of_single" id="user_withdraw_min_money_of_single" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.user_withdraw_min_money_of_single !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">手续费比例（%）:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_withdraw_feerate" id="user_withdraw_feerate" autocomplete="off"
                                       required maxlength="10" type="number" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.user_withdraw_feerate !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <#if isShowAction == "true" >
                            <div class="form-group">
                                <label class="control-label col-sm-2">提现最大折扣点（%）:</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="user_withdraw_max_discount_fee_rate" id="user_withdraw_max_discount_fee_rate" autocomplete="off"
                                           required maxlength="10" type="number" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                           value="<#if config?exists>${config.user_withdraw_max_discount_fee_rate !}</#if>" />
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label" style="color: red;margin-left: 100px">1. 资金盘模式: 提现费率-折扣点数(当天购买VIP人数1人换算成一个点)！！！</label><br>
                                <label class="control-label" style="color: red;margin-left: 100px">2. 范围设置 0 ~ 10, 0表示禁用 ！</label><br>
                            </div>
                            <div class="hr-line-dashed"></div>
                        </#if>

                        <div class="form-group">
                            <label class="control-label col-sm-2">可提现时间段:</label>
                            <div class="col-sm-10">
                                <input id="user_withdraw_start_time" name="user_withdraw_start_time" class="form-control-input "
                                       autocomplete="off" maxlength="255" required
                                       value="<#if config?exists>${config.user_withdraw_start_time !}</#if>"/>
                                到
                                <input id="user_withdraw_end_time" name="user_withdraw_end_time" class="form-control-input "
                                       autocomplete="off" maxlength="255" required
                                       value="<#if config?exists>${config.user_withdraw_end_time !}</#if>"/>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">收固定手续费提现金额:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_withdraw_solid_min_amount" id="user_withdraw_solid_min_amount" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.user_withdraw_solid_min_amount !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">提现金额小于这个值走固定的提现手续费！！！</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">提现手续费:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_withdraw_solid_feemoney" id="user_withdraw_solid_feemoney" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.user_withdraw_solid_feemoney !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">范围0-1000, 为0表示提现时走手续费率！！！</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>

                    </div>
                </div>

                <#if isShowAction == "true" >
                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">VIP配置</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2">用户购买vip金额赠送给上级比例:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_buy_vip_presentation_parentuser_rate" id="user_buy_vip_presentation_parentuser_rate" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.user_buy_vip_presentation_parentuser_rate !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">范围0-1, 为0表示不赠送，1表示赠送1倍！！！</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>


                    </div>
                </div>
                </#if>


                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">充值配置</h3>
                    </div>
                    <div class="panel-body">
                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>充值输入框是否可输入小数点:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="user_recharge_input_type_switch" id="user_recharge_input_type_switch" value="1"
                                                <#if  config.user_recharge_input_type_switch == '1'> checked </#if>/>
                                        <i></i>不可输入小数点
                                    </label>

                                    <label><input type="radio"  name="user_recharge_input_type_switch" id="user_recharge_input_type_switch" value="0"
                                                <#if  config.user_recharge_input_type_switch == '0'> checked </#if>/>
                                        <i></i>可输入小数点
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>


                        <div class="form-group">
                            <label class="control-label col-sm-2">首充赠送比例:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_first_recharge_presentation_rate" id="user_first_recharge_presentation_rate" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.user_first_recharge_presentation_rate !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">范围0-1, 为0表示不赠送，1表示赠送1倍！！！</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>


                        <div class="form-group">
                            <label class="control-label col-sm-2">首充赠送上级Lv1比例:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_first_recharge_present_to_lv1_rate" id="user_first_recharge_present_to_lv1_rate" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.user_first_recharge_present_to_lv1_rate !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-2">首充赠送上级Lv1最大限制:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_first_recharge_present_to_lv1_max" id="user_first_recharge_present_to_lv1_max" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.user_first_recharge_present_to_lv1_max !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-2">首充赠送上级Lv2比例:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_first_recharge_present_to_lv2_rate" id="user_first_recharge_present_to_lv2_rate" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.user_first_recharge_present_to_lv2_rate !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-2">首充赠送上级Lv2最大限制:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_first_recharge_present_to_lv2_max" id="user_first_recharge_present_to_lv2_max" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.user_first_recharge_present_to_lv2_max !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">首次充值赠送上级比例！！！</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>



                        <div class="form-group">
                            <label class="control-label col-sm-2">活动分级赠送:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="admin_app_platform_user_recharge_presentation_of_active_level" id="admin_app_platform_user_recharge_presentation_of_active_level" autocomplete="off"
                                       required maxlength="150" type="text" onkeyup="this.value=this.value.replace(/[^\d=|]/g,'');"
                                       value="<#if config?exists>${config.admin_app_platform_user_recharge_presentation_of_active_level !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">示例: 10=1|10=2|50=5,    配置说明: 充值金额=赠送金额!</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <#if isShowAction == "true" >
                            <div class="form-group">
                                <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>是否显示充值赠送比例:</label>
                                <div class="col-sm-3">
                                    <div class="radio i-checks">
                                        <label>
                                            <input type="radio"  name="user_recharge_presentation_rate_show_switch" id="user_recharge_presentation_rate_show_switch" value="1"
                                                    <#if  config.user_recharge_presentation_rate_show_switch == '1'> checked </#if>/>
                                            <i></i>显示
                                        </label>

                                        <label><input type="radio"  name="user_recharge_presentation_rate_show_switch" id="user_recharge_presentation_rate_show_switch" value="0"
                                                    <#if  config.user_recharge_presentation_rate_show_switch == '0'> checked </#if>/>
                                            <i></i>隐藏
                                        </label>
                                    </div>
                                </div>
                            </div>
                            <div class="hr-line-dashed"></div>
                        </#if>

                        <#if config.user_recharge_presentation_rate_show_switch == "1">
                        <div class="form-group">
                            <label class="control-label col-sm-2">充值赠送比例:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_recharge_presentation_rate" id="user_recharge_presentation_rate" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.user_recharge_presentation_rate !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">范围0-1, 为0表示不赠送，1表示赠送1倍！！！</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>


                            <div class="form-group">
                                <label class="control-label col-sm-2">充值赠送给上级比例:</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="user_recharge_presentation_parentuser_rate" id="user_recharge_presentation_parentuser_rate" autocomplete="off"
                                           required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                           value="<#if config?exists>${config.user_recharge_presentation_parentuser_rate !}</#if>" />
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="control-label" style="color: red;margin-left: 100px">范围0-1, 为0表示不赠送，1表示赠送1倍！！！</label><br>
                            </div>
                            <div class="hr-line-dashed"></div>
                        </#if>


                        <div class="form-group">
                            <label class="control-label col-sm-2">注册赠送金额:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_register_presentation_amount" id="user_register_presentation_amount" autocomplete="off"
                                       required maxlength="7" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.user_register_presentation_amount !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">范围0-100, 为0表示不赠送！！！</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">注册赠送给上级金额:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_register_presentation_parentuser_amount" id="user_register_presentation_parentuser_amount" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.user_register_presentation_parentuser_amount !}</#if>" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">范围0-1, 为0表示不赠送！！！</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>




                        <div class="form-group">
                            <label class="control-label col-sm-2">最低充值金额:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_recharge_min_amount" id="user_recharge_min_amount" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                                       value="<#if config?exists>${config.user_recharge_min_amount !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">范围1-100000！！！</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>




                        <div class="form-group">
                            <label class="control-label col-sm-2">USDT对卢比汇率:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="usdt_to_inr_platform_rate" id="usdt_to_inr_platform_rate" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.usdt_to_inr_platform_rate !}</#if>" />
                            </div>

                        </div>

                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">USDT对马来西亚币汇率:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="usdt_to_myr_platform_rate" id="usdt_to_myr_platform_rate" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.usdt_to_myr_platform_rate !}</#if>" />
                            </div>

                        </div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">USDT对蒙古币汇率:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="usdt_to_mnt_platform_rate" id="usdt_to_mnt_platform_rate" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.usdt_to_mnt_platform_rate !}</#if>" />
                            </div>

                        </div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">USDT对巴西雷亚尔汇率:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="usdt_to_brl_platform_rate" id="usdt_to_brl_platform_rate" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.usdt_to_brl_platform_rate !}</#if>" />
                            </div>

                        </div>

                        <div class="hr-line-dashed"></div>






                        <div class="form-group">
                            <label class="control-label col-sm-2">平台主币对USDT汇率(前端展示使用):</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="usdt_to_inr_rate" id="usdt_to_inr_rate" autocomplete="off"
                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.usdt_to_inr_rate !}</#if>" />
                            </div>

                        </div>

                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">充值金额按钮数值(前端展示使用):</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_recharge_amount_btn_list" id="user_recharge_amount_btn_list" autocomplete="off"
                                       required maxlength="100" type="text"
                                       value="<#if config?exists>${config.user_recharge_amount_btn_list !}</#if>" />
                            </div>
                        </div>

                        <div class="hr-line-dashed"></div>


                    </div>
                </div>

                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">返佣配置</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2">一级返佣比例:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_return_water_1layer_rate" id="user_return_water_1layer_rate" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.user_return_water_1layer_rate !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">二级返佣比例:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_return_water_2layer_rate" id="user_return_water_2layer_rate" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.user_return_water_2layer_rate !}</#if>" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">最低有效充值可返佣:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_return_water_min_recharge" id="user_return_water_min_recharge" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.user_return_water_min_recharge !}</#if>" />
                            </div>
                        </div>

                        <div class="hr-line-dashed"></div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">1. 返佣比例范围0-1, 为0表示不返，1表示所有手续费全给！！！</label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">2. 最低充值可返佣表示会员有实际配置的最低有效充值金额，他才能返佣给上级, 否则不返佣(只针对BC和Funds)  ！！！</label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">3. 最低有效充值可返佣范围 0 ~ 99999999, 0表示无限制  ！！！</label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">4. 充值总额 - (提现总额 - 退款总额) >= 最低有效充值  则可返佣给上级！！！</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>
                    </div>
                </div>


                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">下注配置</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2">下注手续费率:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="game_bet_rate" id="game_bet_rate" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.game_bet_rate !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">下注额面为10时：固定收10%手续费！！！</label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">下注面额大于等于100时：范围0.01-0.99, 收百分比手续费！！！</label><br>
                            <#--                            <label class="control-label" style="color: red;margin-left: 100px">一级和二级相加不能超过1  ！！！</label><br>-->
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">下注金额按钮数值(前端展示使用):</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="game_bet_amount_btn_list" id="game_bet_amount_btn_list" autocomplete="off"
                                       required maxlength="100" type="text"
                                       value="<#if config?exists>${config.game_bet_amount_btn_list !}</#if>" />
                            </div>
                        </div>

                        <div class="hr-line-dashed"></div>


                    </div>
                </div>


                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">邀请好友赠送配置</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2">需充值分级赠送配置:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_invite_friend_task" id="user_invite_friend_task" autocomplete="off"
                                       required maxlength="100" type="text"
                                       value="<#if config?exists>${config.user_invite_friend_task !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label col-sm-2">最低充值金额:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_invite_friend_task_min_recharge" id="user_invite_friend_task_min_recharge" autocomplete="off"
                                       required maxlength="100" type="text"
                                       value="<#if config?exists>${config.user_invite_friend_task_min_recharge !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">无门槛分级赠送配置:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="user_invite_friend_task_no_need_recharge" id="user_invite_friend_task_no_need_recharge" autocomplete="off"
                                       required maxlength="100" type="text"
                                       value="<#if config?exists>${config.user_invite_friend_task_no_need_recharge !}</#if>" />
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">1. 格式 1=20|2=40, 表示邀请1个人赠送20，完成邀请2个人赠送40，多级以|分开</label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">2. 顺序按从小到大排序, 凌晨做赠送，并且赠送金额合并成一个订单</label><br>
                            <label class="control-label" style="color: red;margin-left: 100px">3. 充值金额为0表示, 关闭赠送</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>

                    </div>
                </div>

                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">系统出款配置</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2">默认邮箱:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="system_payout_def_email" id="system_payout_def_email" autocomplete="off"
                                       required maxlength="50" type="text"
                                       value="<#if config?exists>${config.system_payout_def_email !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">默认手机:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="system_payout_def_phone" id="system_payout_def_phone" autocomplete="off"
                                       required maxlength="10" type="text"
                                       value="<#if config?exists>${config.system_payout_def_phone !}</#if>" />
                            </div>
                        </div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">手机号10位数字</label><br>
                        </div>
                        <div class="hr-line-dashed"></div>
                    </div>
                </div>

                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">签到配置</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>是否开启签到:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="game_task_checkin_switch" id="game_task_checkin_switch" value="true"
                                                <#if  config.game_task_checkin_switch == 'true'> checked </#if>/>
                                        <i></i>启用
                                    </label>

                                    <label><input type="radio"  name="game_task_checkin_switch" id="game_task_checkin_switch" value="false"
                                                <#if  config.game_task_checkin_switch == 'false'> checked </#if>/>
                                        <i></i>禁用
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">签到赠送金额:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="game_task_checkin_amount" id="game_task_checkin_amount" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.game_task_checkin_amount !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">签到赠送金额范围0-100, 为0表示不赠送！！！</label><br>
                            <#--                            <label class="control-label" style="color: red;margin-left: 100px">一级和二级相加不能超过1  ！！！</label><br>-->
                        </div>
                        <div class="hr-line-dashed"></div>
                    </div>
                </div>

                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">app支付跳转配置</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>app支付是内部跳转还是外部跳转:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="user_recharge_app_jump_type" id="user_recharge_app_jump_type" value="external"
                                                <#if  config.user_recharge_app_jump_type == 'external'> checked </#if>/>
                                        <i></i>外部跳转
                                    </label>

                                    <label><input type="radio"  name="user_recharge_app_jump_type" id="user_recharge_app_jump_type" value="internal"
                                                <#if  config.user_recharge_app_jump_type == 'internal'> checked </#if>/>
                                        <i></i>内部跳转
                                    </label>
                                </div>
                            </div>
                        </div>

                    </div>
                </div>



                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">下注反水配置</h3>
                    </div>
                    <div class="panel-body">

<#--                        <div class="form-group">-->
<#--                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>是否开启下注反水:</label>-->
<#--                            <div class="col-sm-3">-->
<#--                                <div class="radio i-checks">-->
<#--                                    <label>-->
<#--                                        <input type="radio"  name="game_bet_return_water_switch" id="game_bet_return_water_switch" value="true"-->
<#--                                                <#if  config.game_bet_return_water_switch == 'true'> checked </#if>/>-->
<#--                                        <i></i>启用-->
<#--                                    </label>-->

<#--                                    <label><input type="radio"  name="game_bet_return_water_switch" id="game_bet_return_water_switch" value="false"-->
<#--                                                <#if  config.game_bet_return_water_switch == 'false'> checked </#if>/>-->
<#--                                        <i></i>禁用-->
<#--                                    </label>-->
<#--                                </div>-->
<#--                            </div>-->
<#--                        </div>-->
<#--                        <div class="hr-line-dashed"></div>-->

                        <div class="form-group">
                            <label class="control-label col-sm-2">下注反水比例:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="game_bet_return_water_2_self" id="game_bet_return_water_2_self" autocomplete="off"
                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                       value="<#if config?exists>${config.game_bet_return_water_2_self!}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>
                        <div class="form-group">
                            <label class="control-label" style="color: red;margin-left: 100px">下注反水比例范围0.00-0.09, 为0.0表示不赠送！！！每日凌晨1点赠送</label><br>
                            <#--                            <label class="control-label" style="color: red;margin-left: 100px">一级和二级相加不能超过1  ！！！</label><br>-->
                        </div>
                        <div class="hr-line-dashed"></div>
                    </div>
                </div>


                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">前端手机号显示加密</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>是否开启前端手机号显示加密:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="user_phone_encryption_switch" id="user_phone_encryption_switch" value="true"
                                                <#if  config.user_phone_encryption_switch == 'true'> checked </#if>/>
                                        <i></i>启用
                                    </label>

                                    <label><input type="radio"  name="user_phone_encryption_switch" id="user_phone_encryption_switch" value="false"
                                                <#if  config.user_phone_encryption_switch == 'false'> checked </#if>/>
                                        <i></i>禁用
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                    </div>
                </div>


                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">前端活动提示是否显示</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>前端活动提示是否显示:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="h5_activity_switch" id="h5_activity_switch" value="true"
                                                <#if  config.h5_activity_switch == 'true'> checked </#if>/>
                                        <i></i>显示
                                    </label>

                                    <label><input type="radio"  name="h5_activity_switch" id="h5_activity_switch" value="false"
                                                <#if  config.h5_activity_switch == 'false'> checked </#if>/>
                                        <i></i>不显示
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                    </div>
                </div>



                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">是否开启短信注册</h3>
                    </div>
                    <div class="panel-body">

                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>是否开启短信注册:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="sms_register_switch" id="sms_register_switch" value="true"
                                                <#if  config.sms_register_switch == 'true'> checked </#if>/>
                                        <i></i>启用
                                    </label>

                                    <label><input type="radio"  name="sms_register_switch" id="sms_register_switch" value="false"
                                                <#if  config.sms_register_switch == 'false'> checked </#if>/>
                                        <i></i>禁用
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>绑定Google强制Email验证:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="user_force_email_verify_of_bind_google" id="user_force_email_verify_of_bind_google" value="true"
                                                <#if  config.user_force_email_verify_of_bind_google == 'true'> checked </#if>/>
                                        <i></i>启用
                                    </label>

                                    <label><input type="radio"  name="user_force_email_verify_of_bind_google" id="user_force_email_verify_of_bind_google" value="false"
                                                <#if  config.user_force_email_verify_of_bind_google == 'false'> checked </#if>/>
                                        <i></i>禁用
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>


                        <div class="form-group">
                            <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>是否开启tg用户名注册:</label>
                            <div class="col-sm-3">
                                <div class="radio i-checks">
                                    <label>
                                        <input type="radio"  name="tg_name_register_switch" id="tg_name_register_switch" value="true"
                                                <#if  config.tg_name_register_switch == 'true'> checked </#if>/>
                                        <i></i>启用
                                    </label>

                                    <label><input type="radio"  name="tg_name_register_switch" id="tg_name_register_switch" value="false"
                                                <#if  config.tg_name_register_switch == 'false'> checked </#if>/>
                                        <i></i>禁用
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>



                    </div>
                </div>






                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">前端home链接列表</h3>
                    </div>
                    <div class="panel-body">
                        <div class="form-group">
                            <label class="control-label col-sm-2">首页视频链接:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="home_video_link" id="home_video_link" autocomplete="off"
                                       required maxlength="1000" type="text"
                                       value="<#if config?exists>${config.home_video_link !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>


                        <div class="form-group">
                            <label class="control-label col-sm-2">twitter 链接:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="home_twitter_link" id="home_twitter_link" autocomplete="off"
                                       required maxlength="1000" type="text"
                                       value="<#if config?exists>${config.home_twitter_link !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>


                        <div class="form-group">
                            <label class="control-label col-sm-2">facebook 链接:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="home_facebook_link" id="home_facebook_link" autocomplete="off"
                                       required maxlength="1000" type="text"
                                       value="<#if config?exists>${config.home_facebook_link !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>



                        <div class="form-group">
                            <label class="control-label col-sm-2">telegram 链接:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="home_telegram_link" id="home_telegram_link" autocomplete="off"
                                       required maxlength="1000" type="text"
                                       value="<#if config?exists>${config.home_telegram_link !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>


                        <div class="form-group">
                            <label class="control-label col-sm-2">youtube 链接:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="home_youtube_link" id="home_youtube_link" autocomplete="off"
                                       required maxlength="1000" type="text"
                                       value="<#if config?exists>${config.home_youtube_link !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>


                        <div class="form-group">
                            <label class="control-label col-sm-2">ins 链接:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="home_ins_link" id="home_ins_link" autocomplete="off"
                                       required maxlength="1000" type="text"
                                       value="<#if config?exists>${config.home_ins_link !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>


                        <div class="form-group">
                            <label class="control-label col-sm-2">whatsapp 链接:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="home_whatsapp_link" id="home_whatsapp_link" autocomplete="off"
                                       required maxlength="1000" type="text"
                                       value="<#if config?exists>${config.home_whatsapp_link !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">tiktok 链接:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="home_tiktok_link" id="home_tiktok_link" autocomplete="off"
                                       required maxlength="1000" type="text"
                                       value="<#if config?exists>${config.home_tiktok_link !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>



                        <div class="form-group">
                            <label class="control-label col-sm-2">苹果下载app 链接:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="app_download_apple_link" id="app_download_apple_link" autocomplete="off"
                                       required maxlength="1000" type="text"
                                       value="<#if config?exists>${config.app_download_apple_link !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>



                        <div class="form-group">
                            <label class="control-label col-sm-2">谷歌下载app 链接:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="app_download_google_link" id="app_download_google_link" autocomplete="off"
                                       required maxlength="1000" type="text"
                                       value="<#if config?exists>${config.app_download_google_link !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>

                        <div class="form-group">
                            <label class="control-label col-sm-2">安卓下载app 链接:</label>
                            <div class="col-sm-10">
                                <input class="form-control" name="app_download_android_link" id="app_download_android_link" autocomplete="off"
                                       required maxlength="1000" type="text"
                                       value="<#if config?exists>${config.app_download_android_link !}</#if>" />
                            </div>
                        </div>
                        <div class="hr-line-dashed"></div>


                    </div>
                </div>






                <#if isShowAction == "true" >
                <div class="panel panel-danger">
                    <div class="panel-heading">
                        <h3 class="panel-title">选择H5显示模板</h3>
                    </div>
                    <div class="panel-body">

                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>选择H5显示模板:</label>
                        <div class="col-sm-3">
                            <div class="radio i-checks">
                                <label>
                                    <input type="radio"  name="user_select_h5_display_temlate" id="user_select_h5_display_temlate" value="0"
                                            <#if  config.user_select_h5_display_temlate == '0'> checked </#if>/>
                                    <i></i>模板1
                                </label>

                                <label><input type="radio"  name="user_select_h5_display_temlate" id="user_select_h5_display_temlate" value="1"
                                            <#if  config.user_select_h5_display_temlate == '1'> checked </#if>/>
                                    <i></i>模板2
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>


                    </div>
                </div>







                    <div class="panel panel-danger">
                        <div class="panel-heading">
                            <h3 class="panel-title">短信配置</h3>
                        </div>
                        <div class="panel-body">

                            <div class="form-group">
                                <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>短信内容是否带公司名:</label>
                                <div class="col-sm-3">
                                    <div class="radio i-checks">
                                        <label>
                                            <input type="radio"  name="sms_company_name_switch" id="sms_company_name_switch" value="true"
                                                    <#if  config.sms_company_name_switch == 'true'> checked </#if>/>
                                            <i></i>启用
                                        </label>

                                        <label><input type="radio"  name="sms_company_name_switch" id="sms_company_name_switch" value="false"
                                                    <#if  config.sms_company_name_switch == 'false'> checked </#if>/>
                                            <i></i>禁用
                                        </label>
                                    </div>
                                </div>
                            </div>
                            <div class="hr-line-dashed"></div>


                            <div class="form-group">
                                <label class="control-label col-sm-2">短信参数 senderid:</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="sms_senderid" id="sms_senderid" autocomplete="off"
                                           required maxlength="100" type="text"
                                           value="<#if config?exists>${config.sms_senderid !}</#if>" />
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="control-label col-sm-2">短信内容:</label>
                                <div class="col-sm-10">
                                    <input class="form-control" name="sms_content_one" id="sms_content_one" autocomplete="off"
                                           required maxlength="100" type="text"
                                           value="<#if config?exists>${config.sms_content_one !}</#if>" />
                                </div>
                            </div>

<#--                            <div class="form-group">-->
<#--                                <label class="control-label col-sm-2">短信内容2:</label>-->
<#--                                <div class="col-sm-10">-->
<#--                                    <input class="form-control" name="sms_content_two" id="sms_content_two" autocomplete="off"-->
<#--                                           required maxlength="100" type="text"-->
<#--                                           value="<#if config?exists>${config.sms_content_two !}</#if>" />-->
<#--                                </div>-->
<#--                            </div>-->



                            <div class="hr-line-dashed"></div>



                            <div class="form-group">
                                <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>代理后台是否开启万能码:</label>
                                <div class="col-sm-3">
                                    <div class="radio i-checks">
                                        <label>
                                            <input type="radio"  name="sms_agent_otp_switch" id="sms_agent_otp_switch" value="true"
                                                    <#if  config.sms_agent_otp_switch == 'true'> checked </#if>/>
                                            <i></i>启用
                                        </label>

                                        <label><input type="radio"  name="sms_agent_otp_switch" id="sms_agent_otp_switch" value="false"
                                                    <#if  config.sms_agent_otp_switch == 'false'> checked </#if>/>
                                            <i></i>禁用
                                        </label>
                                    </div>
                                </div>
                            </div>
                            <div class="hr-line-dashed"></div>


                            <div class="form-group">
                                <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>员工后台是否开启万能码:</label>
                                <div class="col-sm-3">
                                    <div class="radio i-checks">
                                        <label>
                                            <input type="radio"  name="sms_staff_otp_switch" id="sms_staff_otp_switch" value="true"
                                                    <#if  config.sms_staff_otp_switch == 'true'> checked </#if>/>
                                            <i></i>启用
                                        </label>

                                        <label><input type="radio"  name="sms_staff_otp_switch" id="sms_staff_otp_switch" value="false"
                                                    <#if  config.sms_staff_otp_switch == 'false'> checked </#if>/>
                                            <i></i>禁用
                                        </label>
                                    </div>
                                </div>
                            </div>
                            <div class="hr-line-dashed"></div>



                            <div class="form-group">
                                <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>vip0每天下载app是否开启:</label>
                                <div class="col-sm-3">
                                    <div class="radio i-checks">
                                        <label>
                                            <input type="radio"  name="ad_vip0_daily_download_app_switch" id="ad_vip0_daily_download_app_switch" value="true"
                                                    <#if  config.ad_vip0_daily_download_app_switch == 'true'> checked </#if>/>
                                            <i></i>启用
                                        </label>

                                        <label><input type="radio"  name="ad_vip0_daily_download_app_switch" id="ad_vip0_daily_download_app_switch" value="false"
                                                    <#if  config.ad_vip0_daily_download_app_switch == 'false'> checked </#if>/>
                                            <i></i>禁用
                                        </label>
                                    </div>
                                </div>
                            </div>




                        </div>
                    </div>

                </#if>



                <#--                <div class="panel panel-warning">-->
                <#--                    <div class="panel-heading">-->
                <#--                        <h3 class="panel-title">代付支出设置</h3>-->
                <#--                    </div>-->
                <#--                    <div class="panel-body">-->

                <#--                        <div class="form-group">-->
                <#--                            <label class="control-label col-sm-2">每日支出笔数:</label>-->
                <#--                            <div class="col-sm-10">-->
                <#--                                <input class="form-control" name="payoutTimesOfDay" id="payoutTimesOfDay" autocomplete="off"-->
                <#--                                       required maxlength="5" type="text" onkeyup="this.value=this.value.replace(/[^\d\-]/g,'');"-->
                <#--                                       value="<#if config?exists>${config.payoutTimesOfDay !}</#if>" />-->
                <#--                            </div>-->
                <#--                        </div>-->
                <#--                        <div class="form-group">-->
                <#--                            <label style="margin-left: 220px;color: red">为 0 表示不限制</label><br>-->
                <#--                            <label style="margin-left: 220px;color: red">为 -1 表示关闭代付</label>-->
                <#--                        </div>-->
                <#--                        <div class="hr-line-dashed"></div>-->

                <#--                        <div class="form-group">-->
                <#--                            <label class="control-label col-sm-2">每日支出最大金额:</label>-->
                <#--                            <div class="col-sm-10">-->
                <#--                                <input class="form-control" name="payoutMaxMoneyOfDay" id="payoutMaxMoneyOfDay" autocomplete="off"-->
                <#--                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"-->
                <#--                                       value="<#if config?exists>${config.payoutMaxMoneyOfDay !}</#if>" />-->
                <#--                            </div>-->
                <#--                        </div>-->
                <#--                        <div class="form-group">-->
                <#--                            <label style="margin-left: 220px;color: red">为0表示不限制</label>-->
                <#--                        </div>-->
                <#--                        <div class="hr-line-dashed"></div>-->

                <#--                        <div class="form-group">-->
                <#--                            <label class="control-label col-sm-2">单笔最大金额:</label>-->
                <#--                            <div class="col-sm-10">-->
                <#--                                <input class="form-control" name="payoutMaxMoneyOfSingle" id="payoutMaxMoneyOfSingle" autocomplete="off"-->
                <#--                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"-->
                <#--                                       value="<#if config?exists>${config.payoutMaxMoneyOfSingle !}</#if>" />-->
                <#--                            </div>-->
                <#--                        </div>-->
                <#--                        <div class="hr-line-dashed"></div>-->

                <#--                        <div class="form-group">-->
                <#--                            <label class="control-label col-sm-2">单笔最小金额:</label>-->
                <#--                            <div class="col-sm-10">-->
                <#--                                <input class="form-control" name="payoutMinMoneyOfSingle" id="payoutMinMoneyOfSingle" autocomplete="off"-->
                <#--                                       required maxlength="10" type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"-->
                <#--                                       value="<#if config?exists>${config.payoutMinMoneyOfSingle !}</#if>" />-->
                <#--                            </div>-->
                <#--                        </div>-->
                <#--                        <div class="hr-line-dashed"></div>-->

                <#--                        <div class="form-group">-->
                <#--                            <label class="control-label col-sm-2">手续费比例（%）:</label>-->
                <#--                            <div class="col-sm-10">-->
                <#--                                <input class="form-control" name="payoutFeeRate" id="payoutFeeRate" autocomplete="off"-->
                <#--                                       required maxlength="10" type="number"-->
                <#--                                       value="<#if config?exists>${config.payoutFeeRate !}</#if>" />-->
                <#--                            </div>-->
                <#--                        </div>-->
                <#--                        <div class="hr-line-dashed"></div>-->

                <#--                    </div>-->
                <#--                </div>-->

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="submit" id="submit" value="保存"/>
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
<script type="text/javascript" src="${STATIC_URL}/lib/jquery.ajaxfileupload.js"></script>
<#--<script type="text/javascript" src="../../../../static/gv.js"></script>-->
<script>
    lay('#version').html('-v' + laydate.v);
    //时间选择器
    laydate.render({
        elem: '#user_withdraw_start_time',
        type: 'time',
        format: 'HH:mm'
    });
    //时间选择器
    laydate.render({
        elem: '#user_withdraw_end_time',
        type: 'time',
        format: 'HH:mm'
    });
    //input输入框只能输入数字和 小数点后两位
    function inputNum(obj,val){
        obj.value = obj.value.replace(/[^\d.]/g,""); //清除"数字"和"."以外的字符
        obj.value = obj.value.replace(/^\./g,""); //验证第一个字符是数字
        obj.value = obj.value.replace(/\.{2,}/g,""); //只保留第一个, 清除多余的
        obj.value = obj.value.replace(/^(\-)*(\d+)\.(\d).*$/,'$1$2.$3'); //只能输入两个小数
        if(obj.value.indexOf(".")< 0 && obj.value !=""){//以上已经过滤，此处控制的是如果没有小数点，首位不能为类似于 01、02的金额
            obj.value= parseFloat(obj.value);
        }
    }
</script>
<script type="text/javascript">

    function dateFormatter(value, row, index) {
        return DateUtils.formatyyyyMMddHHmm(value);
    }

    $('#submit').click(function () {

        $('#myModalDel #tips').text('确定保存吗？');
        $('#myModalDel').modal();

        return false;
    });

    $('#delete_submit').click(function () {

        var data = $('#form').serialize();

        console.log(data);

        $.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: "/alibaba888/Liv2sky3soLa93vEr62/updateBasicPlatformConfig" ,//url
            data: $('#form').serialize(),
            success: function (data) {
                if (data != null && data.code == 200) {
                    $.global.openSuccessMsg("保存成功");
                } else {
                    $.global.openErrorMsg(data.msg);
                }
            },
            error : function() {
                $.global.openErrorMsg('保存失败，请重试');
            }
        });
    });

</script>
</body>
</html>
