FROM openjdk:21-jdk

RUN rm -f /etc/localtime && \
  ln -sv /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
  echo "Asia/Shanghai" > /etc/timezone

ADD ./build/libs/guess-service.jar /opt/app/
CMD java $JAVA_OPTS -jar /opt/app/guess-service.jar

EXPOSE 8080
