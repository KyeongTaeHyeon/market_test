package com.jpa.market.service;

import com.jpa.market.dto.*;
import com.jpa.market.entity.Item;
import com.jpa.market.entity.ItemImg;
import com.jpa.market.mapper.ItemMapper;
import com.jpa.market.repository.ItemImgRepository;
import com.jpa.market.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemMapper itemMapper;
    private final ItemImgRepository itemImgRepository;
    private final FileService fileService;

    @Value("${file.upload.itemImgLocation}")
    private String itemImgLocation;

    // 상품 등록
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 이미지가 없는 경우 예외 발생
        if(itemImgFileList == null || itemImgFileList.isEmpty() || itemImgFileList.get(0).isEmpty()) {
            throw new IllegalArgumentException("첫 번째 상품 이미지는 필수입니다.");
        }

        // 상품 등록(dto를 엔티티로 변환)
        Item item = itemMapper.dtoToEntity(itemFormDto);
        itemRepository.save(item);

        // 이미지를 등록
        for (int i = 0; i < itemImgFileList.size(); i++) {
            MultipartFile itemImgFile = itemImgFileList.get(i);

            if(itemImgFile != null && !itemImgFile.isEmpty()){
                ItemImg itemImg = ItemImg.builder().item(item).repImgYn(i==0? "Y" : "N").build();

                itemImgService.saveItemImg(itemImg, itemImgFile);
            }
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public ItemFormDto getItemDetail(Long itemId){
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();

        for(ItemImg itemImg : itemImgList){
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        Item item = itemRepository.findById(itemId).orElseThrow(()-> new EntityNotFoundException("해당 상품을 찾을 수 없습니다."));
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        // 상품 수정
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다."));
        item.updateItem(itemFormDto);

        // 이미지 수정
        List<Long> itemImgIds = itemFormDto.getItemImgIds();
        for (int i = 0; i < itemImgFileList.size(); i++) {
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }

        return item.getId();
    }

    public void deleteItem(Long itemId) throws Exception {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("해당 상품을 찾을 수 없습니다."));

        // 상품과 연관된 이미지 파일들을 먼저 삭제
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        for (ItemImg itemImg : itemImgList) {
            if (StringUtils.hasText(itemImg.getImgName())) {
                fileService.deleteFile(itemImgLocation + "/" + itemImg.getImgName());
            }
        }
        // 상품 삭제 (ItemImg는 cascade = ALL, orphanRemoval = true 설정으로 함께 삭제됨)
        itemRepository.delete(item);
    }

    @Transactional(readOnly = true)
    public Page<ItemAdminListDto> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getmainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }
}
