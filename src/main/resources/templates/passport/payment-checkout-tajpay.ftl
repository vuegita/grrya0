<html>
<body>
<FORM NAME='from' ACTION='${action}' METHOD='POST'>
    <#list payin_params?keys as key>
        <input type="hidden" name="${key}" value="${payin_params[key]!}" maxlength="300" size="300" /> <br>
    </#list>
</FORM>
</body>
<script type="text/javascript">  document.forms[0].submit();</script>
</html>