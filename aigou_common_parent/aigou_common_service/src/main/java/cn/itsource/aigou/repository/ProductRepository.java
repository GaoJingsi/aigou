package cn.itsource.aigou.repository;

import cn.itsource.aigou.doc.ProductDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductRepository extends ElasticsearchRepository<ProductDoc,Long>{
}
