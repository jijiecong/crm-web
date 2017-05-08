<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=Edge">
<meta name="renderer" content="webkit|ie-comp|ie-stand">
<title>动态表单工作台</title>
<meta name="keywords" content="">
<meta name="description" content="">
<meta name="robots" content="noindex,nofollow">
<script src="http://localhost:8080/admin/form/script/jquery.min.js"></script>
<script type="text/javascript"
	src="http://localhost:8080/admin/form/script/configBase.js"></script>
<link type="text/css" rel="stylesheet" href="/admin/form/skin/base.css">
<link type="text/css" rel="stylesheet"
	href="/admin/form/skin/content.css">
<link type="text/css" rel="stylesheet" href="/admin/form/skin/blue.css">
<script language="javascript" type="text/javascript"
	src="/admin/form/script/jquery-ui.min.js"></script>
<script language="javascript" type="text/javascript"
	src="/admin/form/script/dcselect.js"></script>
<script language="javascript" type="text/javascript"
	src="/admin/form/script/layer/layer.js"></script>
<link rel="stylesheet"
	href="http://localhost:8080/admin/form/script/layer/skin/layer.css"
	id="layui_layer_skinlayercss">
<script language="javascript" type="text/javascript"
	src="/admin/form/script/artTemplate/template.js"></script>
<script type="text/javascript"
	src="http://localhost:8080/admin/form/script/exam/exam.js"></script>
</head>


<h4 class="h4-bg T-center"><span style="font-size:18px;background-color: green"><font style="color: background-color: #fff">表单名称</font></span></h4>

