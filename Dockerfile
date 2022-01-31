FROM tomcat:8.5.41-jre8-alpine
#COPY D:/docker/tomcat/webapps /usr/local/tomcat/webapps/
EXPOSE 8080
CMD ["catalina.sh","run"]