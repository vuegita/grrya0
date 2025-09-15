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
            <h5>添加物料</h5>
        </div>

        <div class="ibox-content">

            <form id="form" class="form-horizontal" autocomplete="off">
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.id}</#if>"/>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>标题:</label>
                    <div class="col-sm-8">
                        <input class="form-control" name="name" id="name" value="<#if entity?exists> ${entity.name} </#if>" autocomplete="off" required maxlength="50"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>简介:</label>
                    <div class="col-sm-8">
                        <input class="form-control" name="desc" id="desc" value="<#if entity?exists> ${entity.desc} </#if>" autocomplete="off" required maxlength="100"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>唯一KEY:</label>
                    <div class="col-sm-8">
                        <input class="form-control" name="key" id="key" value="<#if entity?exists> ${entity.key} </#if>" autocomplete="off" required maxlength="100"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属分类:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="name" id="name" value="<#if categoryInfo?exists> ${categoryInfo.name} </#if>" autocomplete="off" readonly maxlength="100"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>
                <input type="hidden" name="categoryid" id="categoryid" value="<#if categoryInfo?exists>${categoryInfo.id}</#if>"/>

                <div class="form-group">
                    <label class="control-label col-sm-2">缩略图:</label>
                    <div class="col-sm-8">
                        <input class="form-control" name="thumb" id="thumb" value="<#if entity?exists> ${entity.thumb} </#if>" autocomplete="off" required maxlength="255"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">介绍图:</label>
                    <div class="col-sm-8">
                        <input class="form-control" name="introImg" id="introImg" value="<#if entity?exists> ${entity.introImg!} </#if>" autocomplete="off" required maxlength="255"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>跳转地址:</label>
                    <div class="col-sm-8">
                        <input class="form-control" name="jumpUrl" id="jumpUrl" value="<#if entity?exists> ${entity.jumpUrl} </#if>" autocomplete="off" required maxlength="255"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>单价:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="price" id="price"
                               onkeyup="this.value=this.value.replace(/[^\d\\.]/g,'');"
                               value="<#if entity?exists> ${entity.price} </#if>" autocomplete="off" required maxlength="100"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>事件类型:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">
                            <#list eventTypeList as item>
                                <#if entity?exists>
                                    <label>
                                        <input type="radio"  name="eventType" id="eventType" value="${item.getKey()}"
                                                <#if entity?exists && entity.eventType == item.getKey()> checked </#if>/>
                                        <i></i>${item.getName()}
                                    </label>
                                <#else>
                                    <label>
                                        <input type="radio"  name="eventType" id="eventType" value="${item.getKey()}"
                                                />
                                        <i></i>${item.getName()}
                                    </label>
                                </#if>

                            </#list>
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
            var id = $('input[name="id"]').val();
            var categoryid = $('input[name="categoryid"]').val();

            var key = $('input[name="key"]').val();
            var name = $('input[name="name"]').val();
            var desc = $('input[name="desc"]').val();
            var thumb = $('input[name="thumb"]').val();
            var introImg = $('input[name="introImg"]').val();
            var jumpUrl = $('input[name="jumpUrl"]').val();
            var price = $('input[name="price"]').val();
            var eventType = $('input[name="eventType"]:checked').val();
            var status = $('input[name="status"]:checked').val();

            if ( isEmpty(name) || isEmpty(status) ){
                $.global.openErrorMsg('* 号必填参数不能为空');
                return;
            }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/editAdMaterielInfo",
                data: {
                    id:id,
                    categoryid:categoryid,
                    key:key,
                    name: name,
                    desc: desc,
                    thumb: thumb,
                    jumpUrl: jumpUrl,
                    introImg:introImg,
                    price: price,
                    eventType:eventType,
                    status:status,
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