<span style="color: red;"></span>
<body>
	<!--主体框架开始-->
	<div class="pagebox" id="pageContentId">
		<div class="home-desktop" id="desktop_scroll">
			<div style="width: 1025px; position: relative;">
				<div class="create-questions-content">
					<div class="exam-nav scrollCurr"
						style="position: fixed; top: 0px; left: 20px;">
						<div class="exam-item">
							<h4 class="exam-item-title">
								表单元素<i class="icon-expand"></i>
							</h4>
							<ul class="exam-nav-list" id="ui_sortable_exam">
								<li data-uid="1" data-tempid="drag_choice"><a
									href="javascript:;" data-checktype="1"><i
										class="icon-singleChoice"></i>单选项</a></li>
								<li data-uid="2" data-tempid="drag_choice"><a
									href="javascript:;" data-checktype="2"><i
										class="icon-multipleChoice"></i>多选项</a></li>
								<li data-uid="3" data-tempid="drag_choice"><a
									href="javascript:;" data-checktype="3"><i
										class="icon-gapFilling"></i>单行文本</a></li>
								<li data-uid="4" data-tempid="drag_choice"><a
									href="javascript:;" data-checktype="4"><i
										class="icon-multiRow"></i>多行文本</a></li>
								<li data-uid="5" data-tempid="drag_choice"><a
									href="javascript:;" data-checktype="5"><i
										class="icon-gapFilling"></i>固态文本</a></li>
								<li data-uid="5" data-tempid="drag_choice"><a
									href="javascript:;" data-checktype="6"><i
										class="icon-describe"></i>日期控件</a></li>
								<li data-uid="6" data-tempid="drag_choice"><a
									href="javascript:;" data-checktype="7"><i
										class="icon-picChoice"></i>地址控件</a></li>
							</ul>
						</div>
					</div>
					<!--开始位置-->
					<div class="create-questions">
						<div class="questions-head-title">
							<h4 class="h4-bg T_edit T-center" data-tid="10001">
								<span style="font-size: 18px;" id="formTitle">表单名称</span>
							</h4>
						</div>
						<ul class="ui-questions-content-list ui-sortable">
							<li class="ui-module items-questions"
								style="opacity: 1; z-index: 0;">
								<div class="theme-type">
									<div class="module-menu">
										<h4>Q1</h4>
										<div class="module-ctrl">
											<a href="javascript:void(0);" class="ui-up-btn"
												data-tisp="上移"><i class="icon-up"></i></a> <a
												href="javascript:void(0);" class="ui-down-btn"
												data-tisp="下移"><i class="icon-down"></i></a> <a
												href="javascript:void(0);" class="ui-del-btn" data-tisp="删除"><i
												class="icon-del"></i></a>
										</div>
									</div>
									<div class="ui-drag-area ui-sortable-handle">
										<div class="cq-title T_edit T_plugins" data-tid="10125">
											<span style="font-size: 16px;">个人资料</span>
										</div>
									</div>
									<!--多选和单选相关-->
									<div class="cq-items-content">
										<ul class="cq-unset-list" data-checktype="5"
											data-namestr="xxx1">

											<!--固态文本框-->
											<li>
												<div class="banner_xxx1">&nbsp;</div>
											</li>

										</ul>

									</div>
								</div>
							</li>
							<li class="ui-module items-questions"
								style="opacity: 1; z-index: 0;">
								<div class="theme-type">
									<div class="module-menu">
										<h4>Q2</h4>
										<div class="module-ctrl">
											<a href="javascript:void(0);" class="ui-up-btn"
												data-tisp="上移"><i class="icon-up"></i></a> <a
												href="javascript:void(0);" class="ui-down-btn"
												data-tisp="下移"><i class="icon-down"></i></a> <a
												href="javascript:void(0);" class="ui-del-btn" data-tisp="删除"><i
												class="icon-del"></i></a>
										</div>
									</div>
									<div class="ui-drag-area ui-sortable-handle">
										<div class="cq-title T_edit T_plugins" data-tid="10105">
											<span style="font-size: 16px;">姓名</span>
										</div>
									</div>
									<!--多选和单选相关-->
									<div class="cq-items-content">
										<ul class="cq-unset-list" data-checktype="3"
											data-namestr="xxx1">

											<li><input type="text" name="input_xxx1"
												style="border: 1px solid; border-color: rgba(82, 168, 236, .8); width: 300px; height: 30px;">
											</li>

										</ul>

									</div>
								</div>
							</li>
							<li class="ui-module items-questions">
								<div class="theme-type">
									<div class="module-menu">
										<h4>Q3</h4>
										<div class="module-ctrl">
											<a href="javascript:void(0);" class="ui-up-btn"
												data-tisp="上移"><i class="icon-up"></i></a> <a
												href="javascript:void(0);" class="ui-down-btn"
												data-tisp="下移"><i class="icon-down"></i></a> <a
												href="javascript:void(0);" class="ui-del-btn" data-tisp="删除"><i
												class="icon-del"></i></a>
										</div>
									</div>
									<div class="ui-drag-area ui-sortable-handle">
										<div class="cq-title T_edit T_plugins" data-tid="10095">
											<span style="font-size: 16px;">联系电话</span>
										</div>
									</div>
									<!--多选和单选相关-->
									<div class="cq-items-content">
										<ul class="cq-unset-list" data-checktype="3"
											data-namestr="xxx1">

											<li><input type="text" name="input_xxx1"
												style="border: 1px solid; border-color: rgba(82, 168, 236, .8); width: 300px; height: 30px;">
											</li>

										</ul>

									</div>
								</div>
							</li>
							<li class="ui-module items-questions">
								<div class="theme-type">
									<div class="module-menu">
										<h4>Q4</h4>
										<div class="module-ctrl">
											<a href="javascript:void(0);" class="ui-up-btn"
												data-tisp="上移"><i class="icon-up"></i></a> <a
												href="javascript:void(0);" class="ui-down-btn"
												data-tisp="下移"><i class="icon-down"></i></a> <a
												href="javascript:void(0);" class="ui-del-btn" data-tisp="删除"><i
												class="icon-del"></i></a>
										</div>
									</div>
									<div class="ui-drag-area ui-sortable-handle">
										<div class="cq-title T_edit T_plugins" data-tid="10045">
											<span style="font-size: 16px;">预约选项</span>
										</div>
									</div>
									<!--多选和单选相关-->
									<div class="cq-items-content">
										<ul class="cq-unset-list" data-checktype="5"
											data-namestr="xxx1">

											<!--固态文本框-->
											<li>
												<div class="banner_xxx1">&nbsp;</div>
											</li>

										</ul>

									</div>
								</div>
							</li>
							<li class="ui-module items-questions">
								<div class="theme-type">
									<div class="module-menu">
										<h4>Q5</h4>
										<div class="module-ctrl">
											<a href="javascript:void(0);" class="ui-up-btn"
												data-tisp="上移"><i class="icon-up"></i></a> <a
												href="javascript:void(0);" class="ui-down-btn"
												data-tisp="下移"><i class="icon-down"></i></a> <a
												href="javascript:void(0);" class="ui-del-btn" data-tisp="删除"><i
												class="icon-del"></i></a>
										</div>
									</div>
									<div class="ui-drag-area ui-sortable-handle">
										<div class="cq-title T_edit T_plugins" data-tid="10078">
											<span style="font-size: 16px;">地址控件</span>
										</div>
									</div>
									<!--多选和单选相关-->
									<div class="cq-items-content">
										<ul class="cq-unset-list" data-checktype="7"
											data-namestr="xxx1">

											<li>
												<div class="addressPlaceHolder">地址控件占位符，供移动端渲染用</div>
											</li>

										</ul>

									</div>
								</div>
							</li>
							<li class="ui-module items-questions">
								<div class="theme-type">
									<div class="module-menu">
										<h4>Q6</h4>
										<div class="module-ctrl">
											<a href="javascript:void(0);" class="ui-up-btn"
												data-tisp="上移"><i class="icon-up"></i></a> <a
												href="javascript:void(0);" class="ui-down-btn"
												data-tisp="下移"><i class="icon-down"></i></a> <a
												href="javascript:void(0);" class="ui-del-btn" data-tisp="删除"><i
												class="icon-del"></i></a>
										</div>
									</div>
									<div class="ui-drag-area ui-sortable-handle">
										<div class="cq-title T_edit T_plugins" data-tid="10126">
											详细地址<span style="font-size: 16px;"></span>
										</div>
									</div>
									<!--多选和单选相关-->
									<div class="cq-items-content">
										<ul class="cq-unset-list" data-checktype="4"
											data-namestr="xxx1">

											<li><textarea name="textarea_xxx1"
													style="border: 1px solid; border-color: rgba(82, 168, 236, .8); width: 800px; height: 100px;"></textarea>
											</li>

										</ul>

									</div>
								</div>
							</li>
							<li class="ui-module items-questions"
								style="opacity: 1; z-index: 0;">
								<div class="theme-type">
									<div class="module-menu">
										<h4>Q7</h4>
										<div class="module-ctrl">
											<a href="javascript:void(0);" class="ui-up-btn"
												data-tisp="上移"><i class="icon-up"></i></a> <a
												href="javascript:void(0);" class="ui-down-btn"
												data-tisp="下移"><i class="icon-down"></i></a> <a
												href="javascript:void(0);" class="ui-del-btn" data-tisp="删除"><i
												class="icon-del"></i></a>
										</div>
									</div>
									<div class="ui-drag-area ui-sortable-handle">
										<div class="cq-title T_edit T_plugins" data-tid="10096">
											<span style="font-size: 16px;">最喜欢的人？</span>
										</div>
									</div>
									<!--多选和单选相关-->
									<div class="cq-items-content">
										<ul class="cq-unset-list" data-checktype="1"
											data-namestr="xxx1">


											<li><label class="input-check"> <input
													type="radio" name="xxx1" value="0">
											</label>
												<div class="cq-answer-content T_edit T_plugins"
													data-tid="100197">老师</div></li>

											<li><label class="input-check"> <input
													type="radio" name="xxx1" value="0">
											</label>
												<div class="cq-answer-content T_edit T_plugins"
													data-tid="100159">阿姨</div></li>



											<li><label class="input-check"><input
													type="radio" name="xxx1" value="0"></label>
												<div class="cq-answer-content T_edit T_plugins"
													data-tid="100136">我自己</div></li>
										</ul>

										<div class="cq-items-ctrl">
											<a href="javascript:void(0);" class="ui-add-item-btn"
												data-tisp="添加"><i class="icon-add"></i></a>
										</div>

									</div>
								</div>
							</li>
							<li class="ui-module items-questions"
								style="opacity: 1; z-index: 0;">
								<div class="theme-type">
									<div class="module-menu">
										<h4>Q8</h4>
										<div class="module-ctrl">
											<a href="javascript:void(0);" class="ui-up-btn"
												data-tisp="上移"><i class="icon-up"></i></a> <a
												href="javascript:void(0);" class="ui-down-btn"
												data-tisp="下移"><i class="icon-down"></i></a> <a
												href="javascript:void(0);" class="ui-del-btn" data-tisp="删除"><i
												class="icon-del"></i></a>
										</div>
									</div>
									<div class="ui-drag-area ui-sortable-handle">
										<div class="cq-title T_edit T_plugins" data-tid="10137">
											<span style="font-size: 16px;">最讨厌的行为？</span>
										</div>
									</div>
									<!--多选和单选相关-->
									<div class="cq-items-content">
										<ul class="cq-unset-list" data-checktype="2"
											data-namestr="xxx1">


											<li><label class="input-check"> <input
													type="checkbox" name="xxx1" value="0">
											</label>
												<div class="cq-answer-content T_edit T_plugins"
													data-tid="100229">抽烟</div></li>

											<li><label class="input-check"> <input
													type="checkbox" name="xxx1" value="0">
											</label>
												<div class="cq-answer-content T_edit T_plugins"
													data-tid="100161">喝酒</div></li>



											<li><label class="input-check"><input
													type="checkbox" name="xxx1" value="0"></label>
												<div class="cq-answer-content T_edit T_plugins"
													data-tid="100137">嫖娼</div></li>
										</ul>

										<div class="cq-items-ctrl">
											<a href="javascript:void(0);" class="ui-add-item-btn"
												data-tisp="添加"><i class="icon-add"></i></a>
										</div>

									</div>
								</div>
							</li>
							<li class="ui-module items-questions"
								style="opacity: 1; z-index: 0;">
								<div class="theme-type">
									<div class="module-menu">
										<h4>Q9</h4>
										<div class="module-ctrl">
											<a href="javascript:void(0);" class="ui-up-btn"
												data-tisp="上移"><i class="icon-up"></i></a> <a
												href="javascript:void(0);" class="ui-down-btn"
												data-tisp="下移"><i class="icon-down"></i></a> <a
												href="javascript:void(0);" class="ui-del-btn" data-tisp="删除"><i
												class="icon-del"></i></a>
										</div>
									</div>
									<div class="ui-drag-area">
										<div class="cq-title T_edit T_plugins" data-tid="10110">最喜欢吃的水果？</div>
									</div>
									<!--多选和单选相关-->
									<div class="cq-items-content">
										<ul class="cq-unset-list" data-checktype="1"
											data-namestr="xxx1">


											<li><label class="input-check"> <input
													type="radio" name="xxx1" value="0">
											</label>
												<div class="cq-answer-content T_edit T_plugins"
													data-tid="100162">车厘子</div></li>

											<li><label class="input-check"> <input
													type="radio" name="xxx1" value="0">
											</label>
												<div class="cq-answer-content T_edit T_plugins"
													data-tid="100157">杨桃</div></li>



											<li><label class="input-check"><input
													type="radio" name="xxx1" value="0"></label>
												<div class="cq-answer-content T_edit T_plugins"
													data-tid="100138">释迦</div></li>
										</ul>

										<div class="cq-items-ctrl">
											<a href="javascript:void(0);" class="ui-add-item-btn"
												data-tisp="添加"><i class="icon-add"></i></a>
										</div>

									</div>
								</div>
							</li>
						</ul>
						<ul class="ui-foot-all-list"></ul>
					</div>
				</div>
				<div style="height: 40px; margin: 20px 0; text-align: right;">
					<button type="button" class="cotrlBtn exam-save-btn btnBlue"
						style="height: 40px; width: 140px; font-size: 18px;">保存表单</button>
				</div>
			</div>
		</div>


		<div id="tmplRegion">
			fieldRegion
		
		
		</div>
		
		
		
		<!--渲染模块-->
		<script type="text/html" id="drag_choice">
				<li class="ui-module items-questions">
					<div class="theme-type">
						<div class="module-menu">
							<h4></h4>
							<div class="module-ctrl">
								<a href="javascript:void(0);" class="ui-up-btn" data-tisp="上移"><i class="icon-up"></i></a>
								<a href="javascript:void(0);" class="ui-down-btn" data-tisp="下移"><i class="icon-down"></i></a>
 								<a href="javascript:void(0);" class="ui-del-btn" data-tisp="删除"><i class="icon-del"></i></a>
							</div>
						</div>
						<div class="ui-drag-area">
							<div class="cq-title T_edit T_plugins" data-Tid="{{itmetid}}"><span style="font-size:16px;">{{if type==1}}单选项{{else if type==2}}多选项{{else if type==3}}单行文本{{else if type==5}}固态文本{{else if type==6}}日期控件{{else if type==7}}地址控件{{/if}}</span></div>
						</div>
						<!--多选和单选相关-->
						<div class="cq-items-content">
							<ul class="cq-unset-list" data-checkType="{{type}}" data-nameStr="{{name}}"> 
								{{if type==1 || type==2}}
									{{each items as itemData i}}
										<li>
											<label class="input-check">
												<input type="{{if type==1}}radio{{else if type==2}}checkbox{{else if type==3}}text{{/if}}" name="{{name}}" value="{{itemData.value}}">
											</label>
											<div class="cq-answer-content T_edit T_plugins" data-Tid="{{itemData.tid}}">选项{{i+1}}</div>
										</li>
									{{/each}}  
								{{else if type==3}}
									<li> 
	 									<input type="text" name="input_{{name}}" style="border:1px solid;border-color:rgba(82,168,236,.8);width: 300px;height: 30px;" />
									</li> 
								{{else if type==4}}
									<li> 
										<textarea name="textarea_{{name}}" style="border:1px solid;border-color:rgba(82,168,236,.8);width: 800px;height: 100px;"></textarea>
 									</li> 
								{{else if type==5}}
									<!--固态文本框-->
									<li> 
										 <div class="banner_{{name}}">&nbsp;</div>
 									</li> 
								{{else if type==6}}
									<li> 
										<div  name="datePlaceHolder" >日期控件占位符,供移动端渲染用</div>
 									</li> 
								{{else if type==7}}
									<li> 
										<div class="addressPlaceHolder" >地址控件占位符，供移动端渲染用</div>
 									</li> 
								{{/if}}
							</ul>
							{{if type==1 || type==2}}
								<div class="cq-items-ctrl">
									<a href="javascript:void(0);" class="ui-add-item-btn" data-tisp="添加"><i class="icon-add"></i></a>
								</div>
							{{/if}}
						</div>
					</div>
				</li>
			</script>


		<script type="text/html" id="drag_T_edit">
				<div class="cq-into-edit">
					<div class="add-edit cq-edit-title" contenteditable="true">{{title}}</div>
				</div>
			</script>
		<script type="text/html" id="T_edit_plugins">
				<div class="edit-plug-box">
					<a href="javascript:void(0);"><i class="icon-picChoice"></i></a>
					<a href="javascript:void(0);"><i class="icon-title"></i></a>
				</div>
			</script>
		<script type="text/html" id="ui_additem_content">
				{{each items as itemData i}}
				<li><label class="input-check"><input type="{{if type==1}}radio{{else if type==2}}checkbox{{/if}}" name="{{name}}" value="{{itemData.value}}"></label>
					<div class="cq-answer-content T_edit T_plugins" data-Tid="{{itemData.tid}}">选项{{i+1+index}}</div>
				</li>
				{{/each}}
			</script>
		<script type="text/javascript">
				$(function() {
					exam.init();
                    $("select").dcselect();
				});
			</script>
	</div>
	<!--主体框架结束-->


</body>
</html>
