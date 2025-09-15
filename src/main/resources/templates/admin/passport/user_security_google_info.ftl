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
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>用户名:</label>
                        <div class="col-sm-6">
                            <input type="text" class="form-control" id="googleCodeUsername" name="googleCodeUsername" autocomplete="off" maxlength="255"
                                   readonly
                                   value=""/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group">
                        <label class="control-label col-sm-2"><span class="text-danger">*&nbsp;</span>谷歌验证码:</label>
                        <div class="col-sm-6">
                            <input type="text" class="form-control" id="googleCodeValue" name="googleCodeValue" autocomplete="off" maxlength="255"
                                   value=""/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group">
                        <label class="control-label col-sm-3">二维码:</label>
                        <div class="col-sm-6">
                            <img id="googleCodeImg" style="width: 200px; height: 200px" src=""/>
                        </div>
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


                <div class="modal-footer">
                    <button class="btn btn-white" type="button" data-dismiss="modal">取消</button>

<#--                    <button type="button" id="btn_submit" class="btn btn-primary" data-dismiss="modal">-->
<#--                        <span class="glyphicon glyphicon-floppy-disk" aria-hidden="true"></span>确认-->
<#--                    </button>-->

                    <button type="button" id="btn_submit" class="btn btn-primary" onclick="refreshGoogleCode()">确定
                    </button>
                </div>
            </form>
            </div>
        </div>
    </div>
</div>

<script>

    function refreshGoogleCode()
    {
        var username = $('#googleCodeUsername').val();
        var googleCodeValue = $('#googleCodeValue').val();
        var imgSrc = "/alibaba888/Liv2sky3soLa93vEr62/passport/getGoogleKeyEWM?username=" + username + "&googleCode=" + googleCodeValue + "&time=" + Math.random();;
        $('#googleCodeImg').attr("src", imgSrc);
    }

    function showGoogleCode(username)
    {
        $('#googleCodeUsername').val(username);
        $('#myModalGoogle').modal();
    }

</script>