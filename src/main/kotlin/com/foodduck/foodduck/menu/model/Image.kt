package com.foodduck.foodduck.menu.model

import com.foodduck.foodduck.base.domain.BaseEntity
import javax.persistence.*

@Entity
class Image(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "IMAGE_ID")
    val id:Long? = null,

    @Column(name = "URL")
    val url: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "MENU_ID")
    val menu: Menu
):BaseEntity() {

}