package ua.com.alevel.facade.sunglasses.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.com.alevel.facade.sunglasses.BrandFacade;
import ua.com.alevel.persistence.entity.sunglasses.brand.Brand;
import ua.com.alevel.service.sunglasses.brand.BrandService;

@Slf4j
@Service
public class BrandFacadeImpl implements BrandFacade {

    private final BrandService brandService;

    public BrandFacadeImpl(BrandService brandService) {
        this.brandService = brandService;
    }

    @Override
    public Brand findByBrandName(String brandName) {
        log.info("search brand by name: " + brandName);
        return brandService.findByBrandName(brandName);
    }
}
