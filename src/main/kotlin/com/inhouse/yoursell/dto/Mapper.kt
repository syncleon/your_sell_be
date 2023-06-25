package com.inhouse.yoursell.dto

import com.inhouse.yoursell.entity.user.Role
import com.inhouse.yoursell.entity.user.User
import com.inhouse.yoursell.entity.vehicle.Vehicle
import com.inhouse.yoursell.entity.vehicle.enums.EDrivetrain
import com.inhouse.yoursell.entity.vehicle.enums.EModel
import com.inhouse.yoursell.entity.vehicle.enums.ETransmission
import java.util.stream.Collectors

fun Role.toDto(): RoleDto{
    return RoleDto(
        name = name
    )
}


fun User.toDto(): UserDto {
    return UserDto(
        id,
        username,
        email,
        userRoles.stream().map(Role::toDto).collect(Collectors.toSet()),
        vehicles.stream().map(Vehicle::toDto).collect(Collectors.toList())
    )
}

fun Vehicle.toDto(): VehicleDto{
    return VehicleDto(
        id,
        producer,
        model,
        mileage,
        vin,
        year,
        engine,
        drivetrain,
        transmission,
        bodyStyle,
        exteriorColor,
        interiorColor,
        sellerType,
        highlights,
        expectedBid,
        damaged
    )
}
