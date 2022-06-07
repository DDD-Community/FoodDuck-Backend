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
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("api/v1/accounts")
@Api(tags = ["회원 및 토큰 관리"])
class AccountController(
    private val accountService: AccountService
) {

    @ApiOperation(value = "회원가입")
    @PostMapping("/sign-up")
    fun signUp(@RequestBody request: AccountSignUpRequest): ResponseEntity<SimpleResponse<TokenDto>> {
        val tokenDto = accountService.signUp(request)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.CREATED, MessageCode.SIGN_UP_SUCCESS, tokenDto))
    }

    @ApiOperation(value = "회원탈퇴")
    @DeleteMapping("/sign-out")
    fun signOut(@AuthAccount account: Account): ResponseEntity<SimpleResponse<Unit>> {
        accountService.signOut(account)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SIGN_OUT))
    }

    @ApiOperation(value = "로그인")
    @PostMapping("/login")
    fun login(@RequestBody request: AccountLoginRequest): ResponseEntity<SimpleResponse<TokenDto>> {
        val tokenDto = accountService.login(request)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.LOGIN_SUCCESS, tokenDto))
    }

    @ApiOperation(value = "로그아웃")
    @GetMapping("/logout")
    fun logout(@AuthAccount account: Account, request: HttpServletRequest): ResponseEntity<SimpleResponse<Unit>> {
        accountService.logout(account, request)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.CREATED, MessageCode.LOGOUT_SUCCESS))
    }

    @ApiOperation(value = "닉네임 이미 존재하는 지 확인하기")
    @GetMapping
    fun checkNickname(@RequestParam nickname: String): ResponseEntity<SimpleResponse<Unit>> {
        accountService.checkNickname(nickname)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK))
    }

    @ApiOperation(value = "토큰 갱신")
    @GetMapping("/refresh")
    fun reIssueToken(@RequestParam email: String, @RequestParam("refresh-token") refreshToken: String): ResponseEntity<SimpleResponse<TokenDto>> {
        val tokenDto = accountService.reIssueToken(email, refreshToken)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SELECT_OK, tokenDto))
    }

    @ApiOperation(value = "인증번호 이메일 발송")
    @GetMapping("/send")
    fun sendTempAuthenticateNumber(@RequestParam email: String): ResponseEntity<SimpleResponse<Unit>> {
        accountService.sendTempAuthenticateNumber(email)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.SEND_SUCCESS))
    }

    @ApiOperation(value = "인증번호 비교하기")
    @GetMapping("/compare")
    fun compareAuthenticateNumber(@RequestParam email:String, @RequestParam number: String): ResponseEntity<SimpleResponse<Unit>> {
        accountService.compareAuthenticateNumber(email, number)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.OK))
    }

    @ApiOperation(value = "비밀번호 바꾸기")
    @PatchMapping("/{email}/password")
    fun changePassword(@PathVariable email: String, @RequestBody request: AccountChangePasswordRequest): ResponseEntity<SimpleResponse<Unit>> {
        accountService.changePassword(email, request)
        return ResponseEntity.ok(SimpleResponse.of(HttpStatus.OK, MessageCode.OK))
    }

}