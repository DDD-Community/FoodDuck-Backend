package com.foodduck.foodduck.account.repository

import com.foodduck.foodduck.account.model.Reason
import org.springframework.data.jpa.repository.JpaRepository

interface ReasonRepository: JpaRepository<Reason, Long> {
}