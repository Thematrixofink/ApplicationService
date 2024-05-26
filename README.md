# ApplicationService
## Introduction
**ApplicationService** is a Springboot Java back-end implementation based on the Springboot Java back-end, designed to provide stress testing functions for the system.

## Installation and Running
1. Clone the repository:
```
git clone https://github.com/your_username/your_microservice.git
```
2. Navigate to the project directory:
```
cd applicationservice
```
3. Build the project using Maven:
```
mvn clean install
```
4. Build the Docker image:
```
docker build -t <you_image_url> .
```
5. Deploying to Kubernetes:
Make sure you have a Kubernetes cluster set up and kubectl configured to communicate with the cluster.
   Modify the image, namespace and other information in the deployment.yaml configuration file.
```
kubectl apply -f deployment.yaml
```

## application.yaml
server:
  port: 8848
spring:
  application:
    name: PressureMeasureService
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://hostname/pressure_measure?serverTimezone=GMT%2B8&useSSL=true
    username: your database username
    password: your database password

mybatis:
  mapper-locations: classpath:mapper/*Mapper.xml
  type-aliases-package: com.hitices.pressure.repository
  configuration:
    map-underscore-to-camel-case: true

## Database configuration
1. Prepare image and run
```
docker run -p 14448:3306 --name applicationData \
-v /home/zhaozhenxiang/mysql/conf:/etc/mysql \
-v /home/zhaozhenxiang/mysql/logs:/var/log/mysql \
-v /home/zhaozhenxiang/mysql/data:/var/lib/mysql \
-e MYSQL_ROOT_PASSWORD=root \
-d mysql:5.5.40
```
2. Execute init.sql to initialize database


