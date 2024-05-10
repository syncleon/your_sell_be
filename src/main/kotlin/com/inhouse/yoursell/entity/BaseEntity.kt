package com.inhouse.yoursell.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

@MappedSuperclass
open class BaseEntity {
    @CreatedDate
    val created: Long = System.currentTimeMillis()
    @LastModifiedDate
    val updated: Long = System.currentTimeMillis()
}