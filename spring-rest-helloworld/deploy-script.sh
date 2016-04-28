#!/bin/bash 

if [ -z $1 ] ; then	{
	echo argument was missed!!! skipping
} else 
	echo deploying $1 application
	mvn package
	mvn war:war
	echo deploying war to tomcat
	sshpass -p "vagrant" scp target/$1 root@c6403:/opt/tomcat/apache-tomcat-7.0.63/webapps/
fi



