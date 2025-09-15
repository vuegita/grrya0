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
            <h5>添加红包</h5>
            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 所有红包类型只有会员才能领</p>
            <p  style="color: green">2. 普通红包: 直接赠送彩金</p>
            <p  style="color: green">3. 固定红包: 只要输入总金额和红包个数</p>
<#--            <p  style="color: green">数字红包: 在普通红包的基础上，会员可选择玩法进行下注(红包个数固定10个)</p>-->
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <input class="form-control" name="id" id="id" autocomplete="off"  type="hidden" value="<#if periodInfo?exists> ${periodInfo.id} </#if>" />

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>红包金额:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="totalAmount" id="totalAmount" autocomplete="off"
                               required  type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');" value="10" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>最大金额:</label>-->
<#--                    <div class="col-sm-3">-->
<#--                        <input class="form-control" name="maxAmount" id="maxAmount" autocomplete="off"-->
<#--                               required  type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');" value="0" />-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>最小金额:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="totalAmount" id="totalAmount" autocomplete="off" readonly
                               required  type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"  value="0.01"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>最大金额:</label>-->
<#--                    <div class="col-sm-3">-->
<#--                        <input class="form-control" name="totalAmount" id="totalAmount" autocomplete="off"-->
<#--                               required  type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');" value="2" />-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>红包类型:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">
                            <label><input type="radio"  name="type" id="type" value="simple" checked <#if periodInfo?exists> readonly </#if>>
                                <i></i>普通红包
                            </label>

                            <label><input type="radio"  name="type" id="type" value="solid" <#if periodInfo?exists> readonly </#if>>
                                <i></i>固定红包
                            </label>

                            <label><input type="radio"  name="type" id="type" value="Specify" <#if periodInfo?exists> readonly </#if>>
                                <i></i>指定用户红包
                            </label>

                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">红包个数:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="totalCount" id="totalCount" autocomplete="off" <#if periodInfo?exists> readonly </#if>
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d]/g,'');" value="10" placeholder="" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">指定用户名称:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="specifyUserName" id="specifyUserName" autocomplete="off" <#if periodInfo?exists> readonly </#if>
                               required type="text"  value=""  placeholder="(请用输入用户名如up**********)"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group " >
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>失效时间:</label>
                    <div class="col-sm-10">
                        <div class="radio i-checks">

                            <#--                            <label><input type="radio"  name="expires" id="expires" value="1">-->
                            <#--                                <i></i>1分钟-->
                            <#--                            </label>-->
                            <label><input type="radio"  name="expires" id="expires" value="2" checked >
                                <i></i>2分钟
                            </label>

                            <label><input type="radio"  name="expires" id="expires" value="5">
                                <i></i>5分钟
                            </label>

                            <label><input type="radio"  name="expires" id="expires" value="10">
                                <i></i>10分钟
                            </label>

                            <label><input type="radio"  name="expires" id="expires" value="30">
                                <i></i>30分钟
                            </label>

                            <label><input type="radio"  name="expires" id="expires" value="60">
                                <i></i>1小时
                            </label>

                            <label><input type="radio"  name="expires" id="expires" value="120">
                                <i></i>2小时
                            </label>

                            <label><input type="radio"  name="expires" id="expires" value="1440">
                                <i></i>1天
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

<#include "../../../common/delete_form.ftl">
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js"></script>
<script type="text/javascript">
    $(function () {

        //确认保存
        $("#submitBtn").click(function () {
            $('#myModalDel #tips').text('确定创建红包吗?');
            $('#myModalDel').modal();
        });

        $('#delete_submit').click(function () {
            doSubmit();
        });

        function doSubmit()
        {
            var totalAmount = $('input[name="totalAmount"]').val();
            var totalCount = $('input[name="totalCount"]').val();

            var type = $('input[name="type"]:checked').val();
            var expires = $('input[name="expires"]:checked').val();

            var specifyUserName = $('input[name="specifyUserName"]').val();

            if(type=="Specify" && (isEmpty(specifyUserName) || specifyUserName=="" || specifyUserName==undefined)){
                $.global.openErrorMsg('用户名不能为空');
                return;
            }

            if(isEmpty(totalAmount) || totalAmount <= 0)
            {
                $.global.openErrorMsg('红包金额异常');
                return;
            }
            if(isEmpty(totalCount) || totalCount < 1)
            {
                $.global.openErrorMsg('红包个数最少为1');
                return;
            }
            debugger
            var maxCount = totalAmount / 0.01;
            if(maxCount > 1000)
            {
                maxCount = 1000;
            }

            // if(maxCount > 10000)
            // {
            //     maxCount = 10000;
            // }
            if(totalCount > maxCount)
            {
                $.global.openErrorMsg('当前金额最大红包个数为 ' + maxCount);
                return;
            }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/agent/game/red_package/create",
                data: $('#form').serialize(),
                dataType: "json",
                success: function (data) {
                    if (data != null && data.code == 200) {
                        $.global.openSuccessMsg("添加成功",function(){
                            //window.history.go(-1);
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
