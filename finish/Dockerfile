FROM open-liberty
COPY src/main/liberty/config /config/
COPY target/guide-sessions.war /config/apps
COPY target/hazelcast-*.jar /opt/ol/wlp/usr/shared/resources/hazelcast.jar
RUN configure.sh
