package com.inhouse.yoursell.config

import com.inhouse.yoursell.entity.user.User
import org.springframework.security.core.Authentication


fun Authentication.toUser(): User {
    return principal as User
}