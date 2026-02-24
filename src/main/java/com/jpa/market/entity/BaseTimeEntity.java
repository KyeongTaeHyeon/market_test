package com.jpa.market.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// 엔티티 생명주기를 감시하는 역할
@EntityListeners(value = {AuditingEntityListener.class})
@MappedSuperclass // 테이블로 만들어지지 않는 부모 엔티티
@Getter // 값을 직접 지정하지 않으므로 Getter만 설정
public abstract class BaseTimeEntity {
    @CreatedDate // 엔티티가 생성될때의 시간을 감지
    @Column(updatable = false) // 한 번 저장한 이후에는 수정할 수 없도록 제한
    private LocalDateTime regTime;

    @LastModifiedDate // 엔티티가 변경될 때마다 시간을 감지
    private LocalDateTime updateTime;
}
