<div class="form-group">
    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>key:</label>
    <div class="col-sm-3">
        <input class="form-control" name="key" id="key" autocomplete="off"
               maxlength="100" <#if isShowAction != 'true'> readonly</#if>
               value="<#if paymentInfo?exists>${paymentInfo.key !}</#if>"/>
    </div>
</div>
<div class="hr-line-dashed"></div>

<div class="form-group">
    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>salt:</label>
    <div class="col-sm-3">
        <input class="form-control" name="salt" id="salt" autocomplete="off"
               maxlength="100" <#if isShowAction != 'true'> readonly</#if>
               value="<#if paymentInfo?exists>${paymentInfo.salt !}</#if>"/>
    </div>
</div>
<div class="hr-line-dashed"></div>

<div class="form-group <#if isShowAction != 'true'> hidden</#if>" >
    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>Target:</label>
    <div class="col-sm-5">

        <label><input type="radio" value="Topay" name="targetType" id="targetType"
                      data-currency="INR"
                      <#if paymentInfo?exists>disabled</#if>
                    <#if paymentInfo?exists><#if paymentInfo.targetType! == 'Topay'>checked</#if></#if>>
            <i></i>Topay　</label>

<#--        <label><input type="radio" value="Minepay" name="targetType" id="targetType"-->
<#--                      data-currency="INR"-->
<#--                      <#if paymentInfo?exists>disabled</#if>-->
<#--                    <#if paymentInfo?exists><#if paymentInfo.targetType! == 'Minepay'>checked</#if></#if>>-->
<#--            <i></i>Minepay　</label>-->

        <label><input type="radio" value="Tajpay" name="targetType" id="targetType"
                      data-currency="INR"
                      <#if paymentInfo?exists>disabled</#if>
                    <#if paymentInfo?exists><#if paymentInfo.targetType! == 'Tajpay'>checked</#if></#if>>
            <i></i>Tajpay　</label>

<#--        <label><input type="radio" value="Payhub" name="targetType" id="targetType"-->
<#--                      data-currency="IDR"-->
<#--                      <#if paymentInfo?exists>disabled</#if>-->
<#--                    <#if paymentInfo?exists><#if paymentInfo.targetType! == 'Payhub'>checked</#if></#if>>-->
<#--            <i></i>Payhub　</label>-->

<#--        <label><input type="radio" value="Likepay" name="targetType" id="targetType"-->
<#--                      data-currency="IDR"-->
<#--                      <#if paymentInfo?exists>disabled</#if>-->
<#--                    <#if paymentInfo?exists><#if paymentInfo.targetType! == 'Likepay'>checked</#if></#if>>-->
<#--            <i></i>Likepay　</label>-->

<#--        <label><input type="radio" value="Copay" name="targetType" id="targetType"-->
<#--                      data-currency="IDR"-->
<#--                      <#if paymentInfo?exists>disabled</#if>-->
<#--                    <#if paymentInfo?exists><#if paymentInfo.targetType! == 'Copay'>checked</#if></#if>>-->
<#--            <i></i>Copay　</label>-->


    </div>
</div>
<div class="hr-line-dashed"></div>



