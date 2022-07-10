package com.foodduck.foodduck.menu.repository

import com.foodduck.foodduck.account.repository.AccountRepository
import com.foodduck.foodduck.base.config.TestConfig
import com.foodduck.foodduck.base.config.domain.EntityFactory
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@DataJpaTest
@Import(TestConfig::class)
@Transactional
class CommentRepositoryTest {
    @Autowired
    lateinit var menuRepository: MenuRepository

    @Autowired
    lateinit var commentRepository: CommentRepository

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Test
    fun `댓글 조회 첫번째 깊이`() {
        val account = EntityFactory.accountTemplateForReal()
        val menu = EntityFactory.menuTemplateForReal(account = account)
        val parent = EntityFactory.commentTemplateForReal(account, menu, null)
        val comments = listOf(
            EntityFactory.commentTemplateForReal(account, menu, parent),
            EntityFactory.commentTemplateForReal(account, menu, parent),
            EntityFactory.commentTemplateForReal(account, menu, null),
            EntityFactory.commentTemplateForReal(account, menu, null),
            EntityFactory.commentTemplateForReal(account, menu, null, body = "체크"),
        )
        val saveAccount = accountRepository.save(account)
        val saveMenu = menuRepository.save(menu)
        val saveParent = commentRepository.save(parent)
        val saveComments = comments.map { commentRepository.save(it) }.toList()
        val findComments = saveMenu.id?.let { commentRepository.findFirstDepthCommentList(menuId = it, lastId = null, pageSize = 10L) }
        assertThat(findComments).isNotNull
        assertThat(findComments!!.size).isEqualTo(4)
        val findFirstCommentVo = findComments[0]
        assertThat(findFirstCommentVo.commentBody).isEqualTo("체크")
        assertThat(findFirstCommentVo.commentCreatedAt).isEqualTo(saveComments.last().createdAt)
        assertThat(findFirstCommentVo.accountId).isEqualTo(saveAccount.id)
        assertThat(findFirstCommentVo.accountName).isEqualTo(saveAccount.nickname)
    }

    @Test
    fun `댓글 조회 두번째 깊이`() {
        val account = EntityFactory.accountTemplateForReal()
        val menu = EntityFactory.menuTemplateForReal(account = account)
        val parent = EntityFactory.commentTemplateForReal(account, menu, null)
        val comments = listOf(
            EntityFactory.commentTemplateForReal(account, menu, parent),
            EntityFactory.commentTemplateForReal(account, menu, parent, body = "부모체크"),
            EntityFactory.commentTemplateForReal(account, menu, null),
            EntityFactory.commentTemplateForReal(account, menu, null),
            EntityFactory.commentTemplateForReal(account, menu, null, body = "체크"),
        )
        val saveAccount = accountRepository.save(account)
        val saveMenu = menuRepository.save(menu)
        val saveParent = commentRepository.save(parent)
        val childComments = comments.map { commentRepository.save(it) }.filter { it.parent!=null }.toList()
        val findComments = saveParent.id?.let { saveMenu.id?.let { it1 -> commentRepository.findSecondDepthCommentList(it, it1, null, 10L) } }
        assertThat(findComments).isNotNull
        assertThat(findComments!!.size).isEqualTo(2)
        val findFirstCommentVo = findComments[0]
        assertThat(findFirstCommentVo.commentBody).isEqualTo("부모체크")
        assertThat(findFirstCommentVo.commentCreatedAt).isEqualTo(childComments.last().createdAt)
        assertThat(findFirstCommentVo.accountId).isEqualTo(saveAccount.id)
        assertThat(findFirstCommentVo.accountName).isEqualTo(saveAccount.nickname)
    }
}