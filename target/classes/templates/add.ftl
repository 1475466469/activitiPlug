<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title></title>

    <link rel="stylesheet" href="/static/layui/css/layui.css">


    <script src="http://lib.sinaapp.com/js/jquery/1.7.2/jquery.min.js"></script>
</head>
<style>

    .text-center{
        margin:auto;
        text-align: center;

    }

</style>

<body>
<form class="layui-form" id="roleForm" lay-filter="roleForm">
    <div class="layui-fluid" style="padding-bottom: 75px;">
        <div class="layui-card">
            <div class="layui-card-header">基本信息</div>
            <div class="layui-card-body">
                <div class="layui-form-item layui-row">
                    <input name="roleId" type="hidden"/>
                    <div class="layui-inline layui-col-md12">
                        <label class="layui-form-label">流程名称<span style="color: red;">*</span></label>
                        <div class="layui-input-block">
                            <input name="name" placeholder="请输入流程名称" type="text" id="name" class="layui-input" lay-verify="required" required/>
                        </div>
                    </div>

                    <div class="layui-inline layui-col-md12">
                        <label class="layui-form-label">流程编码<span style="color: red;">*</span></label>
                        <div class="layui-input-block">
                            <input id="key" name="key" placeholder="请输入流程编码" type="text" class="layui-input" lay-verify="required" required autocomplete="off"/>
                        </div>
                    </div>


                </div>
            </div>
        </div>
    </div>
    <div class="form-group-bottom text-center">
        <button type="button" class="layui-btn" lay-filter="btnSubmit" id="btn">&emsp;提交&emsp;</button>
        <button type="reset" class="layui-btn layui-btn-primary" ew-event="closeDialog">&emsp;取消&emsp;</button>
    </div>

</form>

</body>
</html>
<script>
    $("#btn").click(function () {
        if($("#name").val()!=""& $("#key").val()!=""){
            window.location="/create?name="+$("#name").val()+"&key="+$("#key").val()
        }



    })



</script>


