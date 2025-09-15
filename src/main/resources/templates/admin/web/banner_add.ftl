<!DOCTYPE HTML>
<html>
<head>
    <#include "../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-订单管理</title>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/plugins/bootstrap-table/bootstrap-table.min.css"/>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/css/plugins/webuploader/webuploader.css"/>
    <link type="text/css" rel="stylesheet" href="${STATIC_URL}/css/uploadImg.css"/>
    <script type="text/javascript" src="${STATIC_URL}/js/plugins/webuploader/webuploader.js"></script>

</head>
<body class="gray-bg">
<div class="wrapper wrapper-content animated fadeInRight">
    <div class="ibox float-e-margins">
        <div class="ibox-title">
            <h5>编辑客服</h5>
        </div>

<#--        banner_title       		varchar(100) NOT NULL comment '标题',-->
<#--        banner_content       		varchar(255) NOT NULL comment '描述',-->

<#--        banner_type       		varchar(255) NOT NULL comment '类型=ad|game_ab|game_rg|game_fruit|game_fm|game_redpackage等等',-->
<#--        banner_img       			varchar(255) NOT NULL comment '图片',-->
<#--        banner_web_url       	    varchar(255) NOT NULL comment '跳转地址',-->

<#--        banner_force_login        varchar(20) NOT NULL default 'disable' COMMENT '是否强制登陆: enale|disable',-->
<#--        banner_status             varchar(20) NOT NULL COMMENT 'enale|disable',-->

<#--        banner_admin              varchar(20) NOT NULL default 'disable' COMMENT '操作人|审核人',-->
<#--        banner_createtime  		datetime NOT NULL,-->
<#--        banner_updatetime  		datetime NOT NULL,-->
<#--        banner_remark 			varchar(1000) NOT NULL DEFAULT '' COMMENT 'json参数',-->

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <#if bannerInfo??>
                    <input type="hidden" id="id" name="id" value="${bannerInfo.id}" autocomplete="off" required maxlength="50"/>
                </#if>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>标题:</label>
                    <div class="col-sm-3">
                        <input class="form-control" name="title" id="title" autocomplete="off" maxlength="20"
                               required  type="text"  value="<#if bannerInfo?exists> ${bannerInfo.title!} </#if>" placeholder="标题(请用英文-20个字符以内)"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>描述:</label>
                    <div class="col-sm-10">
                        <input class="form-control" name="content" id="content" autocomplete="off" maxlength="50"
                               required  type="text"  value="<#if bannerInfo?exists> ${bannerInfo.content!} </#if>" placeholder="描述(请用英文-50个字符以内)"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>图片:</label>-->
<#--                    <div class="col-sm-3">-->
<#--                        <input class="form-control" name="img" id="img" autocomplete="off" maxlength="20"-->
<#--                               required  type="text"  value="<#if bannerInfo?exists> ${bannerInfo.img!} </#if>" placeholder="标题(请用英文-20个字符以内)"/>-->
<#--                    </div>-->
<#--                </div>-->
<#--                <div class="hr-line-dashed"></div>-->


                <div class="form-group">
                    <label class="control-label col-sm-2">图片:</label>
                    <img src="<#if bannerInfo?exists>${bannerInfo.img!}</#if>"  width="200" height="100">
                    <div class="col-sm-10">
                        <input class="form-control" name="img" id="img" readonly autocomplete="off"
                               required maxlength="10" type="hidden"
                               value="<#if bannerInfo?exists>${bannerInfo.img!}</#if>" />
                    </div>
                </div>



                <div class="form-group">
                    <label class="control-label col-sm-2">上传图片:</label>
                    <div class="col-sm-10">
                        <input  name="file" id="input-file" type="file"/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>






                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>跳转链接:</label>
                    <div class="col-sm-6">
                        <input type="text" class="form-control" id="webUrl" name="webUrl" value="<#if bannerInfo??>${bannerInfo.webUrl}</#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="form-group">
                    <label class="control-label" style="color: red;margin-left: 200px">不跳转时，请填写0！！！</label><br>
                </div>
                <div class="hr-line-dashed"></div>



                <div class="form-group">
                    <label class="control-label col-sm-2">状态:</label>
                    <div class="col-sm-3">
                        <div class="radio i-checks">
                            <#if !bannerInfo??>
                                <label><input type="radio" name="status" value="enable" checked/> <i></i>启用</label>
                                <label><input type="radio" name="status" value="disable" /> <i></i>禁用</label>
                            <#else>
                                <label><input type="radio" name="status" value="enable" <#if bannerInfo.status == "enable"> checked </#if> /> <i></i>启用</label>
                                <label><input type="radio" name="status" value="disable" <#if bannerInfo.status == "disable"> checked </#if> /> <i></i>禁用</label>
                            </#if>
                        </div>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <div class="col-sm-4 col-sm-offset-2">
                        <input class="btn btn-primary" type="button" id="mysubmit" value="保存"/>
                        <button class="btn btn-white" type="button" onclick="window.close();">取消</button>
                    </div>
                </div>


            </form>
        </div>
    </div>
</div>

<!-- loading -->
<div class="modal fade" id="loading" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop='static'>
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h4 class="modal-title" id="myModalLabel">提示</h4>
            </div>
            <div class="modal-body">
                上传中。。。<span id="result"></span>
            </div>
        </div>
    </div>
</div>

<#include "../../common/delete_form.ftl">

