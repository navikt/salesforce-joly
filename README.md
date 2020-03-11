# salesforce-joly
Joly, the anti-corruptor is an layer against our Salesforce instance. It subscribes to events on SF with CometD and publish them to Kafka as domain events. 


### Development (in Nav)

```

```


```
PushTopic pushTopic = new PushTopic();
pushTopic.Name = 'TaskUpdates';
pushTopic.Query = 'SELECT AccountId,Id,OwnerId,Description,CreatedDate,Subject from Task';
pushTopic.ApiVersion = 48.0;
pushTopic.NotifyForOperationCreate = true;
pushTopic.NotifyForOperationUpdate = true;
pushTopic.NotifyForOperationUndelete = true;
pushTopic.NotifyForOperationDelete = true;
pushTopic.NotifyForFields = 'Referenced';
insert pushTopic;
```

```
sfdx force:org:display -u JolyDev --json > JolyDev.json
sfdx force:org:open -u JolyDev
sfdx force:source:pull -u JolyDev
```


## Gradle
Kjøre `formatKotlin` før commit for å slippe at lintern klager. Andre gradle kommandoer:
```
./gradlew formatKotlin
./gradlew clean build
./gradlew test --info
```



### Salesforce
- Process Builder med notification
- Popup ved endringer
- Custom field
- Record view form
