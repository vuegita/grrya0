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
            <h5>编辑配置</h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 每个类型只能添加一次!</p>
            <p  style="color: green">2. 新增和修改的类型需要1分钟之后生效!</p>
            <p  style="color: green">3. 大小单双数字强制小day周期!</p>
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.id}</#if>"/>

                <div class="form-group">
                    <label class="control-label col-sm-2">标题:</label>
                    <div class="col-sm-9">
                        <input class="form-control" name="title" id="title" autocomplete="off"
                               required type="text"
                               value="<#if entity?exists> ${entity.title !} </#if>" />
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label col-sm-2">描述:</label>
                    <div class="col-sm-9">
                        <input class="form-control" name="desc" id="desc" autocomplete="off"
                               required type="text"
                               value="<#if entity?exists> ${entity.desc !} </#if>" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属类型:</label>
                    <div class="col-sm-9">
                        <select class="form-control" id="targetType" name="targetType" >
                            <#if entity?exists>
                                <option value="${entity.getTargetType()}">${entity.getTargetType()} </option>
                            <#else>
                                <option value="">---请选择类型--- </option>
                                <#list targetTypeArr as item>
                                    <option value="${item.getKey()}">${item.getKey()} | ${item.getName()} </option>
                                </#list>
                            </#if>
                        </select>
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>周期类型:</label>
                    <div class="col-sm-9">
                        <select class="form-control" id="periodType" name="periodType" >
                            <#if entity?exists>
                                <option value="${entity.getPeriodType()}">${entity.getPeriodType()} </option>
                            <#else>
                                <option value="">---请选择类型--- </option>
                                <#list periodTypeArr as item>
                                    <option value="${item.getKey()}">${item.getKey()} </option>
                                </#list>
                            </#if>
                        </select>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">赠送金额:</label>
                    <div class="col-sm-9">
                        <input class="form-control" name="presentAmount" id="presentAmount" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               value="<#if entity?exists> ${entity.presentAmount !} </#if>" />
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label col-sm-2">限制条件金额:</label>
                    <div class="col-sm-9">
                        <input class="form-control" name="limitAmount" id="limitAmount" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               value="<#if entity?exists> ${entity.limitAmount !} </#if>" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">排序:</label>
                    <div class="col-sm-9">
                        <input class="form-control" name="sort" id="sort" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               value="<#if entity?exists> ${entity.sort !} </#if>" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">抽奖范围赠送金额列表:</label>
                    <div class="col-sm-9">
                        <input class="form-control" name="presentArrValue" id="presentArrValue" autocomplete="off"
                               required type="text"
                               value="<#if entity?exists> ${entity.presentArrValue !} </#if>" />
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>抽奖范围赠送状态:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">
                            <label>
                                <input type="radio"  name="presentArrEnable" id="presentArrEnable" value="enable" disabled
                                        <#if entity?exists && entity.presentArrEnable == 'enable'> checked </#if>/>
                                <i></i>启用
                            </label>

                            <label><input type="radio"  name="presentArrEnable" id="presentArrEnable" value="disable"
                                        <#if entity?exists && entity.presentArrEnable == 'disable'> checked </#if>/>
                                <i></i>禁用
                            </label>

                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <label style="margin-left: 220px;color: red"> 1. 抽奖范围赠送金额列表: 以逗号隔开,并且是整数; 示例   2,8,10:</label><br>
                    <label style="margin-left: 220px;color: red"> 2. 抽奖范围赠送状态：是否是真的赠送给会员!</label> <br>
                    <label style="margin-left: 220px;color: red"> 3. 此配置只针对week周期的类型起作用!</label> <br>
                    <label style="margin-left: 220px;color: red"> 4. 目前强制为假赠送! 由内部随机账户领取!</label>
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

        $('#search-btn').click(function(){
            $.ajax({
                url: '/alibaba888/Liv2sky3soLa93vEr62/findCoinCryptoContractInfoById',
                type: 'post',
                dataType: 'json',
                data:{
                    contractid: function () {
                        return $('input[name="contractid"]').val();
                    }
                },
                success: function(result){
                    if(result && result.code == 200)
                    {
                        var currencyType = result.data.currencyType;
                        var networkType = result.data.networkType;


                        $("input[name='quoteCurrency'][value='" + currencyType +"']").parent().addClass('checked').prop("checked", "checked");
                        $("input[name='networkType'][value='" + networkType +"']").parent().addClass('checked').prop("checked", "checked");

                    }
                    else
                    {
                        $.global.openErrorMsg(result.msg);
                    }
                },
                error: function(){
                    $.global.openErrorMsg('保存失败，请重试');
                }
            });
        });

        //确认保存
        $("#submitBtn").click(function () {

            var id = $('input[name="id"]').val();

            var title = $('input[name="title"]').val();
            var desc = $('input[name="desc"]').val();
            var presentAmount = $('input[name="presentAmount"]').val();
            var limitAmount = $('input[name="limitAmount"]').val();
            var sort = $('input[name="sort"]').val();

            var targetType = $('select[name="targetType"]').val();
            var periodType = $('select[name="periodType"]').val();
            var status = $('input[name="status"]:checked').val();

            var presentArrValue = $('input[name="presentArrValue"]').val();
            var presentArrEnable = $('input[name="presentArrEnable"]:checked').val();

            if(isEmpty(status) || isEmpty(targetType) || isEmpty(periodType) || isEmpty(presentAmount) || isEmpty(limitAmount) || isEmpty(title) || isEmpty(desc))
            {
                $.global.openErrorMsg('* 号为必填!');
                return;
            }

            if(isEmpty(presentArrValue) || isEmpty(presentArrEnable) )
            {
                $.global.openErrorMsg('* 号为必填!');
                return;
            }

            if(isEmpty(sort))
            {
                $.global.openErrorMsg('* 号为必填!');
                return;
            }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/root_gift_config/edit",
                data: {
                    id:id.trim(),
                    title:title,
                    desc:desc.trim(),
                    sort:sort.trim(),
                    targetType:targetType.trim(),
                    periodType:periodType.trim(),
                    presentAmount:presentAmount,
                    limitAmount:limitAmount.trim(),

                    presentArrValue:presentArrValue,
                    presentArrEnable:presentArrEnable,
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
