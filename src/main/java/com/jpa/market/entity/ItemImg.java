package com.jpa.market.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="item_img")
@Getter
@ToString
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor // Builder 패턴 사용 시 필요
public class ItemImg extends BaseEntity {
    @Id
    @Column(name="item_img_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="item_img_name")
    private String imgName; // 카멜 케이스로 변경

    @Column(name="ori_img_name") // DB 컬럼명 매핑
    private String oriImgName;

    @Column(name="img_url")
    private String imgUrl;

    @Column(name="rep_img_yn")
    private String repImgYn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    public ItemImg updateItemImg(String imgName, String oriImgName, String imgUrl, String repImgYn){
        ItemImg itemImg = new ItemImg();

        itemImg.imgName = imgName;
        itemImg.oriImgName = oriImgName;
        itemImg.imgUrl = imgUrl;
        itemImg.repImgYn = repImgYn;

        return itemImg;
    }
}
