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
            <h5>编辑</h5>
            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 返佣比例范围  0 <= X <= 1, 0表示关闭赠送, 1表示走系统默认 100%赠送</p>
            <p  style="color: green">2. 设置完成1分钟之后生效!</p>
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.userid}</#if>"/>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>用户名:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="username" id="username" value="<#if entity?exists> ${entity.username} </#if>" <#if entity?exists> readonly </#if> autocomplete="off" required maxlength="50"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>


                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>自定义返佣状态:</label>
                    <div class="col-sm-3">
                        <div class="radio i-checks">
                            <label>
                                <input type="radio"  name="returnLevelStatus" id="returnLevelStatus" value="enable"
                                        <#if entity?exists && entity.returnLevelStatus == 'enable'> checked </#if>/>
                                <i></i>启用
                            </label>

                            <label><input type="radio"  name="returnLevelStatus" id="returnLevelStatus" value="disable"
                                        <#if entity?exists && entity.returnLevelStatus == 'disable'> checked </#if>/>
                                <i></i>禁用
                            </label>

                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2">返给一级比例:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="returnLv1Rate" id="returnLv1Rate" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity?exists> ${entity.returnLv1Rate?string("0.####") !} </#if> " />
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2">返给二级比例:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="returnLv2Rate" id="returnLv2Rate" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity?exists> ${entity.returnLv2Rate?string("0.####") !} </#if> " />
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2">接受一级比例:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="receivLv1Rate" id="receivLv1Rate" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity?exists> ${entity.receivLv1Rate?string("0.####") !} </#if> " />
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2">接受二级比例:</label>
                    <div class="col-sm-5">
                        <input class="form-control" name="receivLv2Rate" id="receivLv2Rate" autocomplete="off"
                               required type="text" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity?exists> ${entity.receivLv2Rate?string("0.####") !} </#if> " />
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


        $('.usertypeLabel').on("click",function() {
            var input = $(this).find("input");

            var userType = $(input).data('usertype');
            if(userType == 'staff')
            {
                $("#agentNameDIV").show();
            }
            else
            {
                $("#agentNameDIV").hide();
            }
        });


        //确认保存
        $("#submitBtn").click(function () {
            // var nickname = $('input[name="nickname"]').val();
            // var data = $('#form').serialize();

            // if(isEmpty(id) && isEmpty(type))
            // {
            //     $.global.openErrorMsg('* 号必填参数不能为空');
            //     return;
            // }
            //
            // if (isEmpty(username) || isEmpty(phone)  || isEmpty(email) || isEmpty(status) ){
            //     $.global.openErrorMsg('* 号必填参数不能为空');
            //     return;
            // }
            // if(subType=="simple" ||subType=="promotion" ){
            //     type="member"
            // }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/agent/passport/editUserAttr",
                data: $('#form').serialize(),
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
                error: function (e) {
                    console.log(e);
                    $.global.openErrorMsg('保存失败，请重试');
                }
            })
        });
    });

</script>
</body>
</html>
