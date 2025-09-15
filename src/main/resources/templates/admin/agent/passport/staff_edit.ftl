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
            <h5>添加员工</h5>

            <p style="color: blue"> 　</p>
            <p  style="color: green">1. 每个代理最多添加50个员工!</p>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if userinfo?exists>${userinfo.id}</#if>"/>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>用户名:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="username" id="username" <#if userinfo?exists> disabled </#if> value="<#if userinfo?exists> ${userinfo.name} </#if>" autocomplete="off" required maxlength="50"/>
                    </div>
                    <div class="form-group">
                        <label class="control-label" style="color: red;margin-left: 20px">用户名要求数字和字母组合长度6位到20位，（如：user01）</label><br>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>登陆后台:</label>
                    <div class="col-sm-3">
                        <div class="radio i-checks">
                            <label>
                                <input type="radio"  name="loginAgentStatus" id="loginAgentStatus" value="enable"
                                        <#if userinfo?exists && userinfo.loginAgentStatus == 'enable'> checked </#if>/>
                                <i></i>启用
                            </label>

                            <label><input type="radio"  name="loginAgentStatus" id="loginAgentStatus" value="disable"
                                        <#if userinfo?exists && userinfo.loginAgentStatus == 'disable'> checked </#if>/>
                                <i></i>禁用
                            </label>

                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>状态:</label>
                    <div class="col-sm-3">
                        <div class="radio i-checks">
                            <label>
                                <input type="radio"  name="status" id="status" value="enable"
                                        <#if userinfo?exists && userinfo.status == 'enable'> checked </#if>/>
                                <i></i>启用
                            </label>

                            <label><input type="radio"  name="status" id="status" value="disable"
                                        <#if userinfo?exists && userinfo.status == 'disable'> checked </#if>/>
                                <i></i>禁用
                            </label>

                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>手机:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="phone" id="phone"
                               placeholder="长度: 8 <= X <= 11"
                               value="<#if userinfo?exists> ${userinfo.phone} </#if>" <#if userinfo?exists> readonly </#if> autocomplete="off" required maxlength="50"/>
                    </div>

                    <div class="form-group">
                        <label class="control-label" style="color: red;margin-left: 20px">手机号8到11位可以随意输入</label><br>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>邮箱:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="email" id="email" value="<#if userinfo?exists> ${userinfo.email} </#if>" autocomplete="off" required maxlength="50"/>
                    </div>

                    <div class="form-group">
                        <label class="control-label" style="color: red;margin-left: 20px">邮箱格式,如:test001@gmail.com</label><br>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="button" id="submitBtn" value="保存"/>
                        <button class="btn btn-white" type="button" onclick="window.history.back();">取消</button>
                    </div>

                </div>

                <#if !userinfo?exists>
                    <div class="form-group">
                        <label class="control-label" style="color: red;margin-left: 200px">新增员工的默认密码为：${password}（登录员工后台可以修改密码）</label><br>
                    </div>
                </#if>

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
            debugger

            // var nickname = $('input[name="nickname"]').val();
            var id = $('input[name="id"]').val();
            var username = $('input[name="username"]').val();
            var agentname = $('input[name="agentname"]').val();
            var phone = $('input[name="phone"]').val();
            var email = $('input[name="email"]').val();
            var type = $('input[name="type"]:checked').val();
            var status = $('input[name="status"]:checked').val();
            var loginAgentStatus = $('input[name="loginAgentStatus"]:checked').val();


            // if(isEmpty(id) && isEmpty(type))
            // {
            //     $.global.openErrorMsg('* 号必填参数不能为空');
            //     return;
            // }

            if (isEmpty(username) || isEmpty(phone)  || isEmpty(email) || isEmpty(status) ){
                $.global.openErrorMsg('* 号必填参数不能为空');
                return;
            }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/agent/passport/addStaff",
                data: {
                    id:id,
                    username: username,
                    agentname:agentname,
                    phone:phone,
                    email:email,
                    type:type,
                    status:status,
                    loginAgentStatus:loginAgentStatus,
                    password: $.md5('${password}'),
                },
                dataType: "json",
                success: function (data) {
                    if (data != null && data.code == 200) {
                        $.global.openSuccessMsg("保存成功,请重新刷新页面",function(){
                            window.history.go(-1);
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
