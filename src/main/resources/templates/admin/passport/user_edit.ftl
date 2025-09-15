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
            <h5>添加用户</h5>
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if userinfo?exists>${userinfo.id}</#if>"/>

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>昵称:</label>-->
<#--                    <div class="col-sm-3">-->
<#--                        <input class="form-control" name="nickname" id="nickname" value="<#if userinfo?exists> ${userinfo.nickname} </#if>" autocomplete="off" required maxlength="50"/>-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>用户名:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="username" id="username" value="<#if userinfo?exists> ${userinfo.name} </#if>" <#if userinfo?exists> readonly </#if> autocomplete="off" required maxlength="50"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <#if !userinfo??>
                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>用户类型:</label>
                        <div class="col-sm-5">
                            <div class="radio i-checks" >

                                <#if environment == 'dev'>
                                    <label class="usertypeLabel">
                                        <input type="radio"  name="type" id="type" value="member"
                                                <#if userinfo?exists && userinfo.type == 'member'> checked </#if>
                                                <#if userinfo?exists> disabled </#if>
                                        >
                                        <i></i>会员
                                    </label>
                                </#if>

                                <label class="usertypeLabel"><input type="radio"  name="type" id="type" value="staff" data-usertype="staff"
                                            <#if userinfo?exists && userinfo.type == 'staff'> checked </#if>
                                            <#if userinfo?exists> disabled </#if>>
                                    <i></i>员工
                                </label>

                                <label class="usertypeLabel"><input type="radio"  name="type" id="type" value="agent"
                                            <#if userinfo?exists && userinfo.type == 'agent'> checked </#if>
                                            <#if userinfo?exists> disabled </#if>>
                                    <i></i>代理
                                </label>

                                <label class="usertypeLabel"><input type="radio"  name="type" id="type" value="test"
                                            <#if userinfo?exists && userinfo.type == 'test'> checked </#if>
                                            <#if userinfo?exists> disabled </#if>>
                                    <i></i>测试
                                </label>

<#--                                <label class="usertypeLabel"><input type="radio"  name="type" id="type" value="robot"-->
<#--                                            <#if userinfo?exists && userinfo.type == 'robot'> checked </#if>-->
<#--                                            <#if userinfo?exists> disabled </#if>>-->
<#--                                    <i></i>机器人-->
<#--                                </label>-->

                            </div>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group" id="agentNameDIV" style="display: none">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属代理:</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="agentname" id="agentname" value="" autocomplete="off" required maxlength="50"/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>
                </#if>

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

                <!-- 会员和推广人员可以相关切换 -->
                <#if userinfo?? && userinfo.type == 'member' && (userinfo.subType == 'simple' || userinfo.subType == 'promotion')>
                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>会员子类型:</label>
                        <div class="col-sm-5">
                            <div class="radio i-checks" >

                                <label>
                                    <input type="radio"  name="subType" id="subType" value="simple"
                                            <#if userinfo?exists && userinfo.subType == 'simple'> checked </#if>
                                    >
                                    <i></i>普通会员
                                </label>

                                <label><input type="radio"  name="subType" id="subType" value="promotion" data-usertype="promotion"
                                            <#if userinfo?exists && userinfo.subType == 'promotion'> checked </#if>>
                                    <i></i>推广会员
                                </label>

                            </div>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>
                </#if>

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

                            <label><input type="radio"  name="status" id="status" value="freeze"
                                        <#if userinfo?exists && userinfo.status == 'freeze'> checked </#if>/>
                                <i></i>冻结
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
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>邮箱:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="email" id="email" value="<#if userinfo?exists> ${userinfo.email} </#if>" autocomplete="off" required maxlength="50"/>
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
            console.log("1111111111111111");
            debugger
            // var nickname = $('input[name="nickname"]').val();
            var id = $('input[name="id"]').val();
            var username = $('input[name="username"]').val();
            var agentname = $('input[name="agentname"]').val();
            var phone = $('input[name="phone"]').val();
            var email = $('input[name="email"]').val();
            var type = $('input[name="type"]:checked').val();
            var subType = $('input[name="subType"]:checked').val();
            var status = $('input[name="status"]:checked').val();
            var loginAgentStatus = $('input[name="loginAgentStatus"]:checked').val();

            if(isEmpty(id) && isEmpty(type))
            {
                $.global.openErrorMsg('* 号必填参数不能为空');
                return;
            }

            if (isEmpty(username) || isEmpty(phone)  || isEmpty(email) || isEmpty(status) ){
                $.global.openErrorMsg('* 号必填参数不能为空');
                return;
            }
            if(subType=="simple" ||subType=="promotion" ){
                type="member"
            }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/addUser",
                data: {
                    id:id,
                    username: username,
                    agentname:agentname,
                    phone:phone,
                    email:email,
                    type:type,
                    subType:subType,
                    status:status,
                    loginAgentStatus:loginAgentStatus,
                    password: $.md5('123456'),
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
