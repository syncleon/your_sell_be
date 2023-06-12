package com.inhouse.yoursell.entity

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

@MappedSuperclass
open class BaseEntity {
    @CreatedDate
    @Column(name = "created", nullable = false)
    private val created: Long = System.currentTimeMillis()
    @LastModifiedDate
    @Column(name = "updated", nullable = false)
    private val updated: Long = System.currentTimeMillis()
}