<#--银行名称-->
<div class="form-group">
    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>UPI:</label>
    <div class="col-sm-3">
        <input class="form-control" name="indiaUPIAddress" id="indiaUPIAddress" autocomplete="off"
               maxlength="100" placeholder="UPI地址"
               value="<#if paymentInfo?exists>${paymentInfo.upi !}</#if>"/>
    </div>
</div>
<div class="hr-line-dashed"></div>





