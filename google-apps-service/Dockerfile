FROM java:latest
WORKDIR /opt/docker
ADD target/docker/stage/opt /opt
RUN ["chown", "-R", "daemon:daemon", "."]
USER daemon
ENTRYPOINT ["bin/google-apps-service"]
EXPOSE 9000
CMD []
