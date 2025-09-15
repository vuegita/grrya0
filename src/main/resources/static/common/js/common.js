

$(function(){
    $('.i-checks').iCheck({
        checkboxClass: 'icheckbox_square-green',
        radioClass: 'iradio_square-green'
    });
});

var TableUtils = TableUtils || {};
TableUtils.getBtDefaultOptions = function(){
    var options = {};
    options.method = 'post';
    options.dataType = 'json';
    options.contentType = 'application/x-www-form-urlencoded';
    options.sidePagination = 'server';
    options.search = true;
    options.pagination = true;
    options.showRefresh = true;
    options.showColumns = true;
    options.iconSize = 'outline';
    options.toolbar = '#bootstrapTableToolBar';
    options.icons = {
        refresh: 'glyphicon-repeat',
        columns: 'glyphicon-list'
    };
    options.responseHandler = function(res){
        return {
            total: res.data.total,
            rows: res.data.list
        }
    };

    return options;
};

TableUtils.dateFormatter = function(value, row, index) {
    return DateUtils.format(value);
};

String.prototype.format = function(){
    if(arguments.length==0) return this;
    for(var s=this, i=0; i<arguments.length; i++)
        s=s.replace(new RegExp("\\{"+i+"\\}","g"), arguments[i]);
    return s;
};

var DateUtils = DateUtils || {};

DateUtils.formatMMdd = function(timestamp){
    var date = new Date(timestamp);
    return paddingZero((date.getMonth() + 1)) + '月' + paddingZero(date.getDate()) + '日';
};

DateUtils.formatHHmm = function(timestamp){
    var date = new Date(timestamp);
    return paddingZero(date.getHours()) + ':' + paddingZero(date.getMinutes());
};

/**
 * 格式化时间 hh:mm:ss
 * @param timestamp
 * @returns {string}
 */
DateUtils.formatHHmmss = function(timestamp){
    var date = new Date(timestamp);
    return paddingZero(date.getHours()) + ':' + paddingZero(date.getMinutes()) + ':' + paddingZero(date.getSeconds());
};

/**
 * 格式化时间 yyyy.MM.dd
 * @param timestamp
 * @returns {string}
 */
DateUtils.formatYYYYMMDD = function(timestamp){
    var date = new Date(timestamp);
    return paddingZero(date.getFullYear()) + '.' + paddingZero((date.getMonth() + 1)) + '.' + paddingZero(date.getDate());
};

/**
 * 格式化时间 yyyy-MM-dd hh:mm:ss
 * @param timestamp
 * @returns {string}
 */
DateUtils.format = function(timestamp){
    var date = new Date(timestamp);
    return date.getFullYear() + '-' + paddingZero((date.getMonth() + 1)) + '-' + paddingZero(date.getDate()) + '  '
         + paddingZero(date.getHours()) + ':' + paddingZero(date.getMinutes()) + ':' + paddingZero(date.getSeconds());
};

DateUtils.formatyyyyMMddHHmm = function(timestamp){
    var date = new Date(timestamp);
    return date.getFullYear() + '-' + paddingZero((date.getMonth() + 1)) + '-' + paddingZero(date.getDate()) + '  '
        + paddingZero(date.getHours()) + ':' + paddingZero(date.getMinutes());
};
DateUtils.formatyyyyMMddHHmmss = function(timestamp){
    var date = new Date(timestamp);
    var timezone = 8;
    var offset_GMT = date.getTimezoneOffset();
    var nowDate = date.getTime();
    var targetDate = new Date(nowDate + offset_GMT * 60 * 1000 + timezone * 60 * 60 * 1000);
    return targetDate.getFullYear() + '-' + paddingZero((targetDate.getMonth() + 1)) + '-' + paddingZero(targetDate.getDate()) + '  '
        + paddingZero(targetDate.getHours()) + ':' + paddingZero(targetDate.getMinutes())+':'+paddingZero(targetDate.getSeconds());
};

DateUtils.getTodayDate = function(){
    var date = new Date();
    return paddingZero(date.getFullYear()) + '-' + paddingZero((date.getMonth() + 1)) + '-' + paddingZero(date.getDate());
};

/**
 * 小于10的数字前加0
 * @param number
 * @returns {*}
 */
function paddingZero(number){
    return (number < 10) ? ('0' + number) : number;
}

/**
 * 格式化金额， 如1000显示 1,000
 * @param x
 * @returns {string}
 */
