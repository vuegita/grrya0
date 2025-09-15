<#--银行名称-->
<div class="form-group">
    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>BankName:</label>
    <div class="col-sm-3">
        <input class="form-control" name="bankName" id="bankName" autocomplete="off"
               maxlength="100" placeholder="银行名称"
               value="<#if paymentInfo?exists>${paymentInfo.bankName !}</#if>"/>
    </div>
</div>
<div class="hr-line-dashed"></div>

<#--银行编码-->
<div class="form-group">
    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>BankCode:</label>
    <div class="col-sm-3">
        <input class="form-control" name="bankCode" id="bankCode" autocomplete="off"
               maxlength="100" placeholder="银行编码, 印度为IFSC"
               value="<#if paymentInfo?exists>${paymentInfo.bankCode !}</#if>"/>
    </div>
</div>
<div class="hr-line-dashed"></div>

<#--银行账号-->
<div class="form-group">
    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>BankAccount:</label>
    <div class="col-sm-3">
        <input class="form-control" name="bankAccount" id="bankAccount" autocomplete="off"
               maxlength="100" placeholder="储蓄卡银行账号"
               value="<#if paymentInfo?exists>${paymentInfo.bankAccount !}</#if>"/>
    </div>
</div>
<div class="hr-line-dashed"></div>




