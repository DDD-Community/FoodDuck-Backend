package com.foodduck.foodduck.base.domain

import io.swagger.annotations.ApiModelProperty
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(value = [AuditingEntityListener::class])
abstract class BaseEntity {
    @CreatedDate
    @Column(name = "CREATED_AT", updatable = false)
    @ApiModelProperty(hidden = true)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    @Column(name = "MODIFIED_AT")
    @ApiModelProperty(hidden = true)
    var modifiedAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "IS_DELETE")
    @ApiModelProperty(hidden = true)
    var delete: Boolean = false

    fun remove() {
        this.delete = true
    }
}