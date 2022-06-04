package com.foodduck.foodduck.account.model

import com.foodduck.foodduck.base.domain.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ACCOUNT_ID")
    val id: Long? = null,

    @Column(name = "NICKNAME")
    var nickname: String,

    @Column(name = "EMAIL")
    var email: String,

    @Column(name = "PASSWORD")
    var password: String,

    @Column(name = "LAST_LOGIN")
    var lastLogin: LocalDateTime = LocalDateTime.now()

): BaseEntity() {

    @PostLoad
    fun postLoad() {
        lastLogin = LocalDateTime.now()
    }

    fun changePassword(password: String) {
        this.password = password
    }

}