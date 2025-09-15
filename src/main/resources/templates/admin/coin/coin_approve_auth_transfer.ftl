
<div class="modal fade" id="myTransferFormModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">立即转出</h4>
            </div>

            <form id="transferModelForm" class="form-horizontal" autocomplete="off">

                <div class="modal-body form-horizontal" >

                    <input type="hidden" name="transferFormId" id="transferFormId" autocomplete="off" maxlength="100"  style="width:300px;"/>

                    <div class="form-group">
                        <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>用户名:</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="transferFromUsername" id="transferFromUsername" readonly autocomplete="off" maxlength="100"  style="width:300px;"/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>


                    <div class="form-group">
                        <label class="control-label col-sm-3">转出地址:</label>
                        <div class="col-sm-3">
                            <input type="text" class="form-control" minlength="6" name="transferFormAddress" id="transferFormAddress" autocomplete="off"
                                   maxlength="50" placeholder="" readonly style="width:300px;"/>
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group">
                        <label class="control-label col-sm-3">余额:</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="transferFormBalance" id="transferFormBalance" autocomplete="off"
                                   required maxlength="50" type="text" readonly style="width:300px;"
                                   value="" />
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group">
                        <label class="control-label col-sm-3">转出数量:</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="transferFormAmount" id="transferFormAmount" autocomplete="off"
                                   required type="text" maxlength="50" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                   style="width:300px;"
                                   value="" />
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>

                    <div class="form-group">
                        <label class="control-label col-sm-3">谷歌验证码:</label>
                        <div class="col-sm-3">
                            <input class="form-control" name="transferFormGoogleCode" id="transferFormGoogleCode" autocomplete="off"
                                   required type="text" maxlength="50" onkeyup="this.value=this.value.replace(/[^\d\.]/g,'');"
                                   style="width:300px;"
                                   value="" />
                        </div>
                    </div>
                    <div class="hr-line-dashed"></div>



                    <div class="form-group">
                        <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>强制划转:</label>
                        <div class="col-sm-5">
                            <div class="radio i-checks">
                                <label>
                                    <input type="radio"  name="transferFromForceTransfer" id="transferFromForceTransfer" value="1"/>
                                    <i></i>启用
                                </label>

                                <label><input type="radio"  name="transferFromForceTransfer" id="transferFromForceTransfer" value="0" checked/>
                                    <i></i>禁用
                                </label>

                            </div>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="control-label" style="color: red;margin-left: 100px">1. 强制划转: 仅当后台显示余额为0，链上数据不为0时才开启！！！</label><br>
                        <label class="control-label" style="color: red;margin-left: 100px">2. 强制划转: 正常有余额时请勿开启！！！</label><br>
                    </div>
                    <div class="hr-line-dashed"></div>

                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button type="button" class="btn btn-primary" onClick="updateTransferFormInfo()">提交</button>
                </div>

            </form>
        </div>
    </div>
</div>

<script>

    function showTransferFormModal(id, username, address, balance) {
        $('#transferFormId').prop('value', id);
        $('#transferFromUsername').prop('value', username);
        $('#transferFormAddress').prop('value', address);
        $('#transferFormBalance').prop('value', balance);
        $('#transferFormAmount').prop('value', balance);

        $('#myTransferFormModal').modal();
    }

    function updateTransferFormInfo(item) {
        var data = $('#transferModelForm').serialize();
        $('#myTransferFormModal').modal('hide');
        $.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: "/alibaba888/Liv2sky3soLa93vEr62/doCoinTransferBalance" ,//url
            data: data,
            success: function (data) {
                if (data != null && data.code == 200) {
                    $.global.openSuccessMsg("保存成功");
                    //$('#myTransferFormModal').modal('hide');
                } else {
                    $.global.openErrorMsg(data.msg);
                }
            },
            error : function() {
                $.global.openErrorMsg('保存失败，请重试');
            }
        });
    }


</script>