package com.foodduck.foodduck.account.model

import com.foodduck.foodduck.base.domain.BaseEntity
import io.swagger.annotations.ApiModelProperty
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNT_ID")
    @ApiModelProperty(hidden = true)
    val id: Long? = null,

    @Column(name = "NICKNAME")
    @ApiModelProperty(hidden = true)
    var nickname: String,

    @Column(name = "EMAIL")
    @ApiModelProperty(hidden = true)
    var email: String,

    @Column(name = "PASSWORD")
    @ApiModelProperty(hidden = true)
    var password: String,

    @Column(name = "PROFILE")
    @ApiModelProperty(hidden = true)
    var profile: String,

    @Column(name = "LAST_LOGIN")
    @ApiModelProperty(hidden = true)
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