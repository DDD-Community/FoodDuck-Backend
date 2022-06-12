package com.foodduck.foodduck.menu.model

import com.foodduck.foodduck.base.domain.BaseEntity
import javax.persistence.*

@Entity
class Menu(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "MENU_ID")
    val id: Long? = null,

    @Column(name = "TITLE")
    val title: String,

    @Column(columnDefinition = "TEXT", name="BODY")
    val body: String,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FAVOR_ID")
    val favor: Favor
):BaseEntity() {
}