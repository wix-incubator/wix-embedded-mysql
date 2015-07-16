# Design decisions/considerations for v2

Flapdoodle process library is very flexible in many ways:
 - configurability - daemon config(port, version...), runtime config(logging), artifact store (executable naming, temp download path...), downlaod config (url, proxy)...
 - run modes - start/stop per test.
 
In EmbeddedMysql there is no plan to support:
 - run per test - mysql as a process is quite heavy in amount of files it needs, start-up files, initialization (create information_schema) and usage patterns we are looking into is E2E/IT tests, so there is no plan to support new instance for every test - you can do it, but we will not help you.
 
In EmbeddedMysql we plan to support:
 - configurability but just as much as needed and when it's needed, so runtime config, artifact storage are not exposed at all, when other configurability is limited to what is needed/requested.

Otherwise main idea for v2:
 - we don't need to expose starter/preparer/executable/process as it's not applicable for EmbeddedMysql case (see In EmbeddedMysql there is no plan to support), so single executbale 'EmbeddedMysql' is sufficient without possibility to start/stop it multiple times.
 - Have simple and fulent api where same concepts apply to different entities:
  - Simple version for basic scenario + builder for advanced cases. No inbetweeners. Ex. anEmbeddedMysql(version) ior anEmbeddedMysql(config) and no permitations with version + port or version + port + user.
  - Everything is exposed through static factories - no need to use 'new';
  - adding additional configurability is done either extending existing builder (MysqldConfig, SchemaConfig) or adding new config - DownloadConfig with url + proxy settings;
  - version - client should be aware of what version of mysql she needs. We provide version shortcuts like 'v5_6_latest', but no way to run it without telling what version client needs.

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

