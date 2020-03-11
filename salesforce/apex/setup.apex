
PushTopic pushTopic = [SELECT Id FROM PushTopic WHERE Name = 'TaskUpdates' LIMIT 1];

if (pushTopic.Id == NULL) {
    PushTopic pushTopic = new PushTopic();
}
pushTopic.Name = 'TaskUpdates';
pushTopic.Query = 'SELECT AccountId,Id,OwnerId from Task';
pushTopic.ApiVersion = 48.0;
pushTopic.NotifyForOperationCreate = true;
pushTopic.NotifyForOperationUpdate = true;
pushTopic.NotifyForOperationUndelete = true;
pushTopic.NotifyForOperationDelete = true;
pushTopic.NotifyForFields = 'All';

if (pushTopic.Id == NULL) {
    insert pushTopic;
} else {
    update pushTopic;
}
