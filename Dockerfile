FROM openjdk:8-jre-alpine
RUN mkdir -p /opt/jmeter
RUN mkdir -p /opt/application
WORKDIR /opt/application
EXPOSE 8848