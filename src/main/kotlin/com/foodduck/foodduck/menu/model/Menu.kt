package com.foodduck.foodduck.menu.model

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.base.domain.BaseEntity
import javax.persistence.*

@Entity
class Menu(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_ID")
    val id: Long? = null,

    @Column(name = "TITLE")
    var title: String,

    @Column(columnDefinition = "TEXT", name="BODY")
    var body: String,

    @Column(name = "URL")
    var url: String,

    var favorCount: Long,

    @OneToMany(mappedBy = "menu")
    val tagMenu: MutableList<TagMenu> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    val account: Account
):BaseEntity() {
    fun changeFavorCount(count: Long) {
        this.favorCount += count
    }

    fun updateMenu(title: String, body: String, image: String) {
        this.title = title
        this.body = body
        this.url = image
    }
}