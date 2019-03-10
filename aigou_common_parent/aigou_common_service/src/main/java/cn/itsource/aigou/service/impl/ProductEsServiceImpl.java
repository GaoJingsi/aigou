package cn.itsource.aigou.service.impl;


import cn.itsource.aigou.doc.ProductDoc;
import cn.itsource.aigou.repository.ProductRepository;
import cn.itsource.aigou.service.IProductEsService;
import cn.itsource.aigou.util.PageList;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
public class ProductEsServiceImpl implements IProductEsService{
    @Autowired
    private ProductRepository productRepository;


    @Override
    public void addOne(ProductDoc productDoc) {
        productRepository.save(productDoc);
    }

    @Override
    public void addBatch(List<ProductDoc> productDocList) {
        productRepository.saveAll(productDocList);
    }

    @Override
    public void deleteOne(Long id) {
        productRepository.deleteById(id);

    }

    @Override
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            productRepository.deleteById(id);
        }

    }

    @Override
    public ProductDoc findOne(Long id) {
        return productRepository.findById(id).get();
    }

    /**
     * 都是参数的判断:
     *   要判断是否为null
     * @param params
     * @return
     */
    @Override
    public PageList<ProductDoc> queryProducts(Map<String, Object> params) {
        //传入很多参数:
        String keyword = (String)params.get("keyword");

        Long productTypeId=0L;
        Object pt = params.get("productType");
        if(pt!=null){
            productTypeId= Long.valueOf(pt.toString());
        }

        Long brandId=0L;
        Object bi = params.get("brandId");
        if(bi!=null){
            brandId= Long.valueOf(bi.toString());
        }
        // 分 前台查询传入的是元,es和mysql存的都是分: *100
        Long priceMin=0L;
        Object pm = params.get("priceMin");
        if(pm!=null){
            priceMin= Long.valueOf(pm.toString())*100;
        }

        Long priceMax=Long.MAX_VALUE;//默认绑定最大值
        Object pmx = params.get("priceMax");
        if(pmx!=null){
            priceMax= Long.valueOf(pmx.toString())*100;
        }
        String sortField = (String)params.get("sortField");
        String sortType = (String)params.get("sortType");
        Integer page = Integer.valueOf(params.get("page").toString());
        Integer rows = Integer.valueOf(params.get("rows").toString());
        //1:创建一个builder
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        //2:设置query: bool  filter
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if(!StringUtils.isEmpty(keyword)){
            //must:必须包含:关键字
            boolQuery.must(QueryBuilders.matchQuery("all", keyword));
        }

        //多个过滤条件:
        List<QueryBuilder> filter = boolQuery.filter();
        //filter:过滤  productTypeId
        if(productTypeId!=null&&productTypeId!=0){
            filter.add(QueryBuilders.termQuery("productTypeId", productTypeId));
        }

        if(brandId!=null&&brandId!=0){
            filter.add(QueryBuilders.termQuery("brandId", brandId));
        }
        //价格:private Integer maxPrice;// 价格: 99.99 mysql以分为单位存:
        //    private Integer minPrice;
        // max>=minPrice  && min<=maxPrice
        if(priceMax!=null&&priceMin!=null){
            filter.add(QueryBuilders.rangeQuery("minPrice").lte(priceMax));
            filter.add(QueryBuilders.rangeQuery("maxPrice").gte(priceMin));
        }
        builder.withQuery(boolQuery);

        //3:分页  PageRequest of(int page, int size)  page:前台传的是1,2,3===>后台实际应该从0开始
        builder.withPageable(PageRequest.of(page-1, rows));
        //4:排序  前台传的排序的地段:xl   xp  pl jg  rq
        SortOrder sortOrder=SortOrder.ASC;//默认值
        if(!StringUtils.isEmpty(sortType)){
            if("desc".equals(sortType)){
                sortOrder=SortOrder.DESC;
            }
            // asc:就使用默认值,不做判断
        }

        //哪一个字段排序
        if(!StringUtils.isEmpty(sortField)){
            if("xl".equals(sortField)){
                //销量排序:
                builder.withSort(SortBuilders.fieldSort("saleCount").order(sortOrder));

            }
            if("xp".equals(sortField)){
                //新品排序:
                builder.withSort(SortBuilders.fieldSort("onSaleTime").order(sortOrder));

            }
            if("pl".equals(sortField)){
                //评论排序:
                builder.withSort(SortBuilders.fieldSort("commentCount").order(sortOrder));

            }
            if("jg".equals(sortField)){
                //价格排序:
                builder.withSort(SortBuilders.fieldSort("minPrice").order(sortOrder));

            }
            if("rq".equals(sortField)){
                //人气排序:
                builder.withSort(SortBuilders.fieldSort("viewCount").order(sortOrder));

            }
        }

        //5:查哪些字段
        //builder.withSourceFilter()

        NativeSearchQuery build = builder.build();
        //6:执行查询:结果的封装返回
        Page<ProductDoc> search = productRepository.search(build);
        PageList<ProductDoc> pageList = new PageList<>();
        long total = search.getTotalElements();
        List<ProductDoc> content = search.getContent();
        pageList.setTotal(total);
        pageList.setRows(content);

        return pageList;
    }
}
