package co.tradedepot.flutter_realm.models.brands;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import java.util.Date;

public class brand extends RealmObject {

    @PrimaryKey
    private String _id;
    private String _groupId;
    @Required
    private String _partitionKey;
    private Date createdAt;
    private String name;
    private Boolean onlineOrdering;
    private String originBrandId;
    private Date updatedAt;

    // Standard getters & setters
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }
    public String get_groupId() { return _groupId; }
    public void set_groupId(String _groupId) { this._groupId = _groupId; }
    public String get_partitionKey() { return _partitionKey; }
    public void set_partitionKey(String _partitionKey) { this._partitionKey = _partitionKey; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Boolean getOnlineOrdering() { return onlineOrdering; }
    public void setOnlineOrdering(Boolean onlineOrdering) { this.onlineOrdering = onlineOrdering; }
    public String getOriginBrandId() { return originBrandId; }
    public void setOriginBrandId(String originBrandId) { this.originBrandId = originBrandId; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
}