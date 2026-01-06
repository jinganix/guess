FROM eclipse-temurin:25

RUN rm -f /etc/localtime && \
  ln -sv /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
  echo "Asia/Shanghai" > /etc/timezone

ADD ./build/libs/guess-service.jar /opt/app/

RUN echo '#!/bin/bash' > /entrypoint.sh
RUN echo 'exec java $JAVA_OPTS -jar /opt/app/guess-service.jar' >> /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]

EXPOSE 8080
