<!DOCTYPE HTML>
<html>
<head>
</head>
<body>
<#--左侧导航开始-->
<nav class="navbar-default navbar-static-side" role="navigation">
    <div class="nav-close"><i class="fa fa-times-circle"></i>
    </div>
    <div class="sidebar-collapse">
        <ul class="nav" id="side-menu">
            <li class="nav-header">
                <div class="dropdown profile-element">
                    <div style="display:flex;align-items: center;">
<#--                        <div><img alt="image" class="img-circle" src="${STATIC_SERVER}/images/${ENV}head.png" width="75"/>-->
<#--                        </div>-->

                    </div>
                    <a data-toggle="dropdown" class="dropdown-toggle" href="#" style="text-align:center;">
                                    <span class="clear" style="margin-top:5px;">
                                    <span class="block m-t-xs"><strong
                                            class="font-bold">${admin.account!}</strong></span>
                                    <span class="text-muted text-xs block">${admin.rolename!}<b class="caret"></b></span>
                                    </span>
                    </a>
                    <ul class="dropdown-menu animated fadeInRight m-t-xs">
                        <li class="divider"></li>
                        <li><a href="/alibaba888/Liv2sky3soLa93vEr62/logout">安全退出</a>
                        </li>
                    </ul>
                </div>
                <div class="logo-element">C</div>
            </li>
        <#if menuList?exists>
            <#list menuList as menu >
                <#if menu.childList ? exists >
                    <li>
                        <a href="#">
                            <i class="fa ${menu.icon}"></i>
                            <span class="nav-label">${menu.name}</span>
                            <span class="fa arrow"></span>
                        </a>
                        <ul class="nav nav-second-level">
                            <#list menu.childList as childMenu >
                                <li>
                                    <a class="J_menuItem" href="${childMenu.link}" data-index="0">${childMenu.name}</a>
<#--                                    <#if childMenu.safeStatus == 'safe'>-->
<#--                                    <a class="J_menuItem" href="/alibaba888/google/validate/page?jumpFrom=${childMenu.id}" data-index="0">${childMenu.name}</a>-->
<#--                                    <#else >-->
<#--                                    <a class="J_menuItem" href="${childMenu.url}" data-index="0">${childMenu.name}</a>-->
<#--                                    </#if>-->
                                </li>
                            </#list>
                        </ul>

                    </li>
                </#if>
            </#list>
        </#if>
        </ul>
    </div>
</nav>
<#--  左侧导航结束-->
<script>



</script>
</body>
</html>
