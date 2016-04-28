"# Minesweeper" 

запаковка в jar: (картинки должны лежать в том же каталоге что и джарка)

cd ...\Minesweeper\src>
javac app\*.java
jar cvfe ..\Minesweeper.jar app.Game app\*.class ..\*.gif
cd ..
java -jar Minesweeper.jar
