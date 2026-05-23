package contracts.user

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method GET()
        headers {
            accept "application/json"
        }
        url("/api/v1/users") {
            queryParameters {
                parameter("size", value(stub(optional(anyNumber())), test(10)))
                parameter("number", value(stub(optional(anyNumber())), test(0)))
            }
        }
        response {
            status 200
            headers {
                contentType "application/json"
            }
            body([
                    size: fromRequest().query("size"),
                    number: 0,
                    totalElements: 2,
                    totalPages: 1,
                    content: [
                            [
                                    id          : fromRequest().path(3),
                                    name        : "John Doe",
                                    email       : "john.doe@email.com",
                                    type        : "MANAGER",
                                    enabled     : true
                            ],
                            [
                                    id          : fromRequest().path(3),
                                    name        : "Silvester Stalone",
                                    email       : "silvester.stalone@email.com",
                                    type        : "OPERATOR",
                                    enabled     : true
                            ]
                    ]
            ])
        }
    }
}