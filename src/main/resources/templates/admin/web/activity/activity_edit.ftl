<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-用户管理</title>
</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>编辑配置</h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 活动开始之后，除了标题其它都无法修改!</p>
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.id}</#if>"/>

                <div class="form-group">
                    <label class="control-label col-sm-2">所属标题:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="title" id="title" autocomplete="off" maxlength="100"
                               required type="text"
                               value="<#if entity?exists> ${entity.title !} </#if>" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2">所属员工:</label>-->
<#--                    <div class="col-sm-5">-->
<#--                        <input class="form-control" name="staffname" id="staffname" autocomplete="off"-->
<#--                               required type="text"-->
<#--                               value="<#if entity?exists> ${entity.staffname !} </#if>" />-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属分类:</label>
                    <div class="col-sm-5">
                        <select class="form-control" id="businessType" name="businessType" <#if entity?exists> disabled </#if>>
                            <#if entity?exists>
                                <option value="${entity.getBusinessType()}">${entity.getBusinessType()} </option>
                            <#else>
                                <option value="">---请选择分类--- </option>
                                <#list activityBusinessArr as item>
                                    <option value="${item.getKey()}">${item.getKey()} | ${item.getRemark()}</option>
                                </#list>
                            </#if>
                        </select>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">最低邀请人数:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="limitMinInviteCount" id="limitMinInviteCount" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               placeholder="X >= 300" <#if entity?exists> disabled </#if>
                               value="<#if entity?exists> ${entity.limitMinInviteCount!} </#if>" />
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2">最低投资金额:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="limitMinInvesAmount" id="limitMinInvesAmount" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                <#if entity?exists> disabled </#if>
                               value="<#if entity?exists> ${entity.limitMinInvesAmount !} </#if>" />
                    </div>
                    <#--                    <div class="col-sm-5" style="color: red">等级为1时，最低投资金额填写0</div>-->
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">基础赠送金额:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="basicPresentAmount" id="basicPresentAmount" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                <#if entity?exists> disabled </#if>
                               value="<#if entity?exists> ${entity.basicPresentAmount?string('#.####')  !} </#if>" />
                    </div>
<#--                    <div class="col-sm-5" style="color: red">返回比例范围0~1, 例：1%的话填写0.01</div>-->
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2">额外赠送层级:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="extraPresentTier" id="extraPresentTier" autocomplete="off"
                               required type="text"
                               <#if entity?exists> disabled </#if>
                               value="<#if entity?exists> ${entity.extraPresentTier!} </#if>" />
                    </div>

                </div>

                <div class="form-group">
                    <label style="margin-left: 220px;color: red">  1. 配置格式  1000=0.1|2000=0.2|3000=0.3</label><br>
                    <label style="margin-left: 220px;color: red">  2. 如果需要至少需要配置两个等级，不需要则为空, 配置等级  2 <= X <= 5 !</label><br>
                    <label style="margin-left: 220px;color: red">  3. 不能有空格等其它符号 !</label> <br>
                    <label style="margin-left: 220px;color: red">  4. 额外赠送公式:  额外赠送金额 = (投资总金额 - 基础赠送金额) * 所处等级赠送比例 !</label> <br>
                    <label style="margin-left: 220px;color: red">  5. 获取赠送比例是根据 (投资总金额 - 基础赠送金额) 的金额来获取对应的等级配置 !</label> <br>
                </div>
                <div class="hr-line-dashed"></div>


                <#if  !(entity?exists && entity.id > 0) >
                    <div class="form-group">
                        <label class="control-label col-sm-2">开始时间:</label>
                        <div class="col-sm-5">
                            <input type="text" name="time" id="time" class="form-control input-outline"  placeholder="请选择时间" style="width:200px;">
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>状态:</label>
                        <div class="col-sm-5">
                            <div class="radio i-checks">

                                <label><input type="radio"  name="cycleDays" id="cycleDays" value="3"/>
                                    <i></i>3天
                                </label>

                                <label><input type="radio"  name="cycleDays" id="cycleDays" value="7" checked/>
                                    <i></i>7天
                                </label>

                                <label><input type="radio"  name="cycleDays" id="cycleDays" value="15"/>
                                    <i></i>15天
                                </label>
                            </div>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                </#if>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>状态:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">

                            <label><input type="radio"  name="status" id="status" value="new"
                                        <#if entity?exists && entity.status == 'new'> checked </#if>/>
                                <i></i>new
                            </label>

                            <label><input type="radio"  name="status" id="status" value="waiting"
                                        <#if entity?exists && entity.status == 'waiting'> checked </#if>/>
                                <i></i>waiting(待开始)
                            </label>

                            <label><input type="radio"  name="status" id="status" value="realized"
                                        <#if entity?exists && entity.status == 'realized'> checked </#if>/>
                                <i></i>realized(已结束)
                            </label>

                            <label><input type="radio"  name="status" id="status" value="failed"
                                        <#if entity?exists && entity.status == 'failed'> checked </#if>/>
                                <i></i>failed(下架)
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
    lay('#version').html('-v'+ laydate.v);
    //时间选择器
    layTime = laydate.render({
        elem: '#time',
        // type: 'date',
        range: false,
        format:'yyyy-MM-dd',
        min:1,
        max:30,
        change:function(value, date, endDate){

            var s = new Date(date.year+'-'+date.month+'-'+date.date);
            var e = new Date(endDate.year+'-'+endDate.month+'-'+endDate.date);
            //计算两个时间间隔天数
            var day=(e-s)/(1000*60*60*24);
            //console.log(date.year+'-'+date.month+'-'+date.date);
            //console.log(endDate.year+'-'+endDate.month+'-'+endDate.date);
            //console.log(day);
            if(day>90){
                layTime.hint('最多选择30天');
            }
        }
    });

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

            var businessType = $('select[name="businessType"]').val();
            // var currencyType = $('select[name="bu"]').val();

            var limitMinInviteCount = $('input[name="limitMinInviteCount"]').val();
            var limitMinInvesAmount = $('input[name="limitMinInvesAmount"]').val();
            var basicPresentAmount = $('input[name="basicPresentAmount"]').val();
            var extraPresentTier = $('input[name="extraPresentTier"]').val();

            var status = $('input[name="status"]:checked').val();

            var begintime = $('input[name="time"]').val();
            var cycleDays = $('input[name="cycleDays"]:checked').val();

            if(isEmpty(title) || isEmpty(businessType))
            {
                $.global.openErrorMsg('* 号为必填!');
                return;
            }

            if(isEmpty(title))
            {
                $.global.openErrorMsg('* 号为必填!');
                return;
            }

            if(isEmpty(limitMinInviteCount) || isEmpty(limitMinInvesAmount))
            {
                $.global.openErrorMsg('* 号为必填!');
                return;
            }

            if(isEmpty(basicPresentAmount))
            {
                $.global.openErrorMsg('* 号为必填!');
                return;
            }

            if(isEmpty(extraPresentTier))
            {
                extraPresentTier = "";
            }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/updateWebActivityConfig",
                data: {
                    id:id.trim(),
                    title:title.trim(),

                    businessType:businessType.trim(),

                    limitMinInviteCount:limitMinInviteCount.trim(),
                    limitMinInvesAmount:limitMinInvesAmount.trim(),
                    basicPresentAmount:basicPresentAmount.trim(),
                    extraPresentTier:extraPresentTier.trim(),

                    status:status.trim(),

                    begintime:begintime,
                    cycleDays:cycleDays,
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
