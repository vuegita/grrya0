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
            <h5>添加产品</h5>
            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 添加完成请点击编辑更新状态为销售中！</p>
            <p  style="color: green">2. 草稿状态可编辑, 一旦变更为销售中，产品参数不可变更！</p>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <input class="form-control" name="id" id="id" autocomplete="off"  type="hidden" value="<#if productInfo?exists> ${productInfo.id} </#if>" />

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>标题:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="title" id="title" autocomplete="off" maxlength="20"
                               required  type="text"  value="<#if productInfo?exists> ${productInfo.title!} </#if>" placeholder="标题(请用英文-20个字符以内)"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>描述:</label>
                    <div class="col-sm-10">
                        <input class="form-control" name="desc" id="desc" autocomplete="off" maxlength="50"
                               required  type="text"  value="<#if productInfo?exists> ${productInfo.desc!} </#if>" placeholder="描述(请用英文-50个字符以内)"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group " >
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>投资期限:</label>
                    <div class="col-sm-10">
                        <div class="radio i-checks">
                            <#if productInfo?exists>
                                <label><input type="radio"  name="time_horizon" id="time_horizon" value="1" <#if productInfo.timeHorizon == 1> checked</#if> disabled >
                                    <i></i>1天
                                </label>

                                <label><input type="radio"  name="time_horizon" id="time_horizon" value="3" <#if productInfo.timeHorizon == 3> checked</#if> disabled>
                                    <i></i>3天
                                </label>

                                <label><input type="radio"  name="time_horizon" id="time_horizon" value="7" <#if productInfo.timeHorizon == 7> checked</#if> disabled>
                                    <i></i>7天
                                </label>
                            <#else>
                                <label><input type="radio"  name="time_horizon" id="time_horizon" value="1" checked >
                                    <i></i>1天
                                </label>

                                <label><input type="radio"  name="time_horizon" id="time_horizon" value="3">
                                    <i></i>3天
                                </label>

                                <label><input type="radio"  name="time_horizon" id="time_horizon" value="7">
                                    <i></i>7天
                                </label>
                            </#if>


                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <#if !productInfo?exists>
                    <div class="form-group " >
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>开售时间:</label>
                        <div class="col-sm-10">
                            <div class="radio i-checks">

                                <label><input type="radio"  name="afterCreatetime" id="afterCreatetime" value="5"  >
                                    <i></i>5分钟后
                                </label>

                                <label><input type="radio"  name="afterCreatetime" id="afterCreatetime" value="10" checked>
                                    <i></i>10分钟
                                </label>

                                <label><input type="radio"  name="afterCreatetime" id="afterCreatetime" value="30">
                                    <i></i>30分钟
                                </label>

                                <label><input type="radio"  name="afterCreatetime" id="afterCreatetime" value="60">
                                    <i></i>1小时
                                </label>

                                <label><input type="radio"  name="afterCreatetime" id="afterCreatetime" value="120">
                                    <i></i>2小时
                                </label>

                            </div>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                <#else>
                    <div class="form-group " >
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>状态:</label>
                        <div class="col-sm-10">
                            <div class="radio i-checks">

                                <#if productInfo.status == 'new'>
                                <label><input type="radio"  name="status" id="status" value="new" <#if productInfo.status == 'new'> checked</#if>>
                                    <i></i>草稿
                                </label>
                                </#if>

                                <#if productInfo.status == 'saling' || productInfo.status == 'new'>
                                <label><input type="radio"  name="status" id="status" value="saling" <#if productInfo.status == 'saling'> checked</#if>>
                                    <i></i>销售中
                                </label>
                                </#if>

                                <label><input type="radio"  name="status" id="status" value="saled" <#if productInfo.status == 'saled'> checked</#if>>
                                    <i></i>已售磬
                                </label>

                                <label><input type="radio"  name="status" id="status" value="realized" <#if productInfo.status == 'realized'> checked</#if>>
                                    <i></i>已完成
                                </label>

                                <label><input type="radio"  name="status" id="status" value="discard" <#if productInfo.status == 'discard'> checked</#if>>
                                    <i></i>已丢弃
                                </label>

                            </div>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                </#if>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>预期最少收益率:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="return_expected_start" id="return_expected_start" autocomplete="off"
                               required  type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if productInfo?exists> ${productInfo.returnExpectedStart!} <#else> 0.01 </#if>" <#if productInfo?exists> readonly </#if> />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>预期最大收益率:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="return_expected_end" id="return_expected_end" autocomplete="off"
                               required  type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if productInfo?exists> ${productInfo.returnExpectedEnd!} <#else> 0.02 </#if>" <#if productInfo?exists> readonly </#if> />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">实际收益率:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="return_real_rate" id="return_real_rate" autocomplete="off"
                               required  type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               placeholder="设置为0表示由系统随机, 数字在最小和最大之间"
                               value="<#if productInfo?exists> ${productInfo.returnRealRate!} </#if>" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>预售总份额:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="sale_estimate" id="sale_estimate" autocomplete="off"
                               required  type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               value="<#if productInfo?exists> ${productInfo.saleEstimate!} <#else> 100 </#if>" <#if productInfo?exists> readonly </#if> />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>认购最小金额:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="limit_min_sale" id="limit_min_sale" autocomplete="off"
                               required  type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               value="<#if productInfo?exists> ${productInfo.limitMinSale!} <#else> 1 </#if>"
                                <#if productInfo?exists> readonly </#if>/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>认购最大金额:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="limit_max_sale" id="limit_max_sale" autocomplete="off"
                               required  type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               value="<#if productInfo?exists> ${productInfo.limitMaxSale!} <#else> 100 </#if>" <#if productInfo?exists> readonly </#if>/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>认购最低投注额:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="limit_min_bets" id="limit_min_bets" autocomplete="off"
                               required  type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if productInfo?exists> ${productInfo.limitMinBets!} <#else> 100 </#if>" <#if productInfo?exists> readonly </#if>/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>认购最低账户余额:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="limit_min_balance" id="limit_min_balance" autocomplete="off"
                               required  type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');"
                               value="<#if productInfo?exists> ${productInfo.limitMinBalance!} <#else> 100 </#if>" <#if productInfo?exists> readonly </#if>/>
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

<#include "../../common/delete_form.ftl">
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js"></script>
<script type="text/javascript">
    $(function () {

        //确认保存
        $("#submitBtn").click(function () {
            $('#myModalDel #tips').text('确定创建产品吗?');
            $('#myModalDel').modal();
        });

        $('#delete_submit').click(function () {
            doSubmit();
        });

        function doSubmit()
        {
            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/game/fm_product/edit",
                data: $('#form').serialize(),
                dataType: "json",
                success: function (data) {
                    if (data != null && data.code == 200) {
                        $.global.openSuccessMsg("添加成功",function(){
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
        }
    });

</script>
</body>
</html>
