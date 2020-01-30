FROM open-liberty
COPY --chown=1001:0 src/main/liberty/config /config/
COPY --chown=1002:0 target/guide-sessions.war /config/apps
COPY --chown=1001:0 target/hazelcast-*.jar /opt/ol/wlp/usr/shared/resources/hazelcast.jar
RUN configure.sh
