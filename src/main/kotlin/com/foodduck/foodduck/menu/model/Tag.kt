package com.foodduck.foodduck.menu.model

import com.foodduck.foodduck.base.domain.BaseEntity
import javax.persistence.*

@Entity
class Tag(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TAG_ID")
    val id:Long? = null,

    @Column(name = "TITLE")
    val title:String
):BaseEntity() {

}