/**
 * Module, responsible for account management in Payment Core API.
 */
@ApplicationModule(
        allowedDependencies = {
                "shared :: api"
        }
)
package com.anton.tsarenko.payment.core.api.accounts;

import org.springframework.modulith.ApplicationModule;
