<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"/>
<title>Activiti6流程设计器Demo</title>

	<link rel="stylesheet" href="/static/layui/css/layui.css">
</head>


<body>
<h2>


</h2>

<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
	<legend>流程列表</legend>
</fieldset>
<button type="button" class="layui-btn layui-btn-normal" id="btn">绘制流程</button>
<div class="layui-form">
	<table class="layui-table">
		<colgroup>
			<col width="150">
			<col width="150">
			<col width="150">
			<col width="150">
			<col width="50">
			<col width="200">
			<col>
		</colgroup>
		<thead>
		<tr>
			<td width="10%">模型编号</td>
			<td width="10%">版本</td>
			<td width="20%">模型名称</td>
			<td width="20%">模型key</td>
			<td width="20%">实例ID</td>
			<td width="40%">操作</td>


		</tr>
		</thead>
		<tbody>
		<#list modelList as model>
			<tr>
				<td width="10%">${model.id}</td>
				<td width="10%">${model.version}</td>
				<td width="20%"><#if (model.name)??>${model.name}<#else> </#if></td>
				<td width="20%"><#if (model.key)??>${model.key}<#else> </#if></td>
				<td width="20%"><#if (model.deploymentId)??>${model.deploymentId}<#else>未发布</#if> </td>
				<td width="40%">
					<a href="/editor?modelId=${model.id}">编辑</a>
					<a href="/publish?modelId=${model.id}">发布</a>
					<a href="/revokePublish?modelId=${model.id}">撤销</a>
					<a href="/delete?modelId=${model.id}">删除</a>
				</td>
			</tr>
		</#list>

		</tbody>
	</table>
</div>

<div>



</div>
</body>
</html>
<script src="http://lib.sinaapp.com/js/jquery/1.7.2/jquery.min.js"></script>
<script src="/static/layui/layui.js"></script>
<script>
	layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element', 'slider'], function(){

		$("#btn").click(function(){

			layer.open({
				type: 2,
				title: '添加流程',
				shadeClose: true,
				shade: false,
				maxmin: true, //开启最大化最小化按钮
				area: ['893px', '600px'],
				content: '/add'
			});



		})




	})


</script>
