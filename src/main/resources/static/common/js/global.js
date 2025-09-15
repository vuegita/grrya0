

(function($, layer){

    var Global = function(){};
    var Constant = function(){};
    $.global = new Global();
    $.global.constant = new Constant();

    $.global.openSuccessMsg = function(msg, callback){
        layer.msg(msg, {icon: 1, time: 1000}, function() {
            if(callback && typeof callback == 'function'){
                callback();
            }
        });
    };

    $.global.openErrorMsg = function(msg){
        layer.alert(msg, {
            skin: 'layui-layer-red',
            shift: 0 ,
            icon:2
        });
    };

    $.global.openErrorMsgCollback = function(msg,callback){
        layer.alert(msg, {
            skin: 'layui-layer-red',
            shift: 0 ,
            icon:2
        },function () {
            if(callback && typeof callback == 'function'){
                callback();
            }
        });
    };
    /**
     * 表单通用提交
     * @param formId
     * @param url
     * @param formData
     */
    $.global.ajaxSubmitFormNotGo = function(formId, url, formData){
        var $form = $(formId);
        if(!formData){
            formData = $form.serialize();
        }
        var $submitBtn = $form.find('input[type="submit"]');
        //$submitBtn.attr('disabled', true);
        $.ajax({
            url: url,
            type: 'post',
            dataType: 'json',
            data: formData,
            beforeSend: function(){
                layer.load({shade: [0.1,'#fff']});
            },
            complete: function(){
                layer.closeAll('loading');
            },
            success: function(result){
                if(result.code == 200){
                    $.global.openSuccessMsg('操作成功', function(){
                        layer.closeAll();
                        //window.history.go(-1);
                    });
                    return;
                }
                $submitBtn.attr('disabled', false);
                $.global.openErrorMsg(result.msg);
            },
            error: function(){
                $submitBtn.attr('disabled', false);
                $.global.openErrorMsgCollback('系统异常,操作失败',function () {
                    window.location.href = "/alibaba888/toLogin";
                });
            }
        });
    };
    /**
     * 表单通用提交
     * @param formId
     * @param url
     * @param formData
     */
    $.global.ajaxSubmitForm = function(formId, url, formData){debugger;
        var $form = $(formId);
        if(!formData){
            formData = $form.serialize();
        }
        var $submitBtn = $form.find('input[type="submit"]');
        $submitBtn.attr('disabled', true);
        $.ajax({
            url: url,
            type: 'post',
            dataType: 'json',
            data: formData,
            beforeSend: function(){
                layer.load({shade: [0.1,'#fff']});
            },
            complete: function(){
                layer.closeAll('loading');
            },
            success: function(result){
                if(result.code == 200){
                    $.global.openSuccessMsg('保存成功', function(){
                        layer.closeAll();
                        window.history.go(-1);
                    });
                    return;
                }
                $submitBtn.attr('disabled', false);
                $.global.openErrorMsg(result.msg);
            },
            error: function(){
                $submitBtn.attr('disabled', false);
                $.global.openErrorMsgCollback('系统异常,操作失败',function () {
                    window.location.href = "/alibaba888/toLogin";
                });
            }
        });
    };

    $.global.ajaxPost = function(url, formData, successCallback){
        $.ajax({
            url: url,
            type: 'post',
            dataType: 'json',
            data: formData,
            beforeSend: function(){
                layer.load({shade: [0.1,'#fff']});
            },
            complete: function(){
                layer.closeAll('loading');
            },
            success: function(result){
                if(result.code == 200){
                    $.global.openSuccessMsg('操作成功', successCallback);
                    if(successCallback == 'refresh'){
                        refresh();
                    }
                    return;
                }
                $.global.openErrorMsg(result.msg);
            },
            error: function(){
                $.global.openErrorMsgCollback('系统异常,操作失败',function () {
                    window.location.href = "/alibaba888/toLogin";
                });
            }
        });
    };

    $.global.isImage = function(fileName){
        var array = new Array(".jpg",".jpeg", ".bmp", ".png", ".JPG",".BMP",".JPEG",".PNG", ".gif");
        var result = false;

        while (fileName.indexOf("\\") != -1){
            fileName = fileName.slice(fileName.indexOf("\\") + 1);
        }
        var ext = fileName.slice(fileName.lastIndexOf(".")).toLowerCase();
        for (var i = 0; i < array.length; i++) {
            if (array[i] == ext){
                result = true;
                break;
            }
        }
        return result;
    };

    $.global.uploadImage = function(url, fileName, uploadId, callback,finaly, isNotReplaceInput){
        if(!$.global.isImage(fileName)){
            $.global.openErrorMsg("图片格式不正确!");
            return;
        }
        $.ajaxFileUpload({
            url: url,
            secureuri: false,
            fileElementId: [uploadId],
            dataType:'json',
            success: function (data){
                var result = data.code;
                var msg = data.msg;
                if(0 == result) {
                    callback(data);
                    $.global.openSuccessMsg("图片上传成功");
                    if(finaly) finaly();
                    return;
                }
                $.global.openErrorMsg(msg);
                if(finaly) finaly();
            },
            complete: function() {
            	if(!isNotReplaceInput) $('#' + uploadId).replaceWith('<input type="file" id="'+ uploadId +'" name="file">');
            },
            error: function (data, status, e){
                console.log(e)
                $.global.openErrorMsg("服务器异常，图片上传失败！");
                if(finaly) finaly();
            }
        });
    };

    $.global.constant.getUserStatusHtml = function(status){
        if(status=== "enable"){
            return '启用';
        }else {
            return '<span style="color: blue">禁用</span>';
        }
    };

    $.global.constant.getStatusHtml = function(status){
        if(status==="1"){
            return '<span style="color: green">开启</span>';
        }else {
            return '<span style="color: red">关闭</span>';
        }
    };

    $.global.constant.getOrderStatusMsg = function(status){
        if(status==="new"){
            return "待处理";
        }
        if(status==="success"){
            return "处理成功";
        }
        if(status==="waiting"){
            return "待审核";
        }
        if(status==="error"){
            return "失败";
        }
        if(status==="refunding"){
            return "退款中";
        }
        if(status==="refund_success"){
            return "退款成功";
        }
        if(status==="realized"){
            return "完成交易";
        }
        if(status==="discard"){
            return "废弃";
        }
        if(status==="pending"){
            return "已提交";
        }
        return status;
    };

    $.global.constant.PayType = {
        payin:{
            name:"代收",
            code:"payin"
        },
        payout:{
            name:"代付",
            code:"payout"
        }
    };

    $.global.constant.getPayTypeMsg = function(type){
        for(var key in $.global.constant.PayType){
            if(type === $.global.constant.PayType[key].code){
                return $.global.constant.PayType[key].name;
            }
        }
        return type;
    };

    $.global.constant.getOrderTypeValue = function(orderType){
        if(orderType==="user_recharge"){
            return "用户充值";
        }
        if(orderType==="user_withdraw"){
            return "用户提现";
        }
        if(orderType==="platform_recharge"){
            return "平台充值";
        }
        if(orderType==="platform_deduct"){
            return "平台扣款";
        }
        if(orderType==="platform_presentation"){
            return "平台赠送";
        }
        if(orderType==="business_recharge"){
            return "业务充值";
        }
        if(orderType==="business_deduct"){
            return "业务扣款";
        }
        if(orderType==="refund"){
            return "退款";
        }
        if(orderType==="finance_recharge"){
            return "理财充值";
        }
        if(orderType==="finance_deduct"){
            return "理财扣款";
        }

        return orderType;
    };

    $.global.constant.getBusinessTypeValue = function(orderType){
        if(orderType==="user_recharge"){
            return "用户充值";
        }
        if(orderType==="user_withdraw"){
            return "用户提现";
        }
        if(orderType==="platform_recharge"){
            return "平台充值";
        }
        if(orderType==="platform_presentation"){
            return "平台赠送";
        }
        if(orderType==="platform_deduct"){
            return "平台扣款";
        }
        if(orderType === "first_recarge_presentation")
        {
            return "首充赠送";
        }
        if(orderType==="register_presentation"){
            return "注册赠送";
        }

        return orderType;
    };

    $.global.constant.OrderType = {
        recharge:{
            name:"充值",
            code:"recharge"
        },
        bet:{
            name:"提现",
            code:"withdraw"
        },
        recharge_refund:{
            name:"充值退款",
            code:"recharge_refund"
        },
        platform_deduct:{
            name:"平台扣款",
            code:"platform_deduct"
        },
        platform_recharge:{
            name:"平台充值",
            code:"platform_recharge"
        }
    };
    $.global.constant.getOrderTypeMsg = function(type){
        for(var key in $.global.constant.OrderType){
            if(type === $.global.constant.OrderType[key].code){
                return $.global.constant.OrderType[key].name;
            }
        }
        return type;
    };

    $.global.constant.UserType = {
        platform:{
            name:"平台商户",
            code:"platform"
        },
        private:{
            name:"私有商户",
            code:"private"
        },
    };

    $.global.constant.getSettleMode = function(mode){
        if(mode == undefined || mode==="" || mode==="T0"){
            return "T0-人工结算";
        }
        if(mode==="T1"){
            return "T1-自动结算";
        }
        if(mode==="T2"){
            return "T2-自动结算";
        }
        return mode;
    };

    $.global.constant.getUserTypeMsg = function(type){
        for(var key in $.global.constant.UserType){
            if(type === $.global.constant.UserType[key].code){
                return $.global.constant.UserType[key].name;
            }
        }
        return type;
    };


    $.global.constant.getUserTypeHtml = function(type, value){
        if(type === $.global.constant.UserType.platform.code){
            return '<span style="color: blue">' + value + '</span>';
        }
        if(type === $.global.constant.UserType.private.code){
            return '<span style="color: red">' + value + '</span>';
        }



        return type;
    };

    return $;


})(jQuery, layer);

$(function(){
   $('form input[type="text"]').attr('autocomplete', 'off');
});