<%@ page language="java" contentType="text/html; charset=EUC-KR"
	pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!-- ���� �ۼ��ϰ� �ִ� ������ UTF-8 DOS�����Դϴ�.-->
<html xmlns="http://www.w3.org/1999/xhtml" lang="ko">
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
</head>
<body>
	<div id="ex_js_syntax">
		<script type="text/javascript" language="javascript">
		<!--
			/* Form Events onfocus onblur onchange onsubmit 
			onsubmit�� ���� form�� �̺�Ʈ �ɸ� �˴ϴ�.(Ƽ���丮 �ҽ� ����)
			ex) form action="sub_form.php" onsubmit="sub_send()*/
			function onfocus_event() {
				/* onfocus �̺�Ʈ */
				alert("onfocus ����ǿ����");
			}
			function onblur_event() {
				/* onblur �̺�Ʈ */
				alert("onblur ����ǿ����");
			}
			function onchange_event() {
				/* onblur �̺�Ʈ */
				alert("onchange ����ǿ����");
			}

			//-->
		</script>
		
		
		<script type="text/javascript">
		function onblur_eventTest() {
			/* onblur �̺�Ʈ */
			input_type.name.value;
			if (Trim(input_type.name.value == "") || input_type.name.value == null) {
				input_type.name.value == "�Ƶ� �Է��ϼ���";
				alert('onblur')
			}
			alert('dd')
			
		}
		</script>
	
		
		<form name="input_type" method="post">
			<!-- ���� �ڽ����� �ȵ� => form�±׾ȿ� form�±������� ������~��.�� -->
			<input type="text" value="��Ŀ�� ������ ����" onfocus="onfocus_event();">
			<br /> <input type="text" name="test"
				onclick="if(this.value=='�Ƶ� �Է��ϼ���'){this.value=''}"
				value="�Ƶ� �Է��ϼ���" onblur="onblur_eventTest();"><br /> <select
				name="atte_select_base" onchange="onchange_event();">
				<option value="base">�⺻</option>
				<option value="apple">���</option>
				<option value="orange">������</option>
			</select> <br /> 
			<input type="text" name="id" value="�Ƶ�" onfocus="this.value=''" onblur="if(this.value==''){this.value='�Ƶ�'}" />
			<input type="text" name="dd" plaseholder="ddddddddd"></input>
		</form>
	</div>
</body>
</html>