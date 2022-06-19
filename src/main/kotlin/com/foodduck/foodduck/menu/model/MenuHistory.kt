package com.foodduck.foodduck.menu.model

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.base.domain.BaseEntity
import javax.persistence.*

@Entity
class MenuHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_HISTORY_ID")
    val id:Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    val account: Account,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MENU_ID")
    val menu: Menu
):BaseEntity() {

}