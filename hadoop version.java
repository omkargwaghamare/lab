hadoop version
javac -version
export HADOOP_CLASSPATH=$(hadoop classpath)
echo $HADOOP_CLASSPATH
start-yarn.sh
start-dfs.sh
hadoop fs -mkdir /Omk
hadoop fs -mkdir /Omk/Ar
hadoop fs -put '/home/hadoop/4SF21CS104/Input3/input.txt'  /Omk/Ar
cd '/home/hadoop/4SF21CS104/EvenOdd'

Empty folder----------------------------------EvenOdd

javac -classpath ${HADOOP_CLASSPATH} -d '/home/hadoop/4SF21CS104/EvenOdd'  '/home/hadoop/4SF21CS104/EvenOddcount.java'
cd ..
jar -cvf EvenOddcount.jar -C EvenOdd/ .
hadoop jar '/home/hadoop/4SF21CS104/EvenOddcount.jar'  EvenOddcount /Omk/Ar /Omk/output
hadoop dfs -cat /Omk/output/*

localhost 9870 : http://localhost:9870/dfshealth.html#tab-overview