package com.inhouse.yoursell.dto

import com.inhouse.yoursell.entity.Role
import com.inhouse.yoursell.entity.User
import java.util.stream.Collectors

fun Role.toDto(): RoleDto{
    return RoleDto(
        name = name
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        username = username,
        email = email,
        userRoles = userRoles.stream().map(Role::toDto).collect(Collectors.toSet())
    )
}