<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/lib/jquery.ajaxfileupload.js"></script>
<script type="text/javascript">
    $(function () {


        $('#search-btn').click(function(){

            var username = $('input[name="staffname"]').val();

            if(isEmpty(username))
            {
                $.global.openErrorMsg('* 号必填参数不能为空');
                return;
            }

            $.ajax({
                url: '/alibaba888/Liv2sky3soLa93vEr62/findUserInfo',
                type: 'post',
                dataType: 'json',
                data:{
                    username: staffname,
                },
                success: function(result){
                    console.log(result);
                    if(result && result.code == 200)
                    {
                        var balance = result.data.balance;
                        if(balance == null)
                        {
                            balance = 0;
                        }
                        $("#balance").prop("value", balance);
                    }
                    else
                    {
                        $.global.openErrorMsg(result.msg);
                    }
                },
                error: function(){
                    $.global.openErrorMsg('系统异常!');
                }
            });
        });

        $('#mysubmit').click(function () {
            var title =  $('input[name="title"]').val();
            var content = $('input[name="content"]').val();
            var img =  $('input[name="img"]').val();
            var webUrl = $('input[name="webUrl"]').val();
            var status = $('input[name="status"]:checked').val();

            if(isEmpty(content) || isEmpty(title)  || isEmpty(webUrl) || isEmpty(status))
            {
                $.global.openErrorMsg('* 号必填参数不能为空');
                return false;
            }

            var $file1 = $("input[name='file']").val();//用户文件内容(文件)
            // 判断文件是否为空
            if(isEmpty(img)){
                if ($file1 == "" ) {
                    alert("请选择上传的目标文件! ")
                    return false;
                }
            }

            $('#myModalDel #tips').text('确定提交吗？');
            $('#myModalDel').modal();

            return false;
        });

        $('#delete_submit').click(function () {
            var $file1 = $("input[name='file']").val();//用户文件内容(文件)
            if ($file1 == "" ) {
                addData();
            }else{
                uploadBannerAddData();

            }


        });

    });

    function changeType() {
        alert();
        // var type = $('input:radio[name="type"]:checked').val();
        //
        // console.log("type = " + type)
        //
        // if(type === 'bank'){
        //     $("#ifscDIV").removeClass("hidden");
        // }
        // else
        // {
        //     $("#ifscDIV").addClass("hidden");
        // }
    }

    function uploadBannerAddData() {
        var id = $('input[name="id"]').val();
        var title =  $('input[name="title"]').val();
        var content = $('input[name="content"]').val();
        var img =  $('input[name="img"]').val();
        var webUrl = $('input[name="webUrl"]').val();
        var status = $('input[name="status"]:checked').val();

        if(isEmpty(content) || isEmpty(title) || isEmpty(webUrl) || isEmpty(status))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return false;
        }

        var $file1 = $("input[name='file']").val();//用户文件内容(文件)
        if ($file1 == "" ) {
            alert("请选择上传的目标文件! ")
            return false;
        }

        $('#loading').modal('show');
        var fileObj = document.getElementById("input-file").files[0];
        var formData = new FormData();
        formData.append("file", fileObj);


        $.ajax({
            type : "post",
            dataType: "json",//预期服务器返回的数据类型
            url: "/alibaba888/Liv2sky3soLa93vEr62/uploadBannerFile" ,//url
            data : formData,
            processData : false,
            contentType : false,

            success: function (data) {

                if (data != null && data.code == 200) {
                    //隐藏
                    $('#loading').modal('hide');
                    // accessUrl: "http://192.168.1.233:8180/uploads/web/Banner/2021/10/13/file/b/bf/bf7c3284066c4f78954221b0e218e826..png"
                    // relateUrl: "/web/Banner/2021/10/13/file/b/bf/bf7c3284066c4f78954221b0e218e826..png"

                   // $.global.openSuccessMsg("上传成功");
                    addData(data.data.accessUrl)

                    setTimeout(()=>{
                        location.reload();
                    },1000)


                } else {
                    //隐藏
                    $('#loading').modal('hide');
                    $.global.openErrorMsg(data.msg);
                }
            },
            error : function() {
                //隐藏
                $('#loading').modal('hide');
                $.global.openErrorMsg('上传失败，请重试');
            }
        });

    }

    function addData(imgUrl) {

        var id = $('input[name="id"]').val();
        var title =  $('input[name="title"]').val();
        var content = $('input[name="content"]').val();
        var img =  $('input[name="img"]').val();
        var webUrl = $('input[name="webUrl"]').val();
        var status = $('input[name="status"]:checked').val();

        if(isEmpty(content) || isEmpty(title) || isEmpty(webUrl) || isEmpty(status))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return false;
        }
        if(isEmpty(imgUrl)){
            if(isEmpty(img)){
                $.global.openErrorMsg('* 号必填参数不能为空');
                return false;
            }
        }else{
            img=imgUrl;
        }

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/editWebBanner',
            type: 'post',
            dataType: 'json',
            data:{
                id:id,
                title:title,
                content:content,
                img:img,
                webUrl:webUrl,
                status:status
            },
            success: function(result){
                console.log(result);
                if(result && result.code == 200)
                {
                    $.global.openSuccessMsg("添加成功",function(){
                        window.close();
                    });
                }
                else
                {
                    $.global.openErrorMsg(result.msg);
                }
            },
            error: function(){
                $.global.openErrorMsg('系统异常!');
            }
        });
    }





</script>
</body>
</html>
