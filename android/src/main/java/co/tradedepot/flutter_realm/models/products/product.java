package co.tradedepot.flutter_realm.models.products;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

import java.util.Date;
import java.util.List;

public class product extends RealmObject {

    @PrimaryKey
    private String _id;
    private String _groupId;
    @Required
    private String _partitionKey;
    private Boolean autoFulfillment;
    private String barcode;
    private String brand;
    private String brandId;
    private String category;
    private String categoryGroup;
    private String categoryGroupId;
    private String categoryId;
    private Date createdAt;
    private String createdBy;
    private String description;
    private Boolean isOnline;
    private Boolean isPickUp;
    private Boolean isPos;
    private Boolean keepSelling;
    private Boolean manageStock;
    private String name;
    private Boolean onlineOrdering;
    private String opt1;
    private String opt2;
    private String originProductId;
    private Integer price;
    private String producerId;
    private String productType;
    private String status;
    private String supplier;
    private Boolean taxable;
    private String uom;
    private Date updatedAt;
    private Boolean visiblePos;
    private Integer weight;

    // Standard getters & setters
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }
    public String get_groupId() { return _groupId; }
    public void set_groupId(String _groupId) { this._groupId = _groupId; }
    public String get_partitionKey() { return _partitionKey; }
    public void set_partitionKey(String _partitionKey) { this._partitionKey = _partitionKey; }
    public Boolean getAutoFulfillment() { return autoFulfillment; }
    public void setAutoFulfillment(Boolean autoFulfillment) { this.autoFulfillment = autoFulfillment; }
    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getBrandId() { return brandId; }
    public void setBrandId(String brandId) { this.brandId = brandId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getCategoryGroup() { return categoryGroup; }
    public void setCategoryGroup(String categoryGroup) { this.categoryGroup = categoryGroup; }
    public String getCategoryGroupId() { return categoryGroupId; }
    public void setCategoryGroupId(String categoryGroupId) { this.categoryGroupId = categoryGroupId; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
//    public RealmList<product_integrations> getIntegrations() { return integrations; }
//    public void setIntegrations(RealmList<product_integrations> integrations) { this.integrations = integrations; }
    public Boolean getIsOnline() { return isOnline; }
    public void setIsOnline(Boolean isOnline) { this.isOnline = isOnline; }
    public Boolean getIsPickUp() { return isPickUp; }
    public void setIsPickUp(Boolean isPickUp) { this.isPickUp = isPickUp; }
    public Boolean getIsPos() { return isPos; }
    public void setIsPos(Boolean isPos) { this.isPos = isPos; }
    public Boolean getKeepSelling() { return keepSelling; }
    public void setKeepSelling(Boolean keepSelling) { this.keepSelling = keepSelling; }
    public Boolean getManageStock() { return manageStock; }
    public void setManageStock(Boolean manageStock) { this.manageStock = manageStock; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Boolean getOnlineOrdering() { return onlineOrdering; }
    public void setOnlineOrdering(Boolean onlineOrdering) { this.onlineOrdering = onlineOrdering; }
    public String getOpt1() { return opt1; }
    public void setOpt1(String opt1) { this.opt1 = opt1; }
    public String getOpt2() { return opt2; }
    public void setOpt2(String opt2) { this.opt2 = opt2; }
    public String getOriginProductId() { return originProductId; }
    public void setOriginProductId(String originProductId) { this.originProductId = originProductId; }
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    public String getProducerId() { return producerId; }
    public void setProducerId(String producerId) { this.producerId = producerId; }
    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }
//    public RealmList<String> getSearchTerms() { return searchTerms; }
//    public void setSearchTerms(RealmList<String> searchTerms) { this.searchTerms = searchTerms; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
//    public RealmList<String> getTags() { return tags; }
//    public void setTags(RealmList<String> tags) { this.tags = tags; }
    public Boolean getTaxable() { return taxable; }
    public void setTaxable(Boolean taxable) { this.taxable = taxable; }
    public String getUom() { return uom; }
    public void setUom(String uom) { this.uom = uom; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public Boolean getVisiblePos() { return visiblePos; }
    public void setVisiblePos(Boolean visiblePos) { this.visiblePos = visiblePos; }
    public Integer getWeight() { return weight; }
    public void setWeight(Integer weight) { this.weight = weight; }
}