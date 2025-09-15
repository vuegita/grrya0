<div class="modal fade" id="myModalLoginConfig" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog"  role="document">
        <div class="modal-content" >
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">登录设置</h4>
            </div>
            <div class="modal-body"  >

                <form id="form" class="form-horizontal" autocomplete="off">
                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>验证方式:</label>
                    <div class="col-sm-6">
                            <label><input type="radio" name="loginType" value="login_pwd" <#if  "login_pwd" == loginType>checked</#if>> <i></i>仅登录密码验证</label>
                            <br><label><input type="radio" name="loginType" value="google_key" <#if  "google_key" == loginType>checked</#if>> <i></i>登录密码+谷歌组合验证(需绑定谷歌验证)</label>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>谷歌验证码:</label>
                    <div class="col-sm-6">
                        <input class="form-control" name="captcha" id="captcha" autocomplete="off" required
                               placeholder="请输入谷歌验证码"
                               value="" />
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal"><span
                            class="glyphicon glyphicon-confirm" aria-hidden="true"></span>取消
                </button>
                <button type="button" id="btn_submit" class="btn btn-primary" data-dismiss="modal">
                    <span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span>确认
                </button>
            </div>
        </div>
    </div>
</div>