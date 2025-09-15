
<div class="modal fade" id="batchpresetOpenResultModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">批量生成开奖结果</h4>
            </div>
            <div class="modal-body form-horizontal" >


                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>游戏类型 :</label>
                    <div class="col-sm-3">
                            <select class="form-control" name="gameType" id="gameType" >
                                <#list gameList as game>
                                    <option value="${game.key}" >${game.getShowLotteryType()}</option>
                                </#list>
                            </select>
                    </div>
                </div>
                <div class="hr-line-dashed"></div>

<#--                <div class="form-group">-->
<#--                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>期数时间范围 :</label>-->
<#--                    <div class="col-sm-3">-->
<#--                        <input type="text" name="issuetime" id="issuetime" class="form-control input-outline"  placeholder="请选择时间" style="width:350px;">-->
<#--                    </div>-->
<#--                </div>-->
                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>开始时间 :</label>
                    <div class="col-sm-3">
                        <input type="text" name="startTime" id="startTime" class="form-control input-outline"  placeholder="开始时间" style="width:175px;">
                    </div>
                </div>

                <div class="form-group">
                    <label class="control-label col-sm-3"><span class="text-danger">*&nbsp;</span>结束时间 :</label>
                    <div class="col-sm-3">
                        <input type="text" name="endTime" id="endTime" class="form-control input-outline"  placeholder="结束时间" style="width:175px;">
                    </div>
                </div>


                <div class="hr-line-dashed"></div>


            </div>
            <div class="modal-footer">

                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" onClick="batchResetLotteryOpenResult()">提交</button>


            </div>
        </div>
    </div>
</div>

<script>
    //时间选择器
    laydate.render({
        elem: '#startTime'
        ,type: 'datetime'
    });
    laydate.render({
        elem: '#endTime'
        ,type: 'datetime'
    });

    // layTime = laydate.render({
    //     elem: '#issuetime',
    //     type: 'datetime',
    //     range: true,
    //     format:'yyyy-MM-dd HH:mm:ss',
    //    // start: laydate.now(0,"YYYY-MM-dd hh:mm:ss"),  //设置开始时间为当前时间
    //     max:0,
    //     change:function(value, date, endDate){
    //
    //         var s = new Date(date.year+'-'+date.month+'-'+date.date);
    //         var e = new Date(endDate.year+'-'+endDate.month+'-'+endDate.date);
    //         //计算两个时间间隔天数
    //         var day=(e-s)/(1000*60*60*24);
    //         //console.log(date.year+'-'+date.month+'-'+date.date);
    //         //console.log(endDate.year+'-'+endDate.month+'-'+endDate.date);
    //         //console.log(day);
    //         if(day>1){
    //             layTime.hint('最多选择1天');
    //         }
    //     }
    // });

    function batchResetLotteryOpenResult() {
        // if (isEmpty($('input[name="issuetime"]').val())){
        //     $.global.openErrorMsg('请选择时间范围');
        //     return;
        // }
        if (isEmpty($('input[name="startTime"]').val())){
            $.global.openErrorMsg('请选择开始时间');
            return;
        }
        if (isEmpty($('input[name="endTime"]').val())){
            $.global.openErrorMsg('请选择结束时间');
            return;
        }

        // var issuetime = $('#issuetime').val();

        var startTime = $('#startTime').val();
        var endTime = $('#endTime').val();
        var gameType = $('select[name="type"]').val();

        if( isEmpty(startTime) || isEmpty(endTime)|| isEmpty(gameType))  //isEmpty(issuetime)||
        {
            $.global.openErrorMsg('* 号必填参数不能为空');
            return;
        }

        $.ajax({
            url: '/alibaba888/Liv2sky3soLa93vEr62/${moduleRelateUrl}/batchPresetOpenResult',
            type: 'post',
            dataType: 'json',
            data: {
                // issuetime:issuetime,
                gameType:gameType,
                startTime:startTime,
                endTime:endTime,
                moduleLotteryType: '${moduleLotteryType}'
            },
            success: function (result) {
                if (result.code === 200) {
                    $.global.openSuccessMsg(result.msg, function(){
                        //refresh();
                        $('#batchpresetOpenResultModal').modal('hide');
                    });
                    return;
                }
                $.global.openErrorMsg(result.msg);
            },
            error: function () {
                $.global.openErrorMsgCollback('系统异常,操作失败!', function () {

                });
            }
        });
    }

    function loadBatchPresetOpenResult() {//issue
        $('#batchpresetOpenResultModal').modal();
    }


</script>