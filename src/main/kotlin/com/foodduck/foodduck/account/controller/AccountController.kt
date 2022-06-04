package com.foodduck.foodduck.account.controller

import com.foodduck.foodduck.account.dto.AccountChangePasswordRequest
import com.foodduck.foodduck.account.dto.AccountLoginRequest
import com.foodduck.foodduck.account.dto.AccountSignUpRequest
import com.foodduck.foodduck.account.model.Account
import com.foodduck.foodduck.account.model.AuthAccount
import com.foodduck.foodduck.account.service.AccountService
import com.foodduck.foodduck.base.config.security.token.TokenDto
import com.foodduck.foodduck.base.message.MessageCode
import com.foodduck.foodduck.base.message.response.SimpleResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/v1/accounts")
class AccountController(
    private val accountService: AccountService
) {

    @PostMapping("/sign-up")
    fun signUp(@RequestBody request: AccountSignUpRequest): ResponseEntity<SimpleResponse<TokenDto>> {
        val tokenDto = accountService.signUp(request)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.CREATED, MessageCode.SIGN_UP_SUCCESS, tokenDto))
    }

    @DeleteMapping("/sign-out")
    fun signOut(@AuthAccount account: Account): ResponseEntity<SimpleResponse<Unit>> {
        accountService.signOut(account)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SIGN_OUT))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: AccountLoginRequest): ResponseEntity<SimpleResponse<TokenDto>> {
        val tokenDto = accountService.login(request)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.LOGIN_SUCCESS, tokenDto))
    }

    @GetMapping("/logout")
    fun logout(@AuthAccount account: Account, request: HttpServletRequest): ResponseEntity<SimpleResponse<Unit>> {
        accountService.logout(account, request)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.CREATED, MessageCode.LOGOUT_SUCCESS))
    }

    @GetMapping
    fun checkNickname(@RequestParam nickname: String): ResponseEntity<SimpleResponse<Unit>> {
        accountService.checkNickname(nickname)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK))
    }

    @GetMapping("/refresh")
    fun reIssueToken(@RequestParam email: String, @RequestParam("refresh-token") refreshToken: String): ResponseEntity<SimpleResponse<TokenDto>> {
        val tokenDto = accountService.reIssueToken(email, refreshToken)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, tokenDto))
    }

    @GetMapping("/send")
    fun sendTempAuthenticateNumber(@RequestParam email: String): ResponseEntity<SimpleResponse<Unit>> {
        accountService.sendTempAuthenticateNumber(email)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SEND_SUCCESS))
    }

    @GetMapping("/compare")
    fun compareAuthenticateNumber(@RequestParam email:String, @RequestParam number: String): ResponseEntity<SimpleResponse<Unit>> {
        accountService.compareAuthenticateNumber(email, number)
        // TODO: accessToken 과 refreshToken 발급 필요 -> 그 이유는 비밀번호 바꿀 때 해당 사람이 맞는 지 확인해야 하는 상황
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.OK))
    }

    @PatchMapping("/{email}/password")
    fun changePassword(@PathVariable email: String, @RequestBody request: AccountChangePasswordRequest): ResponseEntity<SimpleResponse<Unit>> {
        accountService.changePassword(email, request)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.OK))
    }

}