## Customize Signalling and Notification Channels

### How to run the application

Export the version of Debezium you want to use and start the containers:

```
export DEBEZIUM_VERSION=2.3
docker-compose up -d
```

Register the connector:

```
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d @register-postgres-connector.json
```

