@echo off
echo 正在啟動應用程序...
C:\SideProject\ai-stack-t2\apache-maven-3.9.6\bin\mvn spring-boot:run > app_output.log 2>&1
echo 應用程序已停止，請查看 app_output.log 文件獲取詳細信息
pause
