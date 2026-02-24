package com.jpa.market.controller;

import com.jpa.market.dto.ItemAdminListDto;
import com.jpa.market.dto.ItemFormDto;
import com.jpa.market.dto.ItemSearchDto;
import com.jpa.market.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // 상품등록
    @PostMapping("/admin/item/new")
    public ResponseEntity<Long> itemNew(@Valid @RequestPart("itemCreateDto") ItemFormDto itemFormDto,
                                                          @RequestPart("itemImgFile") List<MultipartFile> itemImgFileList) throws Exception {
        Long itemId = itemService.saveItem(itemFormDto, itemImgFileList);
        return ResponseEntity.ok(itemId);
    }

    @GetMapping(value={"/admin/items", "/admin/items/{page}"})
    public ResponseEntity<Page<ItemAdminListDto>> itemManage(ItemSearchDto itemSearchDto,
                                                             @PathVariable("page") Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
        Page<ItemAdminListDto> items = itemService.getAdminItemPage(itemSearchDto, pageable);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<?> getItemDetail(@PathVariable("itemId") Long itemId) {
        ItemFormDto itemFormDto = itemService.getItemDetail(itemId);

        return ResponseEntity.ok(itemFormDto);
    }

    @PostMapping("/admin/items/{itemId}")
    public ResponseEntity<?> updateItem(@PathVariable("itemId") Long itemId,
                                        @Valid @RequestPart("itemFormDto") ItemFormDto itemFormDto,
                                        @RequestPart("itemImgFileList") List<MultipartFile> itemImgFileList) throws Exception {
        itemFormDto.setId(itemId);
        itemService.updateItem(itemFormDto, itemImgFileList);
        return ResponseEntity.ok(Map.of("message", "상품이 성공적으로 수정되었습니다."));
    }

    @DeleteMapping("/admin/items/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable("itemId") Long itemId) throws Exception {
        itemService.deleteItem(itemId);
        return ResponseEntity.ok(Map.of("message", "상품이 성공적으로 삭제되었습니다."));
    }
    
    
}
