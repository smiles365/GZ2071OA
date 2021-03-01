<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>后台首页</title>

    <!-- Bootstrap -->
    <link href="bootstrap/css/bootstrap.css" rel="stylesheet">
    <link href="css/style.css" rel="stylesheet">

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="http://cdn.bootcss.com/html5shiv/3.7.2/html5shiv.min.js"></script>
    <script src="http://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
    <script src="js/jquery.min.js"></script>
    <script src="bootstrap/js/bootstrap.min.js"></script>
    <script src="js/hardphp.js"></script>
</head>
<body style="background-image: images/2.bmp">

	<!--添加用户 编辑窗口 -->
	<div class="modal fade" id="createUserModal" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
	  <form id="permissionForm" action="updateUser" method="post">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">×</button>
					<h3 id="myModalLabel">编辑用户</h3>
				</div>
				<div class="modal-body">
					<table class="table table-bordered table-striped" width="800px">
						<tr>
							<td>帐号</td>
							<td><input class="form-control" name="name" placeholder="名称" value="${activeUser.username }"></td>
						</tr>					
						<tr>
							<td>初始密码</td>
							<td><input class="form-control" type="password" name="password" placeholder="密码" ></td>
						</tr>
						<tr>
						    <td>电子邮箱</td>
						    <td><input class="form-control" name="email" placeholder="链接" >
						    </td>
						</tr>
						
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-success" data-dismiss="modal"
						aria-hidden="true" onclick="javascript:document.getElementById('permissionForm').submit()">修改</button>
					<button class="btn btn-default" data-dismiss="modal"
						aria-hidden="true">关闭</button>
				</div>
			</div>
		</div>
	 </form>
	</div>
    <div class="myheading">
        <nav class="navbar navbar-inner">
            <div class="container-fluid">

                <div class="navbar-header">
                    <!--nav troggle-->
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#hard-navbar">
                        <span class="sr-only">导航切换</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <!--nav troggle-->
                    <!--brand-->
                    <a class="navbar-brand glyphicon glyphicon-fire" href="#"> 办公自动系统</a>
                    <!--brand-->
                </div>

                <!--nav links-->
                <div class="collapse navbar-collapse" id="hard-navbar">
					<ul class="nav navbar-nav navbar-mid"style="color: white;font-size: 16px;padding-left: 400px;padding-top: 15px">
						<li class="glyphicon glyphicon-piggy-bank"> 当前用户：&sect;&ring;${activeUser.username}&ring;&sect;</li>
					</ul>
                    <ul class="nav navbar-nav navbar-right">
                        <li><a href="welcome.html" class="atip" target="appiframe" data-toggle="tooltip" data-placement="bottom" data-title="首页"><span class="glyphicon glyphicon-home" aria-hidden="true"></span></a></li>
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
                                <span class="glyphicon glyphicon-sunglasses" aria-hidden="true"></span> hardphp
                            </a>
                            <ul class="dropdown-menu dropdown-menu-arrow-right" role="menu">
                            	
                               <!--   <li data-toggle="modal" data-target="#createUserModal"><span class="glyphicon glyphicon-edit" ></span> 修改资料</li> -->
                                <li><a href="logout"><span class="glyphicon glyphicon-log-out" ></span> 退出登录</a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
                <!--nav links-->
            </div>
        </nav>
    </div>

    <!--main-->
    <div class="container-fluid mybody">
        <div class="main-wapper">
            <!--菜单-->
            <div id="siderbar" class="siderbar-wapper">

                <div class="panel-group menu" id="accordion" role="tablist" aria-multiselectable="true">
				 <c:forEach var="menu" items="${activeUser.menuTree}" varStatus="state">
                    <div class="panel panel-inner">
                        <div class="panel-heading panel-heading-self" role="tab" id="heading${state.index}">
                            <h4 class="panel-title">
                                <a data-toggle="collapse" data-parent="#accordion" href="#collapse${state.index}" 
                                   <c:if test="${state.index==0}">aria-expanded="true"</c:if> 
                                   <c:if test="${state.index>0}">aria-expanded="false"</c:if> 
                                   aria-controls="collapse${state.index}">
                                    <span class="glyphicon glyphicon-list-alt"></span> ${menu.name}
                                </a>
                            </h4>
                        </div>
                        <div id="collapse${state.index}" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="heading${state.index}">
                            <div class="list-group list-group-self">
                               <c:forEach var="subMenu" items="${menu.children}">
                                 <a href="${subMenu.url}" target="appiframe" class="list-group-item"><span class="glyphicon glyphicon-menu-right"></span>${subMenu.name}</a>
                               </c:forEach>
                            </div>
                        </div>
                    </div>
				 </c:forEach>
                </div>

            </div>
            <!--菜单-->
            <!--内容-->
            <div id="content" class="main-content">

                <iframe src="welcome.html" style="width:100%;height: 100%;" id="appiframe" name="appiframe" frameborder="0"></iframe>

            </div>
            <!--内容-->
        </div>

    </div>

    <!--main-->

    <script type="text/javascript">
        $(function(){
            var options={
                animation:true,
                trigger:'hover' //触发tooltip的事件
            }
            $('.atip').tooltip(options);

        });



    </script>
</body>
</html>