FROM eclipse-temurin:23-alpine
RUN apk update && apk upgrade
RUN apk add inotify-tools dos2unix
ENV HOME=/app
RUN mkdir -p $HOME
WORKDIR $HOME