package com.foodduck.foodduck.menu.model

import com.foodduck.foodduck.base.domain.BaseEntity
import javax.persistence.*

@Entity
class Favor(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "FAVOR_ID")
    val id: Long? = null,

    @Column(name = "COUNT")
    val count: Long
    ):BaseEntity() {

}