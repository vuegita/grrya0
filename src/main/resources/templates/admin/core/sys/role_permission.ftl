<!DOCTYPE HTML>
<html>
<head>
    <#include "../../../common/head-meta.ftl"/>
    <title>${projectName}后台管理系统-系统管理-编辑角色</title>
    <style type="text/css">
        .hr-line-dashed{
            margin: 10px 0;
        }
    </style>
</head>

<body class="gray-bg">
    <div class="wrapper wrapper-content animated fadeInRight">
        <div class="ibox float-e-margins">
            <div class="ibox-title">
                <h5>系统管理-编辑角色</h5>
            </div>

            <div class="ibox-content">
                <form id="form" class="form-horizontal" autocomplete="off">
                    <div class="form-group">
                        <label class="control-label col-sm-2">角色名称</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="roleName" value="${role.name}" readonly autocomplete="off" required maxlength="20"/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <#list menuList as menu>
                    <div class="form-group">
                        <label class="control-label col-sm-2">${menu.name}</label>
                        <div class="col-sm-5">

                            <#list menu.getChildList() as childMenu>
                            <div class="menu-list">
                                <div class="checkbox i-checks parent-menu">
                                    <label>
                                        <input type="checkbox" <#if childMenu.isCheckedAllPermission() >checked</#if> disable>
                                        <i></i> ${childMenu.name}
                                    </label>
                                </div>

                                <div class="checkbox i-checks child-menu">
                                    <#list childMenu.getChildMenuPermissionList() as permission >
                                        <label>
                                            <input type="checkbox" name="${permission.key}" <#if permission.checked> checked </#if>>
                                            <i></i> ${permission.name}
                                        </label>
                                    </#list>
                                </div>
                            </div>
                            <div class="hr-line-dashed"></div>
                            </#list>
                        </div>
                    </div>
                    </#list>


                    <#--<div class="form-group">
                        <label class="control-label col-sm-2">状态</label>
                        <div class="col-sm-3">
                            <select class="form-control m-b" name="state">
                                <option value="0" >禁用</option>
                                <option value="1" >正常</option>
                            </select>
                        </div>
                    </div>-->
                    <div class="form-group">
                        <div class="col-sm-4 col-sm-offset-2">
                            <input class="btn btn-primary" type="submit" value="保存"/>
                            <button class="btn btn-white" type="button" onclick="window.history.back();">取消</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script type="text/javascript" src="${STATIC_URL}/plugins/validate/jquery.validate.min.js"></script>
    <script type="text/javascript" src="${STATIC_URL}/plugins/validate/messages_zh.min.js"></script>
    <script type="text/javascript" src="${STATIC_URL}/js/validate.defaults.js?v=${version}"></script>
    <script type="text/javascript">

        $(function(){

            $('input[type="checkbox"]').on('ifClicked', function(){
                var $ichecks = $(this).parents('.i-checks');
                var checked = $(this).is(':checked');
                if($ichecks.hasClass('parent-menu')){
                    var $childMenu = $ichecks.parent().find('.child-menu').find('input[type="checkbox"]');
                    if(checked){
                        $childMenu.iCheck('uncheck');
                    }else{
                        $childMenu.iCheck('check');
                    }
                }else if($ichecks.hasClass('child-menu')){
                    var $parentMenu = $ichecks.parent().find('.parent-menu').find('input[type="checkbox"]');
                    if(checked){
                        var checkedCount = 0;
                        var $siblingMenu = $ichecks.find('input[type="checkbox"]');
                        $siblingMenu.each(function () {
                            if($(this).is(':checked')){
                                checkedCount++;
                            }
                        });
                        if(checkedCount == 1){
                            $parentMenu.iCheck('uncheck');
                        }
                    }else{
                        $parentMenu.iCheck('check');
                    }
                }
            });

            var formId = '#form';
            var newUrl = '/alibaba888/Liv2sky3soLa93vEr62/updateRolePermissions';
            var validator = $(formId).validate({
                rules: {
                },
                messages: {
                },
                submitHandler:function(form){
                    var permissiontKeyList = new Array();
                    $('input[type="checkbox"]:checked').each(function(index){
                        var key = $(this).attr("name");
                        if(isEmpty(key))
                        {
                            return;
                        }
                        permissiontKeyList.push(key);
                    });
                    console.log(permissiontKeyList)
                    if(permissiontKeyList.length <= 0){
                        $.global.openErrorMsg('请选择角色权限!');
                        return true;
                    }
                    var formData = {};
                    formData.roleName = $('input[name="roleName"]').val();
                    formData.permissiontKeyList = permissiontKeyList.join(",");

                    console.log("fromdaa = " + formData)

                    $.global.ajaxSubmitForm(formId, newUrl, formData);
                    return false;
                }
            });


        });

        function test() {
            var formId = '#form';
            var newUrl = '/alibaba888/role/editPermission';
            var validateUrl = '/alibaba888/role/validateName';
            var validator = $(formId).validate({
                rules: {
                    name: {
                        remote: {
                            url: validateUrl, type: "post", dataType: "json",
                            data: {
                                id: function() {
                                    return $('input[name="id"]').val();
                                },
                                name: function() {
                                    return $('input[name="name"]').val();
                                }
                            }
                        }
                    }
                },
                messages: {
                    name: {
                        remote: '<i class="fa fa-times-circle"></i> 该角色已存在，请重新输入'
                    }
                },
                submitHandler:function(form){
                    var menuList = new Array();
                    $('input[type="checkbox"]:checked').each(function(index){
                        menuList.push($(this).val());
                    });
                    console.log(menuList)
                    if(menuList.length <= 0){
                        $.global.openErrorMsg('请选择角色权限!');
                        return true;
                    }
                    var formData = {};
                    formData.roleName = $('input[name="roleName"]').val();
                    formData.menuList = menuList;
                    $.global.ajaxSubmitForm(formId, newUrl, formData);
                    return false;
                }
            });
        }

    </script>
</body>
</html>
