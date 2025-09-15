<div class="modal fade" id="myModalGoogle" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog"  role="document">
        <div class="modal-content" >



            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">密钥</h4>
            </div>
            <div class="modal-body"  >

            <form id="form" class="form-horizontal" autocomplete="off">
                <div id="unbindDiv">
                    <div class="form-group">
                        <label class="control-label col-sm-3">二维码:</label>
                        <img id="googleCodeImg" src="/alibaba888/agent/basic/security/getGoogleKeyEWM"/>
                    </div>
                    <div class="hr-line-dashed"></div>

<#--                    <div class="form-group">-->
<#--                        <label class="control-label col-sm-3">密钥:</label>-->
<#--                        <div class="col-sm-6">-->
<#--                            <input id="googleKey" readonly="readonly" class="form-control" name="googleKey" autocomplete="off" maxlength="255" value="${googleCode !}"/>-->
<#--                        </div>-->
<#--                    </div>-->
<#--                    <div class="hr-line-dashed"></div>-->
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>谷歌验证码:</label>
                    <div class="col-sm-6">
                        <input id="captcha" class="form-control" name="captcha" autocomplete="off" maxlength="255" />
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" id="btn_submit" class="btn btn-primary" data-dismiss="modal">
                        <span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span>确认
                    </button>
                </div>
            </form>
            </div>
        </div>
    </div>
</div>