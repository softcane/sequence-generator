# Sequence Generator

A play based service to generate sequence number for a given key using cassandra schema.

### Request/Response example
key name = "id"

```
sbt run
curl -X GET http://127.0.0.1:9191/v1/generate -H 'Cache-Control: no-cache'
```

## Response
```
{
    "id": 5,
    "createdAt": 1537126354435
}
```
