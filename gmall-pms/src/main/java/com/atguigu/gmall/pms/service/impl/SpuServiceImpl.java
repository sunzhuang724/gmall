package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.mapper.SkuMapper;
import com.atguigu.gmall.pms.mapper.SpuDescMapper;
import com.atguigu.gmall.pms.service.SkuAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SpuAttrValueService;
import com.atguigu.gmall.pms.vo.SkuVo;
import com.atguigu.gmall.pms.vo.SpuAttrValueVo;
import com.atguigu.gmall.pms.vo.SpuVo;

import com.atguigu.gmall.sms.vo.SkuSaleVo;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SpuMapper;
import com.atguigu.gmall.pms.service.SpuService;
import org.springframework.util.CollectionUtils;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo querySpuInfo(PageParamVo pageParamVo, Long categoryId) {
        QueryWrapper<SpuEntity> queryWrapper = new QueryWrapper<>();
//        是否为0,为0是查全部，否则按条件查询
        if (categoryId != 0) {
            queryWrapper.eq("category_id", categoryId);
        }
//        判断分页列表中查询条件是否为空
        String key = pageParamVo.getKey();
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(t -> t.like("name", key)).or().like("id", key);
        }
        IPage<SpuEntity> page = this.page(pageParamVo.getPage(), queryWrapper);
        PageResultVo pageResultVo = new PageResultVo(page);
        return pageResultVo;
    }

    @Autowired
    private SpuDescMapper descMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private SpuAttrValueService baseService;
    //    @Autowired
//    private SpuAttrValueVo spuAttrValueVo;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private GmallSmsClient smsClient;
    @Autowired
    private SkuAttrValueService skuAttrValueService;
@Autowired
private RabbitTemplate rabbitTemplate;

    @Override
//      @GlobalTransactional
    public void BigSave(SpuVo spuVo) {
// 保存相关内容
//        1.1保存spu基本信息  spu_info
        spuVo.setPublishStatus(1);
        spuVo.setCreateTime(new Date());
        spuVo.setUpdateTime(spuVo.getCreateTime());
        this.save(spuVo);
        Long spuId = spuVo.getId();
//        1.2保存spu的描述信息 spu_info_desc
        SpuDescEntity spuInfoDescEntity = new SpuDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        spuInfoDescEntity.setDecript(StringUtils.join(spuVo.getSpuImages(), ","));
        this.descMapper.insert(spuInfoDescEntity);
//        1.3保存spu的规格参数信息
        List<SpuAttrValueVo> baseAttrs = spuVo.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)) {
            List<SpuAttrValueEntity> spuAttrValueEntities = baseAttrs.stream().map(spuAttrValueVo -> {
                spuAttrValueVo.setSpuId(spuId);
                spuAttrValueVo.setSort(0);
                return spuAttrValueVo;
            }).collect(Collectors.toList());
            this.baseService.saveBatch(spuAttrValueEntities);
        }
//        2.保存sku相关信息
        List<SkuVo> skuVos = spuVo.getSkus();
        if (CollectionUtils.isEmpty(skuVos)) {
            return;
        }
        skuVos.forEach(skuVo -> {
            //        2.1保存sku基本信息
            SkuEntity skuEntity = new SkuEntity();
            BeanUtils.copyProperties(skuVo, skuEntity);
            skuEntity.setBrandId(spuVo.getBrandId());
            skuEntity.setCatagoryId(spuVo.getCategoryId());
            List<String> images = skuVo.getImages();
            if (!CollectionUtils.isEmpty(images)) {
                skuEntity.setDefaultImage(skuEntity.getDefaultImage() == null ? images.get(0) : skuEntity.getDefaultImage());
            }
            skuEntity.setSpuId(spuId);
            this.skuMapper.insert(skuEntity);
            Long skuId = skuEntity.getId();

            //        2.2保存sku图片信息
            if (!CollectionUtils.isEmpty(images)) {
                String defaultImage = images.get(0);
                List<SkuImagesEntity> skuImageses = images.stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setDefaultStatus(StringUtils.equals(defaultImage, image) ? 1 : 0);
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setSort(0);
                    skuImagesEntity.setUrl(image);
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                this.skuImagesService.saveBatch(skuImageses);
            }
            //        2.3保存sku的规格参数
            List<SkuAttrValueEntity> saleAttrs = skuVo.getSaleAttrs();
            saleAttrs.forEach(saleAttr -> {
                saleAttr.setSort(0);
                saleAttr.setSkuId(skuId);
            });
            this.skuAttrValueService.saveBatch(saleAttrs);


//在sms模块中完成，然后远程调用
//        3.1.积分优惠
//        3.2满减优惠
//        3.3数量折扣
            SkuSaleVo skuSaleVo = new SkuSaleVo();
            BeanUtils.copyProperties(skuVo, skuSaleVo);
            skuSaleVo.setSkuId(skuId);
            this.smsClient.saveSkuSaleInfo(skuSaleVo);
        });
//        往消息队列中发送同步消息，共消费者监听消费
        rabbitTemplate.convertAndSend("PMS_SPU_EXCHANGE","item.insert",spuId);
    }
//    繁杂的代码可以提取到类中
//    private void saveBaseAttr(SpuVo spuVo,Long spuId){
//        List<SpuAttrValueVo> baseAttrs = spuVo.getBaseAttrs();
//        if(!CollectionUtils.isEmpty(baseAttrs)){
//            List<SpuAttrValueEntity> spuAttrValueEntities = baseAttrs.stream().map(spuAttrValueVo -> {
//                spuAttrValueVo.setSpuId(spuId);
//                spuAttrValueVo.setSort(0);
//                return spuAttrValueVo;
//            }).collect(Collectors.toList());
//            this.baseService.saveBatch(spuAttrValueEntities);
//        }
//    }


}