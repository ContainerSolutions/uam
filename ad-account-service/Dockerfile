FROM java:latest
WORKDIR /opt/docker
ADD target/docker/stage/opt /opt
RUN ["chown", "-R", "daemon:daemon", "."]
USER daemon
ENTRYPOINT ["bin/ad-account-service"]
EXPOSE 9000
CMD []
