package contracts.user

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept 'application/json'
        }
        url("/api/v1/users/019d7764-3b02-7be2-9112-039fda30e965")
    }
    response {
        status 200
        headers {
            contentType 'application/json'
        }
        body([
                id          : fromRequest().path(3),
                name        : "John Doe",
                email       : "john.doe@email.com",
                type        : "MANAGER",
                enabled     : true
        ])
    }
}