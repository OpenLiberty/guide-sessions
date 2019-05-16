FROM open-liberty

# Set up the server
COPY --chown=1001:0 target/liberty/wlp/usr/servers/defaultServer/bootstrap.properties /config
COPY --chown=1001:0 target/liberty/wlp/usr/servers/defaultServer/server.env /config
COPY --chown=1001:0 src/main/liberty/config/server.xml /config

# Set up the application
COPY --chown=1001:0 target/*.war /config/apps/guide-sessions.war

# Set up the required shared bundle
USER root
RUN mkdir /opt/ol/wlp/usr/shared
RUN mkdir /opt/ol/wlp/usr/shared/resources
RUN mkdir /opt/ol/wlp/usr/shared/config
USER 1001
COPY --chown=1001:0 target/liberty/wlp/usr/shared/resources/hazelcast.jar /opt/ol/wlp/usr/shared/resources
COPY --chown=1001:0 target/liberty/wlp/usr/shared/config/hazelcast-config.xml /opt/ol/wlp/usr/shared/config
