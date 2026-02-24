package com.jpa.market.service;

import com.jpa.market.entity.Item;
import com.jpa.market.entity.ItemImg;
import com.jpa.market.repository.ItemImgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor // 주요 필드에 대한 생성자를 자동으로 만들어주는 어노테이션
public class ItemImgService {

    private final ItemImgRepository itemImgRepository;

    private final FileService fileService;
    @Value("${file.upload.itemImgLocation}")
    private String itemImgLocation;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        if (StringUtils.hasText(oriImgName)) {
            imgUrl = fileService.uploadFile("items", oriImgName, itemImgFile.getBytes());
            if (imgUrl.contains(".com/")) {
                imgName = imgUrl.substring(imgUrl.lastIndexOf(".com/") + 5);
            }
        }

        itemImg.updateItemImg(imgName, oriImgName, imgUrl, itemImg.getRepImgYn());
        itemImgRepository.save(itemImg);
    }

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception {
        if (!itemImgFile.isEmpty()) {
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(() -> new EntityNotFoundException("상품 이미지 정보를 찾을 수 없습니다."));

            // 기존 이미지 파일 삭제
            if (StringUtils.hasText(savedItemImg.getImgName())) {
                fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            String imgUrl = "/img/item/" + imgName;

            // DB에 저장된 이미지 정보 업데이트
            savedItemImg.updateItemImg(imgName, oriImgName, imgUrl, savedItemImg.getRepImgYn());
            // itemImgRepository.save(savedItemImg); // @Transactional 어노테이션으로 인해 변경감지(dirty checking)가 동작하여 save 호출 필요 없음
        }
    }

    // 이미지삭제
    public void deleteItemImg(Item item) throws Exception {
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(item.getId());

        for(ItemImg itemimg : itemImgList) {

            String s3Key = itemimg.getImgName();

            if(!StringUtils.isEmpty(s3Key)){
                fileService.deleteFile(s3Key);
            }

            itemImgRepository.delete(itemimg);
        }
        itemImgRepository.flush();
    }
}
