package cn.itsource.aigou.service.impl;


import cn.itsource.aigou.doc.ProductDoc;
import cn.itsource.aigou.repository.ProductRepository;
import cn.itsource.aigou.service.IProductEsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
