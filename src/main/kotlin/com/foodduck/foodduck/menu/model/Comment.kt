package com.foodduck.foodduck.menu.model

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.base.domain.BaseEntity
import javax.persistence.*

@Entity
class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    val id: Long? = null,

    @Column(name= "BODY_ID")
    var body: String,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    val account: Account,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MENU_ID")
    val menu: Menu,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMMENT_PARENT_ID")
    var parent: Comment? = null,

    @OneToMany(mappedBy = "parent")
    val child: MutableList<Comment> = mutableListOf()

): BaseEntity() {

    fun addChildComment(comment: Comment) {
        child.add(comment)
        comment.parent = this
    }
}