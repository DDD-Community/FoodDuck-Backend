package com.foodduck.foodduck.menu.model

import com.foodduck.foodduck.base.domain.BaseEntity
import javax.persistence.*

@Entity
class TagMenu(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "TAG_MENU_ID")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAG_ID")
    val tag: Tag,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MENU_ID")
    val menu: Menu
): BaseEntity() {

}