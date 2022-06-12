package com.foodduck.foodduck.menu.service

import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.menu.dto.MenuCreateRequest
import com.foodduck.foodduck.menu.repository.MenuRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MenuService(
    private val menuRepository: MenuRepository
) {

    fun postMenu(account: Account, request: MenuCreateRequest) {
        // image s3 upload and get url
        // check tag exists already
        // if exists pass else create
        // make TagMenu
        // response 200 OK
    }

    fun clickFavor(account: Account, menuId: Long) {
        // find favorMenu account and menu mapping
        // if not exists
        //      menu favor increase count
        //      create favorMenu account
        // else
        //      menu favor decrease count
        //      delete favorMenu account
        // response 200 OK
    }

    fun detailMenu(account: Account, menuId: Long) {
        // if account is not null
        //      create menuHistory
        // else
        //      pass
        //
    }

    fun listMenu(tagId:Long?) {
        // queryDsl dynamic Query
    }


}