package com.foodduck.foodduck.account.model

import com.foodduck.foodduck.base.domain.BaseEntity
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name= "ACCOUNT")
class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ACCOUNT_ID")
    val id: Long? = null,

    var nickname: String,

    var password: String,

    var lastLogin: LocalDateTime

): BaseEntity() {


}