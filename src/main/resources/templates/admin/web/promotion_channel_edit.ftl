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
            <h5>编辑</h5>
        </div>

        <div class="ibox-content">
            <form id="form" class="form-horizontal" autocomplete="off">

                <#if entity??>
                    <input type="hidden" id="id" name="id" value="${entity.id}" autocomplete="off" required maxlength="50"/>
                </#if>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>员工名:</label>
                    <div class="col-sm-5">
                        <input type="text" class="form-control" id="staffname" name="staffname" value="<#if entity??>${entity.staffname}</#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>类型:</label>
                    <div class="col-sm-5">
                        <select class="form-control" id="type" name="type" >
                            <option value="">请选择类型</option>
                            <#list typeArr as group>
                                <option value="${group.key}" <#if entity?exists && entity.type == group.key > selected </#if> >${group.key}</option>
                            </#list>
                        </select>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>名称:</label>
                    <div class="col-sm-5">
                        <input type="text" class="form-control" id="name" name="name" value="<#if entity??>${entity.name}</#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>联系方式:</label>
                    <div class="col-sm-5">
                        <input type="text" class="form-control" id="contact" name="contact" value="<#if entity??>${entity.contact}</#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>地址:</label>
                    <div class="col-sm-5">
                        <input type="text" class="form-control" id="url" name="url" value="<#if entity??>${entity.url}</#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>订阅数:</label>
                    <div class="col-sm-5">
                        <input type="text" class="form-control" id="subscribeCount" name="subscribeCount"
                               onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity??>${entity.subscribeCount}</#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>金额:</label>
                    <div class="col-sm-5">
                        <input type="text" class="form-control" id="amount" name="amount"
                               onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                               value="<#if entity??>${entity.amount}</#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>备注:</label>
                    <div class="col-sm-5">
                        <input type="text" class="form-control" id="remark" name="remark" value="<#if entity??>${entity.remark}</#if>" autocomplete="off" maxlength="255"
                               value=""/>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-2">状态:</label>
                    <div class="col-sm-5">
                        <div class="radio i-checks">
                            <#if !entity??>
                                <label><input type="radio" name="status" value="enable" checked/> <i></i>启用</label>
                                <label><input type="radio" name="status" value="disable" /> <i></i>禁用</label>
                            <#else>
                                <label><input type="radio" name="status" value="enable" <#if entity.status == "enable"> checked </#if> /> <i></i>启用</label>
                                <label><input type="radio" name="status" value="disable" <#if entity.status == "disable"> checked </#if> /> <i></i>禁用</label>
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


<#include "../../common/delete_form.ftl">

<script type="text/javascript" src="${STATIC_URL}/plugins/UEditor/js/ueditor.config.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/UEditor/js/ueditor.all.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
<script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/js/jquery.md5.js?v=${version}"></script>
<script type="text/javascript" src="${STATIC_URL}/lib/jquery.ajaxfileupload.js"></script>
<script type="text/javascript">
    $(function () {
        <#--var ue = UE.getEditor('container');-->
        <#--//var ue = UE.getContent();-->
        <#--//对编辑器的操作最好在编辑器ready之后再做-->
        <#--ue.ready(function() {-->
        <#--    //设置编辑器的内容-->
        <#--    ue.setContent('${content!}');-->
        <#--    //获取html内容，返回: <p>hello</p>-->
        <#--    var html = ue.getContent();-->
        <#--    //获取纯文本内容，返回: hello-->
        <#--    var txt = ue.getContentTxt();-->
        <#--});-->


        $('#search-btn').click(function(){

            var username = $('input[name="agentname"]').val();

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
                    username: username,
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
            $('#myModalDel #tips').text('确定提交吗？');
            $('#myModalDel').modal();
            return false;
        });

        $('#delete_submit').click(function () {
            addData();
        });

    });

    function addData() {
        var id = $('input[name="id"]').val();
        var staffname = $('input[name="staffname"]').val();
        var name =  $('input[name="name"]').val();
        var type =$('select[name="type"]').val(); // var type="agentTips";

        var subscribeCount =  $('input[name="subscribeCount"]').val();
        var amount =  $('input[name="amount"]').val();
        var url =  $('input[name="url"]').val();
        var remark =  $('input[name="remark"]').val();
        var contact =  $('input[name="contact"]').val();

        var status = $('input[name="status"]:checked').val();

        if(isEmpty(staffname) || isEmpty(name) || isEmpty(url) ||  isEmpty(status))
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return false;
        }


        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/root_web_promotion_channel/edit',
            type: 'post',
            dataType: 'json',
            data:{
                id:id,
                staffname:staffname,
                name:name,
                url:url,
                type:type,
                subscribeCount:subscribeCount,
                amount:amount,
                status:status,
                remark:remark,
                contact:contact,
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
