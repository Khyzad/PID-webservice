<%-- 
    Document   : hello
    Created on : Nov 8, 2015, 8:24:29 AM
    Author     : lruffin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <title>minted ids</title>
    </head>
 <pre>
{
    "Status" : <div th:text=${status}></div>,
    "Exception" : <div th:text=${exception}></div>,
    "Messages" : <div th:text=${message}></div>
}</pre>

<pre>
"${stacktrace}"    

</pre>
</html>
