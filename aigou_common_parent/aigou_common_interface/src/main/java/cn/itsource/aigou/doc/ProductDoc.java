package cn.itsource.aigou.doc;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
:name,subName===>all
,brandId,productTypeId,productId
        saleCount,onSaleTime,commentCount,viewCount:取出来设置

        ,价格:maxPrice,minPrice(通过sku计算出价格区间),

        从es中返回结果:
        Medias:媒体属性值:
        skuProperties(ext表里的),viewProperties:(ext表里的),
        接口的封装:*/
@Document(indexName = "aigou",type = "product")
public class ProductDoc {

    @Id
    private Long id;//productId

    private Long brandId;//

    private Long productTypeId;//

    private Integer saleCount;//
    private Integer commentCount;//
    private Integer viewCount;//
    private Long onSaleTime;//

    //
    private Integer maxPrice;// 价格: 99.99 mysql以分为单位存:
    private Integer minPrice;

    // 不查询开始==================
    // skuProperties(ext表里的),viewProperties:(ext表里的),
    private List<Map> skuProperties = new ArrayList<Map>();

    private List<Map> viewProperties = new ArrayList<>();

    // Medias  "groupname/name","groupname/name"
    private List<String> medias = new ArrayList<>();

    // 不查询结束==================


    @Field(type = FieldType.Text,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String all;//把mysql的name,subName数据拼接过来


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public Long getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(Long productTypeId) {
        this.productTypeId = productTypeId;
    }

    public Integer getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(Integer saleCount) {
        this.saleCount = saleCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Long getOnSaleTime() {
        return onSaleTime;
    }

    public void setOnSaleTime(Long onSaleTime) {
        this.onSaleTime = onSaleTime;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public String getAll() {
        return all;
    }

    public void setAll(String all) {
        this.all = all;
    }

    public List<Map> getSkuProperties() {
        return skuProperties;
    }

    public void setSkuProperties(List<Map> skuProperties) {
        this.skuProperties = skuProperties;
    }

    public List<Map> getViewProperties() {
        return viewProperties;
    }

    public void setViewProperties(List<Map> viewProperties) {
        this.viewProperties = viewProperties;
    }

    public List<String> getMedias() {
        return medias;
    }

    public void setMedias(List<String> medias) {
        this.medias = medias;
    }
}
