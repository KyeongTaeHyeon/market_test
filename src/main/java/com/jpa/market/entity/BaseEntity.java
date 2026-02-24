package com.jpa.market.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// 엔티티 생명주기를 감시하는 역할
@EntityListeners(value = {AuditingEntityListener.class})
@MappedSuperclass // 테이블로 만들어지지 않는 부모 엔티티
@Getter // 값을 직접 지정하지 않으므로 Getter만 설정
public abstract class BaseEntity extends BaseTimeEntity{
    // 등록자, 수정자
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String modifiedBy;
}
