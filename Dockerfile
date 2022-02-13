FROM openjdk:11-jdk-slim

ARG CI_PROJECT_NAME
ARG CI_COMMIT_SHORT_SHA
ARG KUBE_DOMAIN

VOLUME /tmp
ADD target/workshop-0.0.1-SNAPSHOT.jar application.jar

RUN echo -e "\
java \
-Dserver.use-forward-headers=true \
-Dspring.profiles.active=dev \
-Dspringdoc.swagger-server=https://$CI_PROJECT_NAME-$CI_COMMIT_SHORT_SHA.$KUBE_DOMAIN \
-jar /application.jar \
" > /run.sh

ENTRYPOINT ["/bin/sh", "/run.sh"]
