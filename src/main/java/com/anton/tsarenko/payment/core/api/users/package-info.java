/**
 * Module for user-related functionality in the payment system.
 */
@ApplicationModule(
        allowedDependencies = {
                "shared :: api"
        }
)

package com.anton.tsarenko.payment.core.api.users;

import org.springframework.modulith.ApplicationModule;
