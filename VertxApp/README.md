# VertxApp
to create fat jar run
#mvn clean package
It will create target/*-SNAPSHOT-fat.jar embedding our application along with all the dependencies (including vert.x itself).

to launch verticle run
java -jar target/*-SNAPSHOT-fat.jar
