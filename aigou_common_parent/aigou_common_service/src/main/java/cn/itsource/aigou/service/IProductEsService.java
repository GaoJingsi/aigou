package cn.itsource.aigou.service;

import cn.itsource.aigou.client.ProductEsClient;
import cn.itsource.aigou.doc.ProductDoc;
import cn.itsource.aigou.util.AjaxResult;
import cn.itsource.aigou.util.PageList;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

public interface IProductEsService {

    //添加一个
    void addOne(ProductDoc productDoc);

    //批量添加
    void addBatch(List<ProductDoc> productDocList);

    //删除一个
    void deleteOne(Long id);

    //批量删除
    void deleteBatch(List<Long> ids);

    //查询一个
    ProductDoc findOne(Long id);


    PageList<ProductDoc> queryProducts(Map<String,Object> params);
}
