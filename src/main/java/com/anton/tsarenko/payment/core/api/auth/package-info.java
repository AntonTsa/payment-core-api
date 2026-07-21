/**
 * Module, responsible for authentication and authorization of users in the payment system.
 */
@ApplicationModule(
        allowedDependencies = {
                "shared :: api"
        }
)
package com.anton.tsarenko.payment.core.api.auth;

import org.springframework.modulith.ApplicationModule;
