<%@page import="org.opencms.ade.upload.CmsUploadActionElement"%><%--
--%><%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %><%
  CmsUploadActionElement upload = new CmsUploadActionElement(pageContext, request, response);
%><!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title><%= upload.getTitle() %></title>
    <script type="text/javascript" src="<cms:link>/system/modules/org.opencms.ade.upload/resources/resources.nocache.js</cms:link>"></script>
    <%= upload.exportAll() %>
  </head>
  <body style="margin: 0px;">&nbsp;</body>
</html>