function formateAmount(x) {
    //强制保留两位小数
    var f = parseFloat(x);
    if (isNaN(f)) return false;
    var f = Math.round(x * 100) / 100;
    var s = f.toString();
    var rs = s.indexOf('.');

    //每三位用一个逗号隔开
    var leftNum=s.split(".")[0];
    var rightNum = "";
    if(rs >= 0)
    {
        rightNum="."+s.split(".")[1];
    }

    var result;
    //定义数组记录截取后的价格
    var resultArray=new Array();
    if(leftNum.length>3){
        var i=true;
        while (i){
            resultArray.push(leftNum.slice(-3));
            leftNum=leftNum.slice(0,leftNum.length-3);
            if(leftNum.length<4){
                i=false;
            }
        }
        //由于从后向前截取，所以从最后一个开始遍历并存到一个新的数组，顺序调换
        var sortArray=new Array();
        for(var i=resultArray.length-1;i>=0;i--){
            sortArray.push(resultArray[i]);
        }
        result=leftNum+","+sortArray.join(",")+rightNum;
    }else {
        result=s;
    }
    return result;
}


function isEmpty(obj) {
    if (typeof obj === 'undefined' || obj == null || obj === '') {
        return true;
    } else {
        return false;
    }
}

function columnFormatterForOrderRemarkMsg(value, row, index) {

    try {
        var jsonObj = JSON.parse(row.remark);
        if(!isEmpty(jsonObj))
        {
            var msg = jsonObj.msg;
            if(!isEmpty(msg))
            {
                return msg;
            }
        }
    } catch (e) {
    }

    return '';
}

function columnFormatterForBoolvalue(value, row, index) {
    try {
        if(value == true || value == "1" || value == "true")
        {
            return "true";
        }
    } catch (e) {
    }
    return 'false';
}

function columnFormatterForMoney(value, row, index) {
    if(value)
    {
        return Number(value).toFixed(6);
    }
    return '0';
}

/**
 * user-type
 * @param value
 * @param row
 * @param index
 * @returns {string|*}
 */
function userTypeFormatter(value, row, index) {
    if(value == "agent")
    {
        return "代理";
    }
    else if(value == "staff")
    {
        return "员工";
    }
    else if(value == "member")
    {
        return "会员";
    }
    else if(value == "test")
    {
        return "测试";
    }
    else if(value == "robot")
    {
        return "机器人";
    }
    return value;
}


function openNewWindow(url, frameWidth, frameHeight)
{
    if(frameWidth == undefined)
    {
        // frameWidth = window.screen.width / 4 * 3;
        // frameWidth = window.screen.width / 4 * 3;
        if(window.screen.width >= 1200)
        {
            frameWidth = 1200;
        }
        else
        {
            frameWidth = 800;
        }
    }
    if(frameHeight == undefined)
    {
        // frameHeight = window.screen.height / 4 * 3;
        // frameHeight = window.screen.height / 4 * 3;
        if(window.screen.width >= 1200)
        {
            frameHeight = 800;
        }
        else
        {
            frameHeight = 600;
        }
    }

    var frameX = (window.screen.width - frameWidth) / 2;
    var frameY = (window.screen.height - frameHeight) / 2;

    var frameParams = "modal=yes, toolbar=no, menubar=no, scrollbars=no, resizable=no, location=no, status=no";
    frameParams += ",width=" + frameWidth;
    frameParams += ",height=" + frameHeight;
    frameParams += ",top=" + frameY;
    frameParams += ",left=" + frameX;

    window.open(url, "_blank", frameParams);
}


/**
 * 操作栏的格式化
 */
function cryptoAddressTokenholdingsFormatter(address, chainType) {
    var result = "";
    // var address = row.address;
    // var chainType = row.chainType;
    if("ERC-20" == chainType || "ETH" == chainType)
    {
        result += '<a href="https://cn.etherscan.com/tokenholdings?a=' + address + '" class="table-btn" target="_blank">' + address + '</a>';
    }
    else if("TRC-20" == chainType)
    {
        result += '<a href="https://tronscan.org/#/address/' + address + '" class="table-btn" target="_blank">' + address + '</a>';
    }
    else if("HT" == chainType)
    {
        result += '<a href="https://hecoinfo.com/tokenholdings?a=' + address + '"' + address + ' class="table-btn" target="_blank">' + address + '</a>';
    }
    else if("BSC" == chainType)
    {
        result += '<a href="https://bscscan.com/tokenholdings?a=' + address + '" class="table-btn" target="_blank">' + address + '</a>';
    }

    return result;
}