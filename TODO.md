# Todo
## custom target dir

To work around firewalls (OS X). making custom target dir as a stable-named folder (instead of guid as now) it should work better for clients using firewalls on dev machines.

## custom download url/proxy settings

reason: for you could work-around proxies or host mysql packages inside organization. Would add it as a DownloadConfig or ArtifactStoreConfig provideable via EmbeddedMysql.Builder

## custom credentials per schema

Possibility to add additional credentails that have access to only that schema. This would be available only via builder. user in MysqldConfig has access to all schemas.

```scala
val schema: SchemaConfig = aSchemaConfig("aschema")
  .withScripts(classPathFiles("db/*.sql"))
  .withUser("anotherUser", "anotherPassword")
  .build
```

## custom bind host

Adding possibility for mysqld to bind to a different host. 

