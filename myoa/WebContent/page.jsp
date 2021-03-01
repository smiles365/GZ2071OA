<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- 分页处理 -->
<table width='100%' align='center' style='font-size: 15px;border: 2px;border-color: black;'
	class="yahoo">
	<tr>
		<td
		
			style='COLOR: #0061de; MARGIN-RIGHT: 3px; PADDING-TOP: 2px; TEXT-DECORATION: none'>
			
			
			 总页数:${pageModel.pages}&nbsp;&nbsp;&nbsp;&nbsp;总记录条数:${pageModel.total}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			
			<c:choose>
				<c:when test="${pageModel.pageNum==1}">
				
					<button class='disabled' >&laquo;</button>
					<c:forEach begin="1" end="${pageModel.pages}" var="i">
						<c:choose>
							<c:when test="${pageModel.pageNum==i}">
								<button class='current'>${i}</button>
							</c:when>
							<c:otherwise>
								<button><a href='#' onclick="toPage(${i})">${i}</a></button>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					<button class='current'><a href='#'
						onclick="toPage(${pageModel.pageNum+1})">&raquo;</a></button>
				
				</c:when>
				<c:when test="${pageModel.pageNum==pageModel.pages}">
					<button class='current'><a href='#'
						onclick="toPage(${pageModel.pageNum-1})">&laquo;</a></button>
					<c:forEach begin="1" end="${pageModel.pages}" var="i">
						<c:choose>
							<c:when test="${pageModel.pageNum==i}">
								<button class='current'>${i}</button>
							</c:when>
							<c:otherwise>
								<button><a href='#' onclick="toPage(${i})">${i}</a></button>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					<button class='disabled'>&raquo;</button>
				</c:when>
				<c:otherwise>
					<button class='current'><a href='#' onclick="toPage(${pageModel.pageNum-1})" >&laquo;</a></button>
					<c:forEach begin="1" end="${pageModel.pages}" var="i">
						<c:choose>
							<c:when test="${pageModel.pageNum==i}">
								<button class='current'>${i}</button>
							</c:when>
							<c:otherwise>
								<button><a href='#' onclick="toPage(${i})">${i}</a></button>
							</c:otherwise>
						</c:choose>
					</c:forEach>
					<button class='current'><a href='#'
						onclick="toPage(${pageModel.pageNum+1})">&raquo;</a></button>
				</c:otherwise>
			</c:choose>
			 &nbsp;
			 跳转到&nbsp;&nbsp;<input
			style='text-align: center; BORDER-RIGHT: #aaaadd 1px solid; PADDING-RIGHT: 5px; BORDER-TOP: #aaaadd 1px solid; PADDING-LEFT: 5px; PADDING-BOTTOM: 2px; MARGIN: 2px; BORDER-LEFT: #aaaadd 1px solid; COLOR: #000099; PADDING-TOP: 2px; BORDER-BOTTOM: #aaaadd 1px solid; TEXT-DECORATION: none'
			type='text' size='2' id='pager_jump_page_size' /> &nbsp;<input
			type='button'
			style='text-align: center; BORDE R-RIGHT: #dedfde 1px solid; PADDING-RIGHT: 6px; BACKGROUND-POSITION: 50% bottom; BORDER-TOP: #dedfde 1px solid; PADDING-LEFT: 6px; PADDING-BOTTOM: 2px; BORDER-LEFT: #dedfde 1px solid; COLOR: #0061de; MARGIN-RIGHT: 3px; PADDING-TOP: 2px; BORDER-BOTTOM: #dedfde 1px solid; TEXT-DECORATION: none'
			value='确定' id='pager_jump_btn' onclick="pagerJump()"/>
			
		</td>
	</tr>
</table>
