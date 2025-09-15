
<div class="form-group">
    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>账户地址:</label>
    <div class="col-sm-3">
        <input class="form-control" name="coinAccountAddress" id="coinAccountAddress" autocomplete="off"
               value="<#if paymentInfo?exists>${paymentInfo.accountAddress !}</#if>"/>
    </div>
</div>
<div class="hr-line-dashed"></div>

<div class="form-group">
    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>账户私钥:</label>
    <div class="col-sm-3">
        <input class="form-control" name="coinAccountPrivateKey" id="coinAccountPrivateKey" autocomplete="off"
               placeholder="如有更新请输入"
<#--               value="<#if paymentInfo?exists>${paymentInfo.accountPrivateKey !}</#if>"-->
                value=""
        />
    </div>

</div>
<div class="hr-line-dashed"></div>

<div class="form-group">
    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>所属代理:</label>
    <div class="col-sm-3">
        <input class="form-control" name="coinAgentname" id="coinAgentname" autocomplete="off"
               value="<#if paymentInfo?exists>${paymentInfo.agentname !}</#if>"/>
    </div>
</div>
<div class="hr-line-dashed"></div>

<#--<div class="form-group">-->
<#--    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>币种类型:</label>-->
<#--    <div class="col-sm-3">-->
<#--        <select class="form-control" name="coinCurrencyType" >-->

<#--            <#if paymentInfo?exists>-->
<#--                <option value="${paymentInfo.currencyType}" > ${paymentInfo.currencyType}</option>-->
<#--            <#else>-->
<#--                <option value=""> 请选择币种 </option>-->
<#--                <#list currencyTypeList as item>-->
<#--                    <#if item.getCategory() == "Crypto">-->
<#--                        <option value="${item.getKey()}"> ${item.getKey()}</option>-->
<#--                    </#if>-->
<#--                </#list>-->
<#--            </#if>-->

<#--        </select>-->
<#--    </div>-->
<#--</div>-->
<#--<div class="hr-line-dashed"></div>-->

<div class="form-group" >
    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>网络类型:</label>
    <div class="col-sm-5">

        <#list networkTypeArr as item>
            <label><input type="radio" value="${item.getKey()}"
                          name="coinNetworkType" id="coinNetworkType"
                          <#if paymentInfo?exists>disabled</#if>
                        <#if paymentInfo?exists><#if paymentInfo.networkType! == '${item.getKey()}'>checked</#if></#if>>
                <i></i>${item.getKey()}　</label>
        </#list>

    </div>
</div>
<div class="hr-line-dashed"></div>


<div class="form-group">
    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>支持币种:</label>
    <div class="col-sm-5">
        <div class="radio i-checks">
            <br>

            <#assign myCurrencyTypeMaps={}/>
            <#if paymentInfo?exists>
                <#assign myCurrencyTypeArr=paymentInfo.currencyTypeArr!?split(",")>
                <#list myCurrencyTypeArr as item>
                    <#assign myCurrencyTypeMaps += {"${item}" : "1"}/>
                </#list>
            </#if>

            <#list currencyTypeList as item>
                <#if item.getCategory() == "Crypto">
                    <label><input type="checkbox"  name="currencyType"  value="${item.getKey()}"
                            <#if myCurrencyTypeMaps[item.getKey()]! == '1'> checked </#if>
                        />
                        <i></i>${item.getKey()}
                    </label>
                </#if>
            </#list>

        </div>

    </div>
</div>

<div class="form-group">
    <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>gasLimit:</label>
    <div class="col-sm-3">
        <input class="form-control" name="coinGasLimit" id="coinGasLimit" autocomplete="off"
               value="<#if paymentInfo?exists>${paymentInfo.gasLimit !}</#if>"/>
    </div>
</div>

<div class="form-group" style="margin-left: 200px">
    <span class="text-danger">网络类型选 ETH (Mainnet)时gasLimit填 100000</span><br>
    <span class="text-danger">网络类型选 BNB (Mainnet)时gasLimit填 100000</span><br>
    <span class="text-danger">网络类型选 MATIC (Polygon)时gasLimit填 150000</span><br>
    <span class="text-danger">网络类型选 TRX (TronGrid)时gasLimit填 40000000</span><br>
</div>
<div class="hr-line-dashed"></div>