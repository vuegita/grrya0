<!DOCTYPE HTML>
<html xmlns="http://www.w3.org/1999/html">
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
                <input type="hidden" name="id" id="id" value="<#if entity?exists>${entity.materielid}</#if>"/>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>内容:</label>
                    <div class="col-sm-8">
<#--                        <input class="form-control" name="content" id="content" value="<#if entity?exists> ${entity.content} </#if>" autocomplete="off" required maxlength="50"/>-->
                        <textarea  rows="8" class="form-control" name="content" id="content" autocomplete="off" required maxlength="50"> <#if entity?exists> ${entity.content} </#if> </textarea>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>尺寸大小:</label>
                    <div class="col-sm-8">
                        <input class="form-control" name="sizes" id="sizes" value="<#if entity?exists> ${entity.sizes} </#if>" autocomplete="off" required maxlength="100"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>图片介绍:</label>
                    <div class="col-sm-8">
<#--                        <input class="form-control" name="images" id="images" value="<#if entity?exists> ${entity.images} </#if>" autocomplete="off" required maxlength="100"/>-->
                        <textarea  rows="8" class="form-control" name="images" id="images" autocomplete="off" required maxlength="50"> <#if entity?exists> ${entity.images} </#if> </textarea>
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
            var content = $('#content').val();
            var sizes = $('input[name="sizes"]').val();
            var images = $('#images').val();

            if ( isEmpty(content) ){
                $.global.openErrorMsg('* 号必填参数不能为空');
                return;
            }

            $.ajax({
                type: "post",
                async: false,
                url: "/alibaba888/Liv2sky3soLa93vEr62/editAdMaterielDetailInfo",
                data: {
                    id:id,
                    content:content,
                    sizes:sizes,
                    images: images,
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
