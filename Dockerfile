FROM quay.io/debezium/connect:latest
USER root
RUN microdnf -y install lsof && microdnf clean all
USER kafka
COPY --chown=kafka:kafka out/artifacts/debezium_custom_signalling_notification_example_jar/debezium-custom-signalling-notification-example.jar /kafka/connect/debezium-connector-postgres/
