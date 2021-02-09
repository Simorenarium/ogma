FROM adoptopenjdk:11

EXPOSE 8080
# Whover does Discord Bot's will know
ARG DISCORD_CLIENT_ID
ARG DISCORD_CLIENT_SECRET
ARG DISCORD_PUBLIC_KEY
ARG DISCORD_BOT_TOKEN
# Default DB stuff
ARG DB_USERNAME
ARG DB_PASSWORD
# JDBC Connection URL
ARG DB_CONNECTION_URL
# JDBC Driver Class (default is psql)
ARG DB_DRIVER_CLASSNAME

ARG JAR_FILE=build/libs/ogma*.jar

ADD ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]