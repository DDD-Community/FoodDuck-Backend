package com.foodduck.foodduck.menu.model

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.base.domain.BaseEntity
import javax.persistence.*

@Entity
class FavorMenu(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "FAVOR_MENU_ID")
    val id:Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    val account: Account,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MENU_ID")
    val menu: Menu
):BaseEntity() {
}