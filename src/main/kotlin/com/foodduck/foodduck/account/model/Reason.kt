package com.foodduck.foodduck.account.model

import javax.persistence.*

@Entity
class Reason(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REASON_ID")
    var id: Long? = null,

    val reason: String,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    val account: Account
)