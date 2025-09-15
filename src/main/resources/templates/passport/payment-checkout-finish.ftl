<html>
<body>
<h1>${recharge_result!} !</h1> <br>

<h1> <a href="/h5/#/mine/mine" id="link">3 seconds will redirect ...</a></h1> <br>
<script>

    var countdwonSeconds = 3;
    setInterval(function () {
        countdwonSeconds --;
        if(countdwonSeconds <= 0)
        {
            window.location.href = "/h5/#/mine/mine";
        }
        else
        {
            var text = countdwonSeconds + " seconds will redirect ...";
            document.getElementById("link").innerText = text;
        }
    }, 1000);

</script>
</body>
</html>