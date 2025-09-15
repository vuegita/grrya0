
<div class="form-group">
    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>钱包ID:</label>
    <div class="col-sm-5">
        <input class="form-control" name="walletAccountid" id="walletAccountid" autocomplete="off"
               maxlength="100" placeholder=""
               value="<#if paymentInfo?exists>${paymentInfo.accountid !}</#if>"/>
    </div>
</div>
<div class="hr-line-dashed"></div>


<div class="form-group" >
    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>钱包类型:</label>
    <div class="col-sm-5">

        <#-- INR -->
        <label><input type="radio" value="Paytm" name="walletType" id="walletType"
                      data-currency="INR"
                      <#if paymentInfo?exists>disabled</#if>
                    <#if paymentInfo?exists><#if paymentInfo.walletType! == 'Paytm'>checked</#if></#if>>
            <i></i>Paytm　</label>
        <br>

        <#-- COP -->
        <label><input type="radio" value="NEQUI" name="walletType" id="walletType"
                      data-currency="COP"
                      <#if paymentInfo?exists>disabled</#if>
                    <#if paymentInfo?exists><#if paymentInfo.walletType! == 'NEQUI'>checked</#if></#if>>
            <i></i>NEQUI　</label>

<#--        <label><input type="radio" value="TPAGA" name="walletType" id="walletType"-->
<#--                      data-currency="COP"-->
<#--                      <#if paymentInfo?exists>disabled</#if>-->
<#--                    <#if paymentInfo?exists><#if paymentInfo.walletType! == 'TPAGA'>checked</#if></#if>>-->
<#--            <i></i>TPAGA　</label>-->

<#--        <label><input type="radio" value="MovII" name="walletType" id="walletType"-->
<#--                      data-currency="COP"-->
<#--                      <#if paymentInfo?exists>disabled</#if>-->
<#--                    <#if paymentInfo?exists><#if paymentInfo.walletType! == 'MovII'>checked</#if></#if>>-->
<#--            <i></i>MovII　</label>-->

<#--        <label><input type="radio" value="Bancolombia" name="walletType" id="walletType"-->
<#--                      data-currency="COP"-->
<#--                      <#if paymentInfo?exists>disabled</#if>-->
<#--                    <#if paymentInfo?exists><#if paymentInfo.walletType! == 'Bancolombia'>checked</#if></#if>>-->
<#--            <i></i>Bancolombia a la mano　</label>-->

        <br>

    </div>
</div>
<div class="hr-line-dashed"></div>



