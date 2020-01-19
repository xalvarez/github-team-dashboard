FROM adoptopenjdk:11-jre-openj9
COPY build/libs/github-team-dashboard-all.jar github-team-dashboard.jar
EXPOSE 8080
CMD java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar github-team-dashboard.jar